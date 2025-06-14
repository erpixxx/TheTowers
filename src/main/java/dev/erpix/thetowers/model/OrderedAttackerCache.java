package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Manages a cache of attackers who have dealt damage to a player,
 * sorted by the amount of damage dealt in descending order.
 *
 * <p>
 * This cache automatically cleans up entries that are older than the specified TTL (time-to-live).
 * </p>
 */
public class OrderedAttackerCache implements Iterable<OrderedAttackerCache.AttackerEntry>, AutoCloseable {

    private final long ttlMillis;
    private final Map<TPlayer, AttackerEntry> playerMap;
    private final NavigableMap<DamageKey, TPlayer> damageMap;
    private boolean registered = false;

    public OrderedAttackerCache(long ttlSeconds) {
        this.ttlMillis = ttlSeconds * 1000;
        this.playerMap = new ConcurrentHashMap<>();
        this.damageMap = new ConcurrentSkipListMap<>(Collections.reverseOrder()); // Highest damage first

        Manager.getInstance().register(this);
        registered = true;
    }

    /**
     * Adds or updates an attacker in the cache with the specified damage.
     *
     * @param player The attacker who dealt the damage.
     * @param damage The amount of damage dealt by the attacker.
     */
    public void put(@NotNull TPlayer player, double damage) {
        long now = System.currentTimeMillis();

        // Check if the attacker already exists in the cache
        AttackerEntry oldEntry = playerMap.get(player);
        double newDamage = damage;

        if (oldEntry != null) {
            // Sum the damage
            newDamage += oldEntry.damage;
            // Remove the old entry from the damageMap
            damageMap.remove(new DamageKey(oldEntry.damage, player.getName()));
        }

        // Add the new entry with the summed damage
        AttackerEntry entry = new AttackerEntry(player, newDamage, now);
        playerMap.put(player, entry);
        damageMap.put(new DamageKey(newDamage, player.getName()), player);
    }

    /**
     * Retrieves the damage dealt by a specific attacker.
     *
     * @param player The attacker whose damage is to be retrieved.
     * @return The amount of damage dealt by the attacker, or null if the attacker is not in the cache.
     */
    public @Nullable Double getDamage(@NotNull TPlayer player) {
        AttackerEntry entry = playerMap.get(player);
        return entry != null ? entry.damage : null;
    }

    /**
     * Retrieves a list of all attackers who have dealt damage to this attacker,
     * sorted by the amount of damage dealt in descending order.
     *
     * @return Ordered list of all attackers.
     */
    public @NotNull @Unmodifiable List<AttackerEntry> getAttackers() {
        List<AttackerEntry> result = new ArrayList<>();

        for (TPlayer player : damageMap.values()) {
            AttackerEntry entry = playerMap.get(player);
            if (entry != null) {
                result.add(entry);
            }
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * Cleans up the cache by removing entries that are older than the specified TTL.
     */
    public void cleanUp() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<TPlayer, AttackerEntry>> it = playerMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<TPlayer, AttackerEntry> mapEntry = it.next();
            TPlayer player = mapEntry.getKey();
            AttackerEntry entry = mapEntry.getValue();

            if (now - entry.timestamp > ttlMillis) {
                it.remove();
                damageMap.remove(new DamageKey(entry.damage, player.getName()));
            }
        }
    }

    @Override
    public @NotNull Iterator<AttackerEntry> iterator() {
        return getAttackers().iterator();
    }

    @Override
    public void forEach(Consumer<? super AttackerEntry> action) {
        getAttackers().forEach(action);
    }

    @Override
    public Spliterator<AttackerEntry> spliterator() {
        return getAttackers().spliterator();
    }

    @Override
    public void close() {
        if (registered) {
            Manager.getInstance().unregister(this);
            registered = false;
        }
    }

    /**
     * Represents an entry in the attacker cache.
     *
     * @param attacker The attacker who dealt the damage.
     * @param damage The amount of damage dealt by the attacker.
     * @param timestamp The timestamp when the damage was dealt.
     */
    public record AttackerEntry(@NotNull TPlayer attacker, double damage, long timestamp) { }

    /**
     * Helper class to handle same damage values in the sorted map
     */
    private static class DamageKey implements Comparable<DamageKey> {
        private final Double damage;
        private final String playerName; // For tiebreaking

        public DamageKey(@NotNull Double damage, @NotNull String playerName) {
            this.damage = damage;
            this.playerName = playerName;
        }

        @Override
        public int compareTo(DamageKey other) {
            int damageCompare = damage.compareTo(other.damage);
            return damageCompare != 0 ? damageCompare : playerName.compareTo(other.playerName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DamageKey damageKey = (DamageKey) o;
            return Objects.equals(damage, damageKey.damage) &&
                    Objects.equals(playerName, damageKey.playerName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(damage, playerName);
        }
    }

    /**
     * Manages all cache instances and performs periodic cleanup.
     */
    public static class Manager {
        private static final Manager INSTANCE = new Manager();

        private final Set<OrderedAttackerCache> registeredCaches = ConcurrentHashMap.newKeySet();
        private final ScheduledExecutorService executor;

        private Manager() {
            executor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "attacker-cache-cleanup-thread");
                thread.setDaemon(true);
                return thread;
            });

            executor.scheduleAtFixedRate(this::cleanupAll, 1, 1, TimeUnit.SECONDS);
        }

        public static @NotNull Manager getInstance() {
            return INSTANCE;
        }

        public void register(@NotNull OrderedAttackerCache cache) {
            registeredCaches.add(cache);
        }

        public void unregister(@NotNull OrderedAttackerCache cache) {
            registeredCaches.remove(cache);
        }

        private void cleanupAll() {
            for (OrderedAttackerCache cache : registeredCaches) {
                try {
                    cache.cleanUp();
                } catch (Exception e) {
                    // Log exception but continue with other caches
                }
            }
        }

        public void shutdown() {
            executor.shutdown();
        }
    }

}