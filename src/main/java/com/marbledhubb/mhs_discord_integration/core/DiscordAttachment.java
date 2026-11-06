package com.marbledhubb.mhs_discord_integration.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DiscordAttachment {

    private final String filename;
    private final Path path;
    private final byte[] data;
    private final String contentType;

    private DiscordAttachment(String filename, Path path, byte[] data, String contentType) {
        this.filename = sanitize(filename);
        this.path = path;
        this.data = data;
        this.contentType = contentType;
    }

    public static DiscordAttachment of(Path path) {
        return of(path, path.getFileName().toString());
    }

    public static DiscordAttachment of(Path path, String filename) {
        return new DiscordAttachment(filename, path, null, null);
    }

    public static DiscordAttachment ofBytes(String filename, byte[] data) {
        return new DiscordAttachment(filename, null, data, null);
    }

    public static DiscordAttachment ofText(String filename, String text) {
        return new DiscordAttachment(filename, null, text.getBytes(StandardCharsets.UTF_8),
                "text/plain; charset=utf-8");
    }

    public String filename() {
        return filename;
    }

    public String contentType() {
        if (contentType != null) return contentType;
        try {
            String guessed = path != null ? Files.probeContentType(path) : null;
            return guessed != null ? guessed : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    public long size() throws IOException {
        return data != null ? data.length : Files.size(path);
    }

    public byte[] load() throws IOException {
        return data != null ? data : Files.readAllBytes(path);
    }

    private static String sanitize(String name) {
        String cleaned = name.replaceAll("[\\\\/\"\\r\\n]", "_").trim();
        return cleaned.isEmpty() ? "file" : cleaned;
    }
}