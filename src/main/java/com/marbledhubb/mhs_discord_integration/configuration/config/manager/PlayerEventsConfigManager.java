package com.marbledhubb.mhs_discord_integration.configuration.config.manager;

import com.marbledhubb.mhs_discord_integration.configuration.config.PlayerEventsJsonConfig;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AbstractJsonConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AdvancementCompletedEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class PlayerEventsConfigManager extends AbstractJsonConfigManager<PlayerEventsJsonConfig> {

    private static PlayerEventsConfigManager instance;

    private PlayerEventsConfigManager() {
        super(PlayerEventsJsonConfig.class, "player-events.json", PlayerEventsJsonConfig::new);
    }

    public static PlayerEventsConfigManager getInstance() {
        if (instance == null) {
            instance = new PlayerEventsConfigManager();
        }
        return instance;
    }

    @Override
    protected boolean fillMissingDefaults(PlayerEventsJsonConfig cfg) {
        boolean changed = false;
        if (cfg.playerJoinEvent == null) {
            cfg.playerJoinEvent = MessageEventEntry.defaultEntry("Player Events");
            changed = true;
        }
        if (cfg.playerLeaveEvent == null) {
            cfg.playerLeaveEvent = MessageEventEntry.defaultEntry("Player Events");
            changed = true;
        }
        if (cfg.advancementCompleted == null) {
            cfg.advancementCompleted = AdvancementCompletedEntry.defaultEntry("Player Events");
            changed = true;
        }
        if (cfg.playerDeath == null) {
            cfg.playerDeath = MessageEventEntry.defaultEntry("Player Events");
            changed = true;
        }
        return changed;
    }

    public MessageEventEntry getPlayerJoinEvent() {
        return getConfig().playerJoinEvent;
    }

    public MessageEventEntry getPlayerLeaveEvent() {
        return getConfig().playerLeaveEvent;
    }

    public AdvancementCompletedEntry getAdvancementCompleted() {
        return getConfig().advancementCompleted;
    }

    public MessageEventEntry getPlayerDeath() {
        return getConfig().playerDeath;
    }
}