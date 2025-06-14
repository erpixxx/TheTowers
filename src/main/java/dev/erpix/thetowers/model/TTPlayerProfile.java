package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the profile of a player in the game.
 */
public class TTPlayerProfile {

    private final String name;
    private final TotalStats stats = new TotalStats();

    public TTPlayerProfile(@NotNull String name) {
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
    public @NotNull StatsTracker<ProfileStatKey> getStats() {
        return stats;
    }

}
