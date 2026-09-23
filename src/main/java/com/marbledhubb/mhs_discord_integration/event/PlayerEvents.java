package com.marbledhubb.mhs_discord_integration.event;

import com.marbledhubb.mhs_discord_integration.api.DiscordWebhookAPI;
import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.ModPlayerEventsConfiguration;
import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import com.marbledhubb.mhs_discord_integration.util.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.List;

@Mod.EventBusSubscriber
public class PlayerEvents {

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {

        List<? extends String> configOptions = ModPlayerEventsConfiguration.playerLoggedIn;

        MessageMode messageMode = MessageMode.fromString(configOptions.get(0));

        Player player = event.getEntity();

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.get(2))
                .avatarUrl(configOptions.get(3))
                .allowedMentions(AllowedMentions.none());

        Component message = Component.translatable("multiplayer.player.joined", player.getName().getString());

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> {
                builder.content(message.getString());
            }

            case STYLED -> {

                String headUrl = PlayerUtils.getHeadUrl(player, 48);

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.get(4))
                        .author(message.getString(), headUrl)
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.get(1), builder.build());

    }

}
