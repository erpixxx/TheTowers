package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Consumer;

/**
 * A class for managing player statistics.
 */
public class StatsTracker implements Iterable<Map.Entry<PlayerStat, Integer>> {

    private final Map<PlayerStat, Integer> stats = new HashMap<>();

    /**
     * Retrieves the value of a specific statistic.
     *
     * @param key the Enum key representing the statistic.
     * @return the value of the statistic, or 0 if not present.
     */
    public int getStat(@NotNull PlayerStat key) {
        return stats.getOrDefault(key, 0);
    }

    /**
     * Retrieves the ratio of two statistics.
     *
     * @param first the first statistic.
     * @param second the second statistic.
     * @return the ratio of the first statistic to the second, or 0 if the second is 0.
     */
    public double getRatio(@NotNull PlayerStat first, @NotNull PlayerStat second) {
        int firstValue = getStat(first);
        int secondValue = getStat(second);
        return secondValue == 0 ? 0 : (double) firstValue / secondValue;
    }

    /**
     * Increments the value of a specific statistic by 1.
     *
     * @param key the Enum key representing the statistic.
     */
    public void incrementStat(@NotNull PlayerStat key) {
        stats.merge(key, 1, Integer::sum);
    }

    /**
     * Increments the value of a specific statistic by a given value.
     *
     * @param key the Enum key representing the statistic.
     * @param value the value to increment the statistic by (must be non-negative).
     */
    public void incrementStat(@NotNull PlayerStat key, @Range(from = 0, to = Integer.MAX_VALUE) int value) {
        stats.merge(key, value, Integer::sum);
    }

    /**
     * Decrements the value of a specific statistic by 1.
     *
     * @param key the Enum key representing the statistic.
     */
    public void decrementStat(@NotNull PlayerStat key) {
        stats.merge(key, -1, Integer::sum);
    }

    /**
     * Decrements the value of a specific statistic by a given value.
     *
     * @param key the Enum key representing the statistic.
     * @param value the value to decrement the statistic by (must be non-negative).
     */
    public void decrementStat(@NotNull PlayerStat key, @Range(from = 0, to = Integer.MAX_VALUE) int value) {
        stats.merge(key, -value, Integer::sum);
    }

    /**
     * Sets the value of a specific statistic.
     *
     * @param key the Enum key representing the statistic
     * @param value the value to set for the statistic
     */
    public void setStat(@NotNull PlayerStat key, int value) {
        stats.put(key, value);
    }

    /**
     * Clears all statistics, resetting them to zero.
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
