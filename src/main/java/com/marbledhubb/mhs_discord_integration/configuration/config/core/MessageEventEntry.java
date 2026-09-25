package com.marbledhubb.mhs_discord_integration.configuration.config.core;

import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;

public class MessageEventEntry {

    public String mode;
    public String channelReference;
    public String username;
    public String avatarUrl;
    public String embedColor;

    // Empty but needed for deserialization, don't touch me!
    public MessageEventEntry() {
    }

    public MessageEventEntry(String mode, String channelReference, String username, String avatarUrl, String embedColor) {
        this.mode = mode;
        this.channelReference = channelReference;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.embedColor = embedColor;
    }

    public static MessageEventEntry defaultEntry(String username) {
        return new MessageEventEntry(
                MessageMode.STYLED.toString(),
                "edit-me-or-i-wont-work",
                username,
                "https://example.com/example.png",
                "FF0000"
        );
    }
}