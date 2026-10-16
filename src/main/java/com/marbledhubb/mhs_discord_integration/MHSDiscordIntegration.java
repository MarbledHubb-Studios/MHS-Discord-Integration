package com.marbledhubb.mhs_discord_integration;

import com.marbledhubb.mhs_discord_integration.api.DiscordConsoleLog;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ChatEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.GeneralConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.WebhookChannelManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.PlayerEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ServerEventsConfigManager;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(MHSDiscordIntegration.MODID)
public class MHSDiscordIntegration {

    // No gag here, sorry. Wait, does this count as a gag?

    public static final String MODID = "mhs_discord_integration";
    public static final String NAME = "MHS Discord Integration";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MHSDiscordIntegration(IEventBus modEventBus) {

        NeoForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        WebhookChannelManager.getInstance().load();

        GeneralConfigManager.getInstance().load();
        ServerEventsConfigManager.getInstance().load();
        PlayerEventsConfigManager.getInstance().load();
        ChatEventsConfigManager.getInstance().load();

        DiscordConsoleLog.init();

    }

}
