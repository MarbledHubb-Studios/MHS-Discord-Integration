package com.marbledhubb.mhs_discord_integration.util;

import net.minecraft.world.entity.player.Player;

public class PlayerUtils {

    public static String getHeadUrl(Player player, Integer resolution) {
        if (resolution == null)
            resolution = 600;
        return "https://skinmc.net/api/v1/face/username/" + player.getName().getString() + "/" + resolution;
    }

}
