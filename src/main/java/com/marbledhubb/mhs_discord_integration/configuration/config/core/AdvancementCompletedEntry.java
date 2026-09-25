package com.marbledhubb.mhs_discord_integration.configuration.config.core;

import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;

public class AdvancementCompletedEntry extends MessageEventEntry {

    public boolean includeDescription;

    public AdvancementCompletedEntry() {
        super();
    }

    public AdvancementCompletedEntry(String mode, String channelReference, String username, String avatarUrl,
                                      String embedColor, boolean includeDescription) {
        super(mode, channelReference, username, avatarUrl, embedColor);
        this.includeDescription = includeDescription;
    }

    public static AdvancementCompletedEntry defaultEntry(String username) {
        return new AdvancementCompletedEntry(
                MessageMode.STYLED.toString(),
                "edit-me-or-i-wont-work",
                username,
                "https://example.com/example.png",
                "FF0000",
                true
        );
    }
}