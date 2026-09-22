package com.marbledhubb.mhs_discord_integration.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.marbledhubb.mhs_discord_integration.MHSDiscordIntegration;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebhookChannelManager {

    // Instead of having to deal with urls in the config, people can specify in the generated json a "mapping"
    // of the channels they want and related webhook urls. A bit less messy in my opinion, but we can scrap this if not needed

    private static final WebhookChannelManager INSTANCE = new WebhookChannelManager();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, String> webhooksByAlias = new ConcurrentHashMap<>();
    private Path filePath;
    private boolean loaded = false;

    public static WebhookChannelManager getInstance() {
        return INSTANCE;
    }

    public synchronized void load() {
        try {
            Path configDir = FMLPaths.CONFIGDIR.get().resolve(MHSDiscordIntegration.MODID);
            Files.createDirectories(configDir);
            filePath = configDir.resolve("mhs-discord-integration-webhooks.json");

            if (!Files.exists(filePath)) {
                Map<String, String> example = new LinkedHashMap<>();
                example.put("example-channel", "https://discord.com/api/webhooks/PUT_WEBHOOK_ID_HERE/PUT_WEBHOOK_TOKEN_HERE");
                writeToDisk(example);
                webhooksByAlias.clear();
                webhooksByAlias.putAll(example);
                MHSDiscordIntegration.LOGGER.info("Created a new webhooks list file with an example entry at {}", filePath);
                loaded = true;
                return;
            }

            try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                Type type = new TypeToken<LinkedHashMap<String, String>>() {}.getType();
                Map<String, String> fromDisk = GSON.fromJson(reader, type);
                webhooksByAlias.clear();
                if (fromDisk != null) {
                    webhooksByAlias.putAll(fromDisk);
                }
            }
            loaded = true;
            MHSDiscordIntegration.LOGGER.info("Loaded {} Discord webhook alias(es).", webhooksByAlias.size());
        } catch (IOException e) {
            MHSDiscordIntegration.LOGGER.error("Failed to load webhooks list file", e);
        }
    }

    private void writeToDisk(Map<String, String> data) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            GSON.toJson(data, writer);
        }
    }

    public synchronized void save() {
        if (filePath == null) return;
        try {
            writeToDisk(new LinkedHashMap<>(webhooksByAlias));
        } catch (IOException e) {
            MHSDiscordIntegration.LOGGER.error("Failed to save webhooks list file", e);
        }
    }

    public Optional<String> getUrl(String alias) {
        if (!loaded) load();
        return Optional.ofNullable(webhooksByAlias.get(alias));
    }

    public synchronized void addWebhook(String alias, String url) {
        webhooksByAlias.put(alias, url);
        save();
    }

    public synchronized boolean removeWebhook(String alias) {
        boolean removed = webhooksByAlias.remove(alias) != null;
        if (removed) save();
        return removed;
    }

    public Map<String, String> getAll() {
        if (!loaded) load();
        return new LinkedHashMap<>(webhooksByAlias);
    }
}