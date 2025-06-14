package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the profile of a player in the game.
 */
public class TPlayerProfile {

    private final String name;
    private final AggregatedStatistics stats = new AggregatedStatistics();

    public TPlayerProfile(@NotNull String name) {
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
     * @return the {@link StatisticsStorage} for the player's profile
     */
    public @NotNull StatisticsStorage<ProfileStatKey> getStats() {
        return stats;
    }

}
