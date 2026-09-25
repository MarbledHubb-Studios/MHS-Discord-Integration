package com.marbledhubb.mhs_discord_integration.configuration.config.manager;

import com.marbledhubb.mhs_discord_integration.configuration.config.ChatEventsJsonConfig;
import com.marbledhubb.mhs_discord_integration.configuration.config.ServerEventsJsonConfig;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AbstractJsonConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ChatMessageEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;

public class ChatEventsConfigManager extends AbstractJsonConfigManager<ChatEventsJsonConfig> {

    private static ChatEventsConfigManager instance;

    private ChatEventsConfigManager() {
        super(ChatEventsJsonConfig.class, "chat-events.json", ChatEventsJsonConfig::new);
    }

    public static ChatEventsConfigManager getInstance() {
        if (instance == null) {
            instance = new ChatEventsConfigManager();
        }
        return instance;
    }

    @Override
    protected boolean fillMissingDefaults(ChatEventsJsonConfig cfg) {
        boolean changed = false;
        if (cfg.messageSent == null) {
            cfg.messageSent = ChatMessageEntry.defaultEntry("Chat Events");
            changed = true;
        }
        return changed;
    }

    public ChatMessageEntry getMessageSent() {
        return getConfig().messageSent;
    }

}