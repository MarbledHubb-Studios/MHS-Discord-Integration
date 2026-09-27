package com.marbledhubb.mhs_discord_integration.configuration.config.core;

import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;

public class ChatMessageEntry extends MessageEventEntry {

    public boolean allowUserPings;
    public boolean allowMassPings;

    public ChatMessageEntry() {
        super();
    }

    public ChatMessageEntry(String mode, String channelReference, String username, String avatarUrl,
                            String embedColor, boolean allowUserPings, boolean allowMassPings) {
        super(mode, channelReference, username, avatarUrl, embedColor);
        this.allowUserPings = allowUserPings;
        this.allowMassPings = allowMassPings;
    }

    public static ChatMessageEntry defaultEntry(String username) {
        return new ChatMessageEntry(
                MessageMode.SIMPLE.toString(),
                "edit-me-or-i-wont-work",
                username,
                "default",
                "FF0000",
                true,
                false
        );
    }
}