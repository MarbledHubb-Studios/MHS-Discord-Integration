package com.marbledhubb.mhs_discord_integration;

import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ChatEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.GeneralConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.WebhookChannelManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.PlayerEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ServerEventsConfigManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MHSDiscordIntegration.MODID)
public class MHSDiscordIntegration {

    // No gag here, sorry. Wait, does this count as a gag?

    public static final String MODID = "mhs_discord_integration";
    public static final String NAME = "MHS Discord Integration";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MHSDiscordIntegration(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        WebhookChannelManager.getInstance().load();
        GeneralConfigManager.getInstance().load();
        ServerEventsConfigManager.getInstance().load();
        PlayerEventsConfigManager.getInstance().load();
        ChatEventsConfigManager.getInstance().load();
    }

}
