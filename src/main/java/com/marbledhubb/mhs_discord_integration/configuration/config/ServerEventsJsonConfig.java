package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ConsoleLogEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class ServerEventsJsonConfig {

    public MessageEventEntry serverStartup = MessageEventEntry.defaultEntry("Server Events", MessageMode.STYLED);
    public MessageEventEntry serverShutdown = MessageEventEntry.defaultEntry("Server Events", MessageMode.STYLED);
    public ConsoleLogEntry consoleLog = ConsoleLogEntry.defaultEntry("Console Log");

}