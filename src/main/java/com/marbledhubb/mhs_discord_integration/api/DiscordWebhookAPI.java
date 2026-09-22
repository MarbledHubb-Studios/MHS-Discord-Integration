package com.marbledhubb.mhs_discord_integration.api;

import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import com.marbledhubb.mhs_discord_integration.configuration.WebhookChannelManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class DiscordWebhookAPI {

    public static boolean hasChannel(String alias) {
        return WebhookChannelManager.getInstance().getUrl(alias).isPresent();
    }

    // Sends ONLY a normal message
    public static CompletableFuture<Void> sendMessage(String channelAlias, String content) {
        return sendMessage(channelAlias, DiscordWebhookMessage.builder().content(content).build());
    }

    // Sends ONLY one embed
    public static CompletableFuture<Void> sendEmbed(String channelAlias, DiscordEmbed embed) {
        return sendMessage(channelAlias, DiscordWebhookMessage.builder().addEmbed(embed).build());
    }

    // Our normal sending thing that sends a message with text and up to 10 embeds
    public static CompletableFuture<Void> sendMessage(String channelAlias, DiscordWebhookMessage message) {
        return resolve(channelAlias).thenComposeSuccess(url -> DiscordWebhookSender.send(url, message));
    }

    // If you need further help with these, should check the DiscordWebhookSender class.
    public static CompletableFuture<String> sendMessageAndGetId(String channelAlias, DiscordWebhookMessage message) {
        return resolve(channelAlias).thenComposeSuccess(url -> DiscordWebhookSender.sendAndGetId(url, message));
    }

    public static CompletableFuture<Void> editMessage(String channelAlias, String messageId, DiscordWebhookMessage message) {
        return resolve(channelAlias).thenComposeSuccess(url -> DiscordWebhookSender.edit(url, messageId, message));
    }

    public static CompletableFuture<Void> deleteMessage(String channelAlias, String messageId) {
        return resolve(channelAlias).thenComposeSuccess(url -> DiscordWebhookSender.delete(url, messageId));
    }

    private static ResolvedUrl resolve(String channelAlias) {
        Optional<String> url = WebhookChannelManager.getInstance().getUrl(channelAlias);
        return new ResolvedUrl(url, channelAlias);
    }

    private record ResolvedUrl(Optional<String> url, String alias) {
        <T> CompletableFuture<T> thenComposeSuccess(java.util.function.Function<String, CompletableFuture<T>> action) {
            if (url.isEmpty()) {
                return CompletableFuture.failedFuture(
                        new IllegalArgumentException("No webhook registered under alias '" + alias + "'. "
                                + "Add this entry in the file: " + alias + " <url>"));
            }
            return action.apply(url.get());
        }
    }
}