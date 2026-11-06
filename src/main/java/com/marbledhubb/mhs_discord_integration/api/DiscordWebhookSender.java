package com.marbledhubb.mhs_discord_integration.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marbledhubb.mhs_discord_integration.MHSDiscordIntegration;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.GeneralConfigManager;
import com.marbledhubb.mhs_discord_integration.core.DiscordAttachment;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class DiscordWebhookSender {

    // Sender for the webhooks, unfortunately it's some ugly JSON requests
    // This is some adapted code I did some time ago. If you notice any improvements, go ahead!
    // ...if you're an MHS member that is. If not, hi.

    private static final Gson GSON = new GsonBuilder().create();

    private static final int MAX_FILES_PER_MESSAGE = 10;
    private static final long MAX_UPLOAD_BYTES = 25L * 1024 * 1024;
    private static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.UTF_8);

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Executor EXECUTOR = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "mhs-discord-integration"); // Runs off the main thread and in a new one
        thread.setDaemon(true);
        return thread;
    });

    public static CompletableFuture<Void> send(String webhookUrl, DiscordWebhookMessage message) {
        String url = withThread(webhookUrl, message);
        return postAsync(url, toJson(message)).thenAccept(DiscordWebhookSender::logIfFailed);
    }

    // Sends a message with files attached. The message may be null for a files-only upload.
    public static CompletableFuture<Void> sendWithFiles(String webhookUrl, DiscordWebhookMessage message, List<DiscordAttachment> files) {
        String url = message == null ? webhookUrl : withThread(webhookUrl, message);
        String json = message == null ? "{}" : toJson(message);
        return postMultipartAsync(url, json, files).thenAccept(DiscordWebhookSender::logIfFailed);
    }


    // Sends, but also saves the message ID in case we need to edit or delete it
    public static CompletableFuture<String> sendAndGetId(String webhookUrl, DiscordWebhookMessage message) {
        String url = withThread(webhookUrl, message) + (webhookUrl.contains("?") ? "&" : "?") + "wait=true";
        return postAsync(url, toJson(message)).thenApply(response -> {
            if (response.statusCode() / 100 != 2) {
                logIfFailed(response);
                return null;
            }
            JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
            return body.has("id") ? body.get("id").getAsString() : null;
        });
    }

    public static CompletableFuture<Void> edit(String webhookUrl, String messageId, DiscordWebhookMessage message) {
        HttpRequest request = baseRequest(webhookUrl + "/messages/" + messageId)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(toJson(message)))
                .build();
        return CompletableFuture.supplyAsync(() -> sendWithRetry(request, 0), EXECUTOR)
                .thenAccept(DiscordWebhookSender::logIfFailed);
    }

    public static CompletableFuture<Void> delete(String webhookUrl, String messageId) {
        HttpRequest request = baseRequest(webhookUrl + "/messages/" + messageId).DELETE().build();
        return CompletableFuture.supplyAsync(() -> sendWithRetry(request, 0), EXECUTOR)
                .thenAccept(DiscordWebhookSender::logIfFailed);
    }

    private static String withThread(String webhookUrl, DiscordWebhookMessage message) {
        if (message.getThreadId() == null) return webhookUrl;
        return webhookUrl + (webhookUrl.contains("?") ? "&" : "?") + "thread_id=" + message.getThreadId();
    }

    private static String toJson(DiscordWebhookMessage message) {
        return GSON.toJson(message);
    }

    private static CompletableFuture<HttpResponse<String>> postAsync(String url, String jsonBody) {
        HttpRequest request = baseRequest(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        return CompletableFuture.supplyAsync(() -> sendWithRetry(request, 0), EXECUTOR);
    }

    private static CompletableFuture<HttpResponse<String>> postMultipartAsync(String url, String jsonBody, List<DiscordAttachment> files) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest request;
            try {
                request = multipartRequest(url, jsonBody, files);
            } catch (RuntimeException e) {
                MHSDiscordIntegration.LOGGER.warn("Discord file upload not sent: {}", e.getMessage());
                throw e;
            }
            return sendWithRetry(request, 0);
        }, EXECUTOR);
    }

    private static HttpRequest multipartRequest(String url, String jsonBody, List<DiscordAttachment> files) {
        if (files.isEmpty() || files.size() > MAX_FILES_PER_MESSAGE) {
            throw new IllegalArgumentException("A message needs between 1 and " + MAX_FILES_PER_MESSAGE
                    + " files, got " + files.size());
        }

        String boundary = "mhs" + UUID.randomUUID().toString().replace("-", "");
        List<byte[]> body = new ArrayList<>();

        body.add(partHeader(boundary, "payload_json", null, "application/json"));
        body.add(jsonBody.getBytes(StandardCharsets.UTF_8));
        body.add(CRLF);

        long total = 0;
        for (int i = 0; i < files.size(); i++) {
            DiscordAttachment file = files.get(i);
            byte[] data;
            try {
                total += file.size();
                if (total > MAX_UPLOAD_BYTES) {
                    throw new IllegalArgumentException("Upload is larger than " + (MAX_UPLOAD_BYTES / 1024 / 1024)
                            + " MiB (stopped at " + file.filename() + ")");
                }
                data = file.load();
            } catch (IOException e) {
                throw new UncheckedIOException("Could not read " + file.filename(), e);
            }
            body.add(partHeader(boundary, "files[" + i + "]", file.filename(), file.contentType()));
            body.add(data);
            body.add(CRLF);
        }
        body.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArrays(body))
                .build();
    }

    private static byte[] partHeader(String boundary, String name, String filename, String contentType) {
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\""
                + (filename != null ? "; filename=\"" + filename + "\"" : "")
                + "\r\nContent-Type: " + contentType + "\r\n\r\n";
        return header.getBytes(StandardCharsets.UTF_8);
    }

    private static HttpResponse<String> sendWithRetry(HttpRequest request, int attempt) {
        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 429 && attempt < GeneralConfigManager.getInstance().getMaxRetries()) {
                double retryAfterSeconds = 1.0;
                try {
                    JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
                    if (body.has("retry_after")) {
                        retryAfterSeconds = body.get("retry_after").getAsDouble();
                    }
                } catch (Exception ignored) {
                    // Do nothing. Let's eat a cookie in the meantime
                }
                MHSDiscordIntegration.LOGGER.warn("Discord webhook rate limited, retrying in {}s", retryAfterSeconds);
                Thread.sleep((long) (retryAfterSeconds * 1000) + 50);
                return sendWithRetry(request, attempt + 1);
            }
            return response;
        } catch (Exception e) {
            MHSDiscordIntegration.LOGGER.error("Failed to reach Discord webhook", e);
            throw new RuntimeException(e);
        }
    }

    private static HttpRequest.Builder baseRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json");
    }

    private static void logIfFailed(HttpResponse<String> response) {
        if (response != null && response.statusCode() / 100 != 2) {
            MHSDiscordIntegration.LOGGER.warn("Discord webhook call failed ({}): {}", response.statusCode(), response.body());
        }
    }

}