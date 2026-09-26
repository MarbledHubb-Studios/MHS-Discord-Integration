package com.marbledhubb.mhs_discord_integration.configuration.config.core;

public class ConsoleLogEntry {

    public boolean enabled;
    public String level;
    public String channelReference;
    public String username;
    public String avatarUrl;

    public ConsoleLogEntry() {
    }

    public ConsoleLogEntry(boolean enabled, String level, String channelReference, String username, String avatarUrl) {
        this.enabled = enabled;
        this.channelReference = channelReference;
        this.level = level;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public static ConsoleLogEntry defaultEntry(String username) {
        return new ConsoleLogEntry(
                true,
                "WARN",
                "edit-me-or-i-wont-work",
                username,
                "https://example.com/example.png"
        );
    }
}