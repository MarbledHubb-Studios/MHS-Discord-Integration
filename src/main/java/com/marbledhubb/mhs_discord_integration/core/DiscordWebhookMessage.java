package com.marbledhubb.mhs_discord_integration.core;

import com.google.gson.annotations.SerializedName;
import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;

import java.util.ArrayList;
import java.util.List;

public class DiscordWebhookMessage {

    private String content;
    private String username;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private boolean tts;
    private List<DiscordEmbed> embeds;
    @SerializedName("allowed_mentions")
    private AllowedMentions allowedMentions;

    // Fancy new word I learned just for this, from my understanding transient won't serialize the value of threadId,
    // but will give it its default value on deserialization. Aka its not persistent
    private transient String threadId;

    public static Builder builder() {
        return new Builder();
    }

    public String getThreadId() {
        return threadId;
    }

    public static class Builder {
        private final DiscordWebhookMessage message = new DiscordWebhookMessage();

        public Builder content(String content) {
            if (content != null && content.length() > 2000) {
                throw new IllegalStateException("Message content cannot exceed 2000 characters.");
            }
            message.content = content;
            return this;
        }

        public Builder username(String username) {
            message.username = username;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            message.avatarUrl = avatarUrl;
            return this;
        }

        // This yells out messages, fun sometimes but lets not lol
        public Builder tts(boolean tts) {
            message.tts = tts;
            return this;
        }

        // See the code of AllowedMentions for the explanation
        public Builder allowedMentions(AllowedMentions allowedMentions) {
            message.allowedMentions = allowedMentions;
            return this;
        }

        public Builder addEmbed(DiscordEmbed embed) {
            if (message.embeds == null) {
                message.embeds = new ArrayList<>();
            }
            if (message.embeds.size() >= 10) {
                throw new IllegalStateException("A single webhook message supports a maximum of 10 embeds.");
            }
            message.embeds.add(embed);
            return this;
        }

        // This is used if the logs may have a thread or forum for different things, we can make the webhook send there instead
        public Builder thread(String threadId) {
            message.threadId = threadId;
            return this;
        }

        public DiscordWebhookMessage build() {
            if (message.content == null && (message.embeds == null || message.embeds.isEmpty())) {
                throw new IllegalStateException("A webhook message needs at least text content or one embed.");
            }
            return message;
        }
    }
}