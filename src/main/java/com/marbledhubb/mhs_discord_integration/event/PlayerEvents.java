package com.marbledhubb.mhs_discord_integration.event;

import com.marbledhubb.mhs_discord_integration.api.DiscordWebhookAPI;
import com.marbledhubb.mhs_discord_integration.configuration.AllowedMentions;
import com.marbledhubb.mhs_discord_integration.configuration.MessageMode;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.GeneralConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.manager.PlayerEventsConfigManager;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.AdvancementCompletedEntry;
import com.marbledhubb.mhs_discord_integration.configuration.config.core.MessageEventEntry;
import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import com.marbledhubb.mhs_discord_integration.util.PlayerUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class PlayerEvents {

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {

        MessageEventEntry configOptions = PlayerEventsConfigManager.getInstance().getPlayerJoinEvent();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        Player player = event.getEntity();

        String avatarUrl = configOptions.avatarUrl;
        if (avatarUrl.equals("default"))
            avatarUrl = GeneralConfigManager.getInstance().getDefaultAvatarUrl();

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(avatarUrl)
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
                        .color(configOptions.embedColor)
                        .author(message.getString(), headUrl)
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

    @SubscribeEvent
    public static void onAdvancementComplete(AdvancementEvent.AdvancementEarnEvent event) {

        AdvancementCompletedEntry configOptions = PlayerEventsConfigManager.getInstance().getAdvancementCompleted();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        Player player = event.getEntity();
        Advancement advancement = event.getAdvancement();
        DisplayInfo display = advancement.getDisplay();

        if (display == null) return;
        if (advancement.getParent() == null) return;

        FrameType type = display.getFrame();

        String avatarUrl = configOptions.avatarUrl;
        if (avatarUrl.equals("default"))
            avatarUrl = GeneralConfigManager.getInstance().getDefaultAvatarUrl();

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(avatarUrl)
                .allowedMentions(AllowedMentions.none());

        String translatable = "chat.type.advancement.task";

        switch (type) {
            case GOAL -> translatable = "chat.type.advancement.goal";
            case CHALLENGE -> translatable = "chat.type.advancement.challenge";
        }

        StringBuilder messageBuilder = new StringBuilder();

        Component title = Component.translatable(
                translatable,
                "**" + player.getName().getString() + "**",
                "**" + advancement.getDisplay().getTitle().getString() + "**"
        );

        messageBuilder.append(title.getString());

        if (configOptions.includeDescription)
            messageBuilder.append("\n").append("_").append(advancement.getDisplay().getDescription().getString()).append("_");

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> builder.content(messageBuilder.toString());

            case STYLED -> {

                String headUrl = PlayerUtils.getHeadUrl(player, 32);

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.embedColor)
                        .description(messageBuilder.toString())
                        .author(player.getName().getString(), headUrl)
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;

        MessageEventEntry configOptions = PlayerEventsConfigManager.getInstance().getPlayerDeath();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        String avatarUrl = configOptions.avatarUrl;
        if (avatarUrl.equals("default"))
            avatarUrl = GeneralConfigManager.getInstance().getDefaultAvatarUrl();

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(avatarUrl)
                .allowedMentions(AllowedMentions.none());

        DamageSource source = event.getSource();

        Component message = Component.literal(
                source.getLocalizedDeathMessage(player).getString()
        );

        switch (messageMode) {

            case NONE -> {
                return;
            }

            case SIMPLE -> {
                message = Component.literal(
                        source.getLocalizedDeathMessage(player).getString()
                                .replace(
                                        player.getName().getString(),
                                        "**" + player.getName().getString() + "**"
                                )
                );
                builder.content(message.getString());
            }

            case STYLED -> {

                String headUrl = PlayerUtils.getHeadUrl(player, 48);

                DiscordEmbed embed = DiscordEmbed.builder()
                        .color(configOptions.embedColor)
                        .author(message.getString(), headUrl)
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

    @SubscribeEvent
    public static void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {

        MessageEventEntry configOptions = PlayerEventsConfigManager.getInstance().getPlayerLeaveEvent();

        MessageMode messageMode = MessageMode.fromString(configOptions.mode);

        Player player = event.getEntity();

        String avatarUrl = configOptions.avatarUrl;
        if (avatarUrl.equals("default"))
            avatarUrl = GeneralConfigManager.getInstance().getDefaultAvatarUrl();

        DiscordWebhookMessage.Builder builder = DiscordWebhookMessage.builder()
                .username(configOptions.username)
                .avatarUrl(avatarUrl)
                .allowedMentions(AllowedMentions.none());

        Component message = Component.translatable("multiplayer.player.left", player.getName().getString());

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
                        .color(configOptions.embedColor)
                        .author(message.getString(), headUrl)
                        .build();

                builder.addEmbed(embed);

            }

        }

        DiscordWebhookAPI.sendMessage(configOptions.channelReference, builder.build());

    }

}
