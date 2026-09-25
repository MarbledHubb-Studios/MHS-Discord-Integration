package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.configuration.config.core.AdvancementCompletedEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class PlayerEventsJsonConfig {

    public MessageEventEntry playerJoinEvent = MessageEventEntry.defaultEntry("Player Events");
    public MessageEventEntry playerLeaveEvent = MessageEventEntry.defaultEntry("Player Events");
    public AdvancementCompletedEntry advancementCompleted = AdvancementCompletedEntry.defaultEntry("Player Events");
    public MessageEventEntry playerDeath = MessageEventEntry.defaultEntry("Player Events");

}