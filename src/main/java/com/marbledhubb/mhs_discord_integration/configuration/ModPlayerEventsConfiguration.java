package com.marbledhubb.mhs_discord_integration.configuration;

import com.marbledhubb.mhs_discord_integration.MHSDiscordIntegration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MHSDiscordIntegration.MODID, value = Dist.DEDICATED_SERVER)
public class ModPlayerEventsConfiguration {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue PLAYER_LOGGED_IN = BUILDER
            .comment(
                    "Whether player join events should be logged or not."
            )
            .define(
                    "playerJoinEvent",
                    true
            );

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean playerLoggedIn;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {

        if (event.getConfig().getSpec() != SPEC) return;

        playerLoggedIn = PLAYER_LOGGED_IN.get();

    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {

        if (event.getConfig().getSpec() != SPEC) return;

        playerLoggedIn = PLAYER_LOGGED_IN.get();

    }
}
