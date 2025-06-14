package dev.erpix.thetowers.model.manager;

import com.google.gson.Gson;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.TTPlayerProfile;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages player profiles, including loading, saving, and creating profiles.
 */
public class ProfileManager {

    private static final ComponentLogger logger = TheTowers.getInstance().getLogger();

    private final Path profilesDir = TheTowers.getInstance().getPlugin().getDataPath().resolve("profiles");
    private final Map<String, TTPlayerProfile> profiles = new HashMap<>();
    private final Gson gson = new Gson();

    /**
     * Initializes the profile manager.
     *
     * <p>Ensures that the profiles directory exists, creating it if necessary.</p>
     */
    public void init() {
        try {
            if (!Files.exists(profilesDir)) {
                Files.createDirectories(profilesDir);
                logger.info("Created profiles directory at: {}", profilesDir);
            }
        } catch (IOException e) {
            logger.error("Could not create profiles directory: {}", e.getMessage());
        }
    }

    /**
     * Loads a player's profile from a JSON file.
     *
     * <p>If the profile file does not exist or is invalid, a new profile is created.</p>
     *
     * @param playerName the name of the player whose profile is to be loaded.
     */
    public void load(@NotNull String playerName) {
        try (FileReader reader = new FileReader(jsonFile(playerName))) {
            TTPlayerProfile profile = gson.fromJson(reader, TTPlayerProfile.class);

            if (profile == null || profile.getName().isEmpty()) {
                profile = createProfile(playerName);
            }

            profiles.put(profile.getName(), profile);
            logger.info("Loaded '{}' profile", playerName);
        } catch (FileNotFoundException e) {
            logger.info("Profile file for '{}' not found, creating new profile", playerName);
            createProfile(playerName);
        } catch (IOException e) {
            logger.error("Error reading profile file", e);
        }
    }

    /**
     * Saves a player's profile to a JSON file.
     *
     * @param playerName the name of the player whose profile is to be saved.
     */
    public void save(@NotNull String playerName) {
        try (FileWriter writer = new FileWriter(jsonFile(playerName))) {
            TTPlayerProfile profile = profiles.get(playerName);

            if (profile == null) {
                logger.warn("Cannot save profile for '{}', profile not found", playerName);
            }

            gson.toJson(profile, writer);
            logger.info("Saved profile for '{}'", playerName);
        } catch (IOException e) {
            logger.error("Error writing profiles file", e);
        }
    }

    /**
     * Saves all player profiles to their respective JSON files.
     */
    public void saveAll() {
        for (TTPlayerProfile profile : profiles.values()) {
            save(profile.getName());
        }
        logger.info("All player profiles saved successfully.");
    }

    // TODO: docs
    /**
     * Retrieves a player's profile from memory.
     *
     * @param name the name of the player whose profile is to be retrieved.
     * @return the {@link TTPlayerProfile}, or null if not found.
     */
    public @NotNull Optional<TTPlayerProfile> getProfile(@NotNull String name) {
        return Optional.ofNullable(this.profiles.get(name));
    }

    /**
     * Creates a new profile for a player and stores it in memory.
     *
     * @param name the name of the player for whom the profile is to be created.
     * @return the newly created {@link TTPlayerProfile}.
     */
    private @NotNull TTPlayerProfile createProfile(@NotNull String name) {
        TTPlayerProfile profile = new TTPlayerProfile(name);
        this.profiles.put(name, profile);

        logger.info("Created new profile for player '{}'", name);
        return profile;
    }

    /**
     * Constructs the file path for a player's profile JSON file.
     *
     * @param file the name of the file (without extension).
     * @return the file representing the profile file path.
     */
    private @NotNull File jsonFile(@NotNull String file) {
        return profilesDir.resolve(file + ".json").toFile();
    }

}
