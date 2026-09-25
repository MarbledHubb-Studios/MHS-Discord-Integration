package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class ServerEventsJsonConfig {

    public MessageEventEntry serverStartup = MessageEventEntry.defaultEntry("Server Events");
    public MessageEventEntry serverShutdown = MessageEventEntry.defaultEntry("Server Events");

}