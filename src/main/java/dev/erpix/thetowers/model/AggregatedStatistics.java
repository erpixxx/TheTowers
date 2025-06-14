package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents aggregated statistics for a player's profile.
 */
public class AggregatedStatistics extends StatisticsStorage<ProfileStatKey> {

    public AggregatedStatistics() {
        super(ProfileStatKey.class);
        for (ProfileStatKey stat : ProfileStatKey.values()) {
            setStat(stat, 0);
        }
    }

    /**
     * Incorporates game statistics into the aggregated statistics.
     *
     * <p>
     * Maps each {@link GameStatKey} from the provided {@link GameStatistics}
     * to a corresponding {@link ProfileStatKey} and increments the aggregated
     * statistics by the values from the game statistics.
     * </p>
     *
     * @param gameStats the game statistics to incorporate.
     * @throws IllegalArgumentException if a {@link GameStatKey} cannot be mapped to a {@link ProfileStatKey}.
     */
    public void incorporateGameStats(@NotNull GameStatistics gameStats) {
        gameStats.forEach(statEntry -> {
            ProfileStatKey profileStat = Mapper.map(statEntry.getKey());
            incrementStat(profileStat, statEntry.getValue());
        });
    }

    /**
     * Utility class for mapping {@link GameStatKey} to {@link ProfileStatKey}.
     */
    public static class Mapper {

        /**
         * Maps a {@link GameStatKey} to a corresponding {@link ProfileStatKey}.
         *
         * @param gameStatKey the game statistic key to map.
         * @return the corresponding {@link ProfileStatKey}.
         * @throws IllegalArgumentException if no matching {@link ProfileStatKey} exists.
         */
        public static @NotNull ProfileStatKey map(@NotNull GameStatKey gameStatKey) {
            String key = gameStatKey.getKey();
            ProfileStatKey profileStatKey = ProfileStatKey.fromKey(key);
            if (profileStatKey == null) {
                throw new IllegalArgumentException("No matching ProfileStatKey for GameStatKey: " + key);
            }
            return profileStatKey;
        }

    }

}
