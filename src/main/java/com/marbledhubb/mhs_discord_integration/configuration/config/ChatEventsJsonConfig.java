package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ChatMessageEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class ChatEventsJsonConfig {

    public ChatMessageEntry messageSent = ChatMessageEntry.defaultEntry("Chat Events");
    public MessageEventEntry commandExecuted = MessageEventEntry.defaultEntry("Chat Events", MessageMode.SIMPLE);

}