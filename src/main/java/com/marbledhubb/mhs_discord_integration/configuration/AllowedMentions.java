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
        if (parse.contains("users")) {
            throw new IllegalStateException(
                    "Already allowing all users via allowAllUsers()/allowAll(); don't also call allowUser().");
        }
        users.add(userId);
        return this;
    }

    public static AllowedMentions allowAllUsers() {
        AllowedMentions mentions = new AllowedMentions();
        mentions.parse.add("users");
        return mentions;
    }

    public static AllowedMentions allowAllRoles() {
        AllowedMentions mentions = new AllowedMentions();
        mentions.parse.add("roles");
        return mentions;
    }

    public static AllowedMentions allowAll() {
        AllowedMentions mentions = new AllowedMentions();
        mentions.parse.add("everyone");
        mentions.parse.add("users");
        mentions.parse.add("roles");
        return mentions;
    }

    public static AllowedMentions custom(boolean everyone, boolean users, boolean roles) {
        AllowedMentions mentions = new AllowedMentions();
        if (everyone)
            mentions.parse.add("everyone");
        if (users)
            mentions.parse.add("users");
        if (roles)
            mentions.parse.add("roles");
        return mentions;
    }

    // Allows role pings so we have every option but..I don't think it will be used. Also, not sure if games can be pinged technically? But we'll find out
    public AllowedMentions allowRole(String roleId) {
        if (parse.contains("roles")) {
            throw new IllegalStateException(
                    "Already allowing all roles via allowAllRoles()/allowAll(); don't also call allowRole().");
        }
        roles.add(roleId);
        return this;
    }
}