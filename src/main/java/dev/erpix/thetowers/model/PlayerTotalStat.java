package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents total statistics associated with a player profile with a few more specific stats.
 */
public interface PlayerTotalStat extends PlayerStat {

    PlayerStat GAMES_PLAYED = Registry.register("games_played");
    PlayerStat WINS = Registry.register("wins");
    PlayerStat LOSSES = Registry.register("losses");

    /**
     * Retrieves a {@link PlayerStat} instance by its key.
     *
     * @param key the key of the statistic type.
     * @return the corresponding {@link PlayerStat} instance, or null if not found.
     */
    static @Nullable PlayerStat fromKey(@NotNull String key) {
        return Registry.fromKey(key);
    }

    /**
     * Returns all registered {@link PlayerTotalStat} instances.
     *
     * @return an unmodifiable collection of all player statistics.
     */
    static @NotNull @Unmodifiable Collection<PlayerStat> totalStats() {
        return Registry.getAll();
    }

    /**
     * Returns all registered {@link PlayerStat} instances including {@link PlayerTotalStat}.
     *
     * @return an unmodifiable collection of all player statistics.
     */
    static @NotNull @Unmodifiable Collection<PlayerStat> stats() {
        ArrayList<PlayerStat> merged = new ArrayList<>(totalStats());
        merged.addAll(PlayerStat.stats());
        return merged;
    }

}
