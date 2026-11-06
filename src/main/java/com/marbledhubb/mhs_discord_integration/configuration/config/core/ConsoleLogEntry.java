package com.marbledhubb.mhs_discord_integration.configuration.config.core;

public class ConsoleLogEntry {

    public boolean enabled;
    public String level;
    public String channelReference;
    public String username;
    public String avatarUrl;
    public boolean sendNotificationOnFatal;
    public String fatalNotification;
    public boolean uploadLog;

    public ConsoleLogEntry() {
    }

    public ConsoleLogEntry(boolean enabled, String level, String channelReference, String username, String avatarUrl, boolean sendNotificationOnFatal, String fatalNotification, boolean uploadLog) {
        this.enabled = enabled;
        this.channelReference = channelReference;
        this.level = level;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.sendNotificationOnFatal = sendNotificationOnFatal;
        this.fatalNotification = fatalNotification;
        this.uploadLog = uploadLog;
    }

    public static ConsoleLogEntry defaultEntry(String username) {
        return new ConsoleLogEntry(
                true,
                "WARN",
                "edit-me-or-i-wont-work",
                username,
                "default",
                true,
                "@TechTeam **The server has received FATAL errors, pinging!**",
                true
        );
    }
}