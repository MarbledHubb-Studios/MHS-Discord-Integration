package com.marbledhubb.mhs_discord_integration.event;

import com.marbledhubb.mhs_discord_integration.api.DiscordWebhookAPI;
import com.marbledhubb.mhs_discord_integration.configuration.ModPlayerEventsConfiguration;
import com.marbledhubb.mhs_discord_integration.core.DiscordEmbed;
import com.marbledhubb.mhs_discord_integration.core.DiscordWebhookMessage;
import com.marbledhubb.mhs_discord_integration.util.PlayerUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber
public class PlayerEvents {

    @SubscribeEvent
    public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {

        if (!ModPlayerEventsConfiguration.playerLoggedIn)
            return;

        Player player = event.getEntity();
        String headUrl = PlayerUtils.getHeadUrl(player, 48);

        DiscordEmbed embed = DiscordEmbed.builder()
                .color(Color.RED)
                .author(player.getName().getString() + " joined the game", headUrl)
                .build();

        DiscordWebhookMessage message = DiscordWebhookMessage.builder()
                .username("Player Events")
                .avatarUrl("https://media.discordapp.net/attachments/1495718002039980082/1495718010219139072/mhs_logo_square_big.png?ex=69e74372&is=69e5f1f2&hm=4d9cf64445606d488d745c44e7eab84921a73f8fb39f301456a01dd28a6d0d19&=&format=webp&quality=lossless&width=960&height=960")
                .addEmbed(embed)
                .build();

        DiscordWebhookAPI.sendMessage("bot-log", message);

    }

}
