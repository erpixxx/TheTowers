package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Stores and manages player statistics.
 */
public class StatsTracker implements Iterable<Map.Entry<PlayerStat, Integer>> {

    private final Map<PlayerStat, Integer> stats = new HashMap<>();

    /**
     * Retrieves the current value of a specific statistic.
     *
     * @param key the stat key.
     * @return the value or 0 if not present.
     */
    public int getStat(@NotNull PlayerStat key) {
        return stats.getOrDefault(key, 0);
    }

    /**
     * Retrieves the ratio of two statistics. (safe against division by zero)
     *
     * @param first the first statistic.
     * @param second the second statistic.
     * @return the ratio of the first statistic to the second.
     */
    public double getRatio(@NotNull PlayerStat first, @NotNull PlayerStat second) {
        int firstValue = getStat(first);
        int secondValue = getStat(second);
        return secondValue == 0 ? 0 : (double) firstValue / secondValue;
    }

    /**
     * Increments the value of a specific statistic by 1.
     *
     * @param key the stat key.
     */
    public void incrementStat(@NotNull PlayerStat key) {
        stats.merge(key, 1, Integer::sum);
    }

    /**
     * Increments the value of a specific statistic by a given value.
     *
     * @param key the stat key.
     * @param value the value to increment the statistic by.
     */
    public void incrementStat(@NotNull PlayerStat key, int value) {
        stats.merge(key, value, Integer::sum);
    }

    /**
     * Decrements the value of a specific statistic by 1.
     *
     * @param key the stat key.
     */
    public void decrementStat(@NotNull PlayerStat key) {
        stats.merge(key, -1, Integer::sum);
    }

    /**
     * Decrements the value of a specific statistic by a given value.
     *
     * @param key the stat key.
     * @param value the value to decrement the statistic by.
     */
    public void decrementStat(@NotNull PlayerStat key, int value) {
        stats.merge(key, -value, Integer::sum);
    }

    /**
     * Sets the value of a specific statistic.
     *
     * @param key the stat key.
     * @param value the value to set.
     */
    public void setStat(@NotNull PlayerStat key, int value) {
        stats.put(key, value);
    }

    /**
     * Clears all stats, resetting them to zero.
     */
    public void reset() {
        stats.replaceAll((k, v) -> 0);
    }

    @Override
    public @NotNull Iterator<Map.Entry<PlayerStat, Integer>> iterator() {
        return stats.entrySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<PlayerStat, Integer>> action) {
        stats.entrySet().forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<PlayerStat, Integer>> spliterator() {
        return stats.entrySet().spliterator();
    }

}
