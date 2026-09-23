package com.marbledhubb.mhs_discord_integration.configuration.config;

import com.marbledhubb.mhs_discord_integration.MHSDiscordIntegration;
import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MHSDiscordIntegration.MODID, value = Dist.DEDICATED_SERVER)
public class ModPlayerEventsConfiguration {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> PLAYER_LOGGED_IN = BUILDER
            .comment(
                    """
                            Configuration for the player join event.
                            Mode: NONE (no logging), SIMPLE (simple message), STYLED (nice embed)
                            Channel Reference: Discord channel mapping setup in config/mhs_discord_integration/mhs-discord-integration-webhooks.json
                            Username: Username of the webhook
                            Avatar URL: Avatar of the webhook
                            Embed Color: Hex Color of the embed if STYLED Mode is used.
                            """
            )
            .defineList(
                    "playerJoinEvent",
                    List.of(
                            MessageMode.STYLED.toString(),
                            "edit-me-or-i-wont-work",
                            "Player Events",
                            "https://example.com/example.png",
                            "FF0000"
                    ),
                    ModPlayerEventsConfiguration::validateItemName
            );

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String;
    }

    public static List<? extends String> playerLoggedIn;

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
