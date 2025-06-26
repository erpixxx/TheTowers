package dev.erpix.thetowers.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for loading the configuration.
 */
public class ConfigLoader {

    /**
     * Config file name.
     */
    public static final String CONFIG_FILE_NAME = "config.yml";

    /**
     * Loads the configuration from the plugin's data folder.
     *
     * @param plugin the plugin instance from which to load the configuration.
     * @return the loaded configuration.
     * @throws ConfigLoadException if the configuration file cannot be loaded.
     */
    public static @NotNull Config load(@NotNull Plugin plugin) {
        Path dataPath = plugin.getDataPath();
        if (!Files.exists(dataPath)) {
            plugin.saveDefaultConfig();
        }
        Path configPath = dataPath.resolve(CONFIG_FILE_NAME);

        try {
            return YamlLoader.load(configPath, Config.class);
        } catch (IOException e) {
            throw new ConfigLoadException("Failed to load configuration file.", e);
        }
    }

    /**
     * Saves the configuration to the plugin's data folder.
     *
     * @param plugin the plugin instance from which to save the configuration.
     * @param config the configuration to save.
     * @throws ConfigLoadException if the configuration file cannot be saved.
     */
    public static void save(@NotNull Plugin plugin, @NotNull Config config) {
        Path dataPath = plugin.getDataPath();
        if (!Files.exists(dataPath)) {
            try {
                Files.createDirectories(dataPath);
            } catch (IOException e) {
                throw new ConfigLoadException("Failed to create data directory.", e);
            }
        }
        Path configPath = dataPath.resolve(CONFIG_FILE_NAME);

        try {
            YamlLoader.save(configPath, config);
        } catch (IOException e) {
            throw new ConfigLoadException("Failed to save configuration file.", e);
        }
    }

}
