package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the profile of a player in the game.
 */
public class PlayerProfile {

    private final String name;
    private final StatsTracker stats = new StatsTracker();

    public PlayerProfile(@NotNull String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the player.
     *
     * @return the name of the player
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Retrieves the player's aggregated statistics.
     *
     * @return the {@link StatsTracker} for the player's profile
     */
    public @NotNull StatsTracker getStats() {
        return stats;
    }

}
