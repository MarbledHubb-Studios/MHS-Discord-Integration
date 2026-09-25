package com.marbledhubb.mhs_discord_integration.configuration.config.manager;

import com.marbledhubb.mhs_discord_integration.configuration.config.ServerEventsJsonConfig;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AbstractJsonConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class ServerEventsConfigManager extends AbstractJsonConfigManager<ServerEventsJsonConfig> {

    private static ServerEventsConfigManager instance;

    private ServerEventsConfigManager() {
        super(ServerEventsJsonConfig.class, "server-events.json", ServerEventsJsonConfig::new);
    }

    public static ServerEventsConfigManager getInstance() {
        if (instance == null) {
            instance = new ServerEventsConfigManager();
        }
        return instance;
    }

    @Override
    protected boolean fillMissingDefaults(ServerEventsJsonConfig cfg) {
        boolean changed = false;
        if (cfg.serverStartup == null) {
            cfg.serverStartup = MessageEventEntry.defaultEntry("Server Events");
            changed = true;
        }
        if (cfg.serverShutdown == null) {
            cfg.serverShutdown = MessageEventEntry.defaultEntry("Server Events");
            changed = true;
        }
        return changed;
    }

    public MessageEventEntry getServerStartup() {
        return getConfig().serverStartup;
    }

    public MessageEventEntry getServerShutdown() {
        return getConfig().serverShutdown;
    }
}