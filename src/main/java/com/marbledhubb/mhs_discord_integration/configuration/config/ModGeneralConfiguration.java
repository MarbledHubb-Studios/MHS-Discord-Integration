package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.MHSDiscordIntegration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MHSDiscordIntegration.MODID, value = Dist.DEDICATED_SERVER)
public class ModGeneralConfiguration {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_RETRIES = BUILDER
            .comment(
                    "Maximum retries when the webhook hits an error or rate limit."
            )
            .defineInRange(
                    "maxRetries",
                    3,
                    1,
                    Integer.MAX_VALUE
            );

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int maxRetries;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {

        if (event.getConfig().getSpec() != SPEC) return;

        maxRetries = MAX_RETRIES.get();

    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {

        if (event.getConfig().getSpec() != SPEC) return;

        maxRetries = MAX_RETRIES.get();

    }
}
