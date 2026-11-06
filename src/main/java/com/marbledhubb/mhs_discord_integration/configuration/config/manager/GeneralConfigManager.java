package com.marbledhubb.mhs_discord_integration.configuration.config.manager;

import com.marbledhubb.mhs_discord_integration.configuration.config.GeneralJsonConfig;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AbstractJsonConfigManager;

public class GeneralConfigManager extends AbstractJsonConfigManager<GeneralJsonConfig> {

    private static GeneralConfigManager instance;

    private GeneralConfigManager() {
        super(GeneralJsonConfig.class, "general.json", GeneralJsonConfig::new);
    }

    public static GeneralConfigManager getInstance() {
        if (instance == null) {
            instance = new GeneralConfigManager();
        }
        return instance;
    }

    @Override
    protected boolean fillMissingDefaults(GeneralJsonConfig cfg) {
        if (cfg.maxRetries < 1) {
            cfg.maxRetries = 1;
            return true;
        }
        return false;
    }

    public int getMaxRetries() {
        return getConfig().maxRetries;
    }

    public String getDefaultAvatarUrl() {
        return getConfig().defaultAvatarUrl;
    }

}