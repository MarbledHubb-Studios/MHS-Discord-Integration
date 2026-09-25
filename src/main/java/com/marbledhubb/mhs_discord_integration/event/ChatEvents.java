package com.marbledhubb.mhs_discord_integration.event;

import com.marbledhubb.mhs_discord_integration.api.DiscordWebhookAPI;
import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.ChatMessageEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.ChatEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.PlayerEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import com.marbledhubb.mhs_discord_integration.util.PlayerUtils;
import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CommandEvent;
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

    @SubscribeEvent
    public static void onCommand(CommandEvent event) {

        ParseResults<CommandSourceStack> parse = event.getParseResults();
        CommandSourceStack source = event.getParseResults().getContext().getSource();

        String command = parse.getReader().getString();

        if (command.contains("msg") || command.contains("tell") || command.contains("whisper")) return;

        ServerPlayer executor = null;
        Component message = Component.translatable("chat_events.mhs_discord_integration.command_used", "**" + command + "**");

        if (source.getEntity() instanceof ServerPlayer player) {
            executor = player;
            message = Component.translatable("chat_events.mhs_discord_integration.command_used.player",
                    "**" + executor.getName().getString() + "**",
                    "**/" + command + "**");
        }

        MessageEventEntry configOptions = ChatEventsConfigManager.getInstance().getCommandExecuted();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(configOptions.avatarUrl)
                .allowedMentions(AllowedMentions.none());

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> {
                builder.content(message.getString());
            }

            case STYLED -> {

                String noBold = message.getString().replace("**", "");

                String headUrl = PlayerUtils.getHeadUrl(executor, 48);

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.embedColor)
                        .author(noBold, headUrl)
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

}
