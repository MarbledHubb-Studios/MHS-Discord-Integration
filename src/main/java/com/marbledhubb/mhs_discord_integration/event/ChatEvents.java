package com.marbledhubb.mhs_discord_integration.event;

import com.marbledhubb.mhs_discord_integration.api.DiscordWebhookAPI;
import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ChatMessageEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ChatEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import com.marbledhubb.mhs_discord_integration.util.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class ChatEvents {

    @SubscribeEvent
    public static void onMessageSent(ServerChatEvent event) {

        ChatMessageEntry configOptions = ChatEventsConfigManager.getInstance().getMessageSent();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        ServerPlayer player = event.getPlayer();

        AllowedMentions allowedMentions = AllowedMentions.custom(configOptions.allowMassPings, configOptions.allowUserPings, false);

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(configOptions.avatarUrl)
                .allowedMentions(allowedMentions);

        Component chatMessage = event.getMessage();
        String playerUsername = event.getUsername();

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> {
                Component message = Component.literal("<**" + playerUsername + "**> ").append(chatMessage);
                builder.content(message.getString());
            }

            case STYLED -> {

                String headUrl = PlayerUtils.getHeadUrl(player, 48);

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.embedColor)
                        .author(playerUsername, headUrl)
                        .description(chatMessage.getString())
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

}
