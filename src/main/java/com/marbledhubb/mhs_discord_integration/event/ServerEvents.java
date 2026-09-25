package com.marbledhubb.mhs_discord_integration.event;

import com.marbledhubb.mhs_discord_integration.api.DiscordWebhookAPI;
import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ServerEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;
import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class ServerEvents {

    @SubscribeEvent
    public static void onStartup(ServerStartedEvent event) {

        MessageEventEntry configOptions = ServerEventsConfigManager.getInstance().getServerStartup();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(configOptions.avatarUrl)
                .allowedMentions(AllowedMentions.none());

        MinecraftServer server = event.getServer();

        Component message = Component.translatable("server_events.mhs_discord_integration.startup");

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> {
                builder.content(message.getString());
            }

            case STYLED -> {

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.embedColor)
                        .title(message.getString())
                        .description(server.getMotd())
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

    @SubscribeEvent
    public static void onShutdown(ServerStoppingEvent event) {

        MessageEventEntry configOptions = ServerEventsConfigManager.getInstance().getServerShutdown();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(configOptions.avatarUrl)
                .allowedMentions(AllowedMentions.none());

        Component message = Component.translatable("server_events.mhs_discord_integration.shutdown");

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> {
                builder.content(message.getString());
            }

            case STYLED -> {

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.embedColor)
                        .title(message.getString())
                        .description(Component.translatable("server_events.mhs_discord_integration.shutdown.details").getString())
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

}
