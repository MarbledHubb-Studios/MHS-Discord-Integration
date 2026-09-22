package com.marbledhubb.mhs_discord_integration.core;

import com.google.gson.annotations.SerializedName;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DiscordEmbed {

    private String title;
    private String description;
    private String url;
    private String timestamp;
    private Integer color;
    private Footer footer;
    private Image image;
    private Image thumbnail;
    private Author author;
    private List<Field> fields;

    public static Builder builder() {
        return new Builder();
    }

    public static class Footer {
        private String text;
        @SerializedName("icon_url")
        private String iconUrl;
    }

    public static class Image {
        private String url;
    }

    public static class Author {
        private String name;
        private String url;
        @SerializedName("icon_url")
        private String iconUrl;
    }

    public static class Field {
        private final String name;
        private final String value;
        private final boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }

    public static class Builder {
        private final DiscordEmbed embed = new DiscordEmbed();

        public Builder title(String title) {
            embed.title = title;
            return this;
        }

        public Builder description(String description) {
            embed.description = description;
            return this;
        }

        // To other folks reading, this sets only the title as url, not the whole embed
        public Builder url(String url) {
            embed.url = url;
            return this;
        }

        // This is not the readable timestamp!! Its the pc version of one
        public Builder timestampNow() {
            embed.timestamp = Instant.now().toString();
            return this;
        }

        public Builder timestamp(Instant instant) {
            embed.timestamp = instant.toString();
            return this;
        }

        // RGB has a lot more options than the built-in colors in discord.Color or equivalents
        public Builder color(int rgb) {
            embed.color = rgb & 0xFFFFFF;
            return this;
        }

        public Builder color(Color color) {
            return color(color.getRGB() & 0xFFFFFF);
        }

        // Same as RGB, but Hex instead
        public Builder color(String hex) {
            String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
            return color(Integer.parseInt(cleaned, 16));
        }

        public Builder footer(String text, String iconUrl) {
            Footer footer = new Footer();
            footer.text = text;
            footer.iconUrl = iconUrl;
            embed.footer = footer;
            return this;
        }

        public Builder footer(String text) {
            return footer(text, null);
        }

        // Images are shown at the bottom of the embed, not the side
        public Builder image(String url) {
            Image image = new Image();
            image.url = url;
            embed.image = image;
            return this;
        }

        // Now this is shown in the side. It's smaller though
        public Builder thumbnail(String url) {
            Image thumb = new Image();
            thumb.url = url;
            embed.thumbnail = thumb;
            return this;
        }

        // Shown on top
        public Builder author(String name, String url, String iconUrl) {
            Author author = new Author();
            author.name = name;
            author.url = url;
            author.iconUrl = iconUrl;
            embed.author = author;
            return this;
        }

        public Builder author(String name) {
            return author(name, null, null);
        }

        // Fields, so separate segments of the Embed. Max of 25, inline only has 3 per line
        public Builder addField(String name, String value, boolean inline) {
            if (embed.fields == null) {
                embed.fields = new ArrayList<>();
            }
            if (embed.fields.size() >= 25) {
                throw new IllegalStateException("Discord embeds support a maximum of 25 fields.");
            }
            embed.fields.add(new Field(name, value, inline));
            return this;
        }

        public Builder addField(String name, String value) {
            return addField(name, value, false);
        }

        public DiscordEmbed build() {
            if (embed.title != null && embed.title.length() > 256) {
                throw new IllegalStateException("Embed title cannot exceed 256 characters.");
            }
            if (embed.description != null && embed.description.length() > 4096) {
                throw new IllegalStateException("Embed description cannot exceed 4096 characters.");
            }
            return embed;
        }
    }
}