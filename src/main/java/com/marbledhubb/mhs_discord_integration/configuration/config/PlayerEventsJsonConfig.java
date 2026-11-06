package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AdvancementCompletedEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class PlayerEventsJsonConfig {

    public MessageEventEntry playerJoinEvent = MessageEventEntry.defaultEntry("Player Events", MessageMode.STYLED);
    public MessageEventEntry playerLeaveEvent = MessageEventEntry.defaultEntry("Player Events", MessageMode.STYLED);
    public AdvancementCompletedEntry advancementCompleted = AdvancementCompletedEntry.defaultEntry("Player Events");
    public MessageEventEntry playerDeath = MessageEventEntry.defaultEntry("Player Events", MessageMode.STYLED);

}