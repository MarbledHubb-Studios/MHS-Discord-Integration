package com.marbledhubb.mhs_discord_integration.configuration.config.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marbledhubb.mhs_discord_integration.MHSDiscordIntegration;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public abstract class AbstractJsonConfigManager<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> type;
    private final String fileName;
    private final Supplier<T> defaultFactory;

    protected T config;

    protected AbstractJsonConfigManager(Class<T> type, String fileName, Supplier<T> defaultFactory) {
        this.type = type;
        this.fileName = fileName;
        this.defaultFactory = defaultFactory;
        this.config = defaultFactory.get();
    }

    private Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get()
                .resolve(MHSDiscordIntegration.MODID)
                .resolve(fileName);
    }

    protected boolean fillMissingDefaults(T config) {
        return false;
    }

    public void load() {
        Path path = getConfigPath();

        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                config = defaultFactory.get();
                save();
                MHSDiscordIntegration.LOGGER.info("Created default config at {}", path);
                return;
            }

            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                T loaded = GSON.fromJson(reader, type);
                config = (loaded != null) ? loaded : defaultFactory.get();
            }

            if (fillMissingDefaults(config)) {
                save();
            }

            MHSDiscordIntegration.LOGGER.info("Loaded config from {}", path);

        } catch (IOException e) {
            MHSDiscordIntegration.LOGGER.error("Failed to load config {}, falling back to defaults", fileName, e);
            config = defaultFactory.get();
        }
    }

    public void save() {
        Path path = getConfigPath();
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            MHSDiscordIntegration.LOGGER.error("Failed to save config {}", fileName, e);
        }
    }

    public T getConfig() {
        return config;
    }
}