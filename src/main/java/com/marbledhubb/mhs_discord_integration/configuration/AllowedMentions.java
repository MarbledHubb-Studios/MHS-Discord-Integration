package com.marbledhubb.mhs_discord_integration.configuration;

import java.util.ArrayList;
import java.util.List;

public class AllowedMentions {

    // In a nutshell, if we don't tell the webhook whether it's allowed to ping or not, it will actually ping user, roles or even
    // @everyone or @here, which is not very nice

    private final List<String> parse = new ArrayList<>();
    private final List<String> roles = new ArrayList<>();
    private final List<String> users = new ArrayList<>();

    // This suppresses everything, so no pings at all
    public static AllowedMentions none() {
        return new AllowedMentions();
    }

    // Allows mass pinging. Probably won't be needed
    public static AllowedMentions allowEveryone() {
        AllowedMentions mentions = new AllowedMentions();
        mentions.parse.add("everyone");
        return mentions;
    }

    // Allows user pings. Maybe can be useful if we ever add the option to link game chat to a public channel and let Discord and game chat mix
    public AllowedMentions allowUser(String userId) {
        users.add(userId);
        return this;
    }

    // Allows role pings so we have every option but..I don't think it will be used. Also not sure if games can be pinged technically? But we'll find out
    public AllowedMentions allowRole(String roleId) {
        roles.add(roleId);
        return this;
    }
}