package com.marbledhubb.mhs_discord_integration;

import com.marbledhubb.mhs_discord_integration.configuration.ModGeneralConfiguration;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

        context.registerConfig(ModConfig.Type.SERVER, ModGeneralConfiguration.SPEC, NAME + "/general-configuration.toml");

    }
}
