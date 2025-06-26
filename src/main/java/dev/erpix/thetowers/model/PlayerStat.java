package dev.erpix.thetowers.model;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Represents a type of statistic associated with a player.
 */
public interface PlayerStat {

    PlayerStat PLAY_TIME = Registry.register(RegistryKey.PLAY_TIME);

    PlayerStat KILLS = Registry.register(RegistryKey.KILLS);
    PlayerStat DEATHS = Registry.register(RegistryKey.DEATHS);
    PlayerStat ASSISTS = Registry.register(RegistryKey.ASSISTS);
    PlayerStat HEART_DAMAGE = Registry.register(RegistryKey.HEART_DAMAGE);
    PlayerStat TOWERS_DESTROYED = Registry.register(RegistryKey.TOWERS_DESTROYED);

    PlayerStat COAL_MINED = Registry.register(RegistryKey.COAL_MINED);
    PlayerStat COPPER_MINED = Registry.register(RegistryKey.COPPER_MINED);
    PlayerStat IRON_MINED = Registry.register(RegistryKey.IRON_MINED);
    PlayerStat GOLD_MINED = Registry.register(RegistryKey.GOLD_MINED);
    PlayerStat DIAMOND_MINED = Registry.register(RegistryKey.DIAMOND_MINED);
    PlayerStat EMERALD_MINED = Registry.register(RegistryKey.EMERALD_MINED);
    PlayerStat LAPIS_MINED = Registry.register(RegistryKey.LAPIS_MINED);
    PlayerStat AMETHYST_MINED = Registry.register(RegistryKey.AMETHYST_MINED);
    PlayerStat QUARTZ_MINED = Registry.register(RegistryKey.QUARTZ_MINED);
    PlayerStat NETHERITE_MINED = Registry.register(RegistryKey.NETHERITE_MINED);

    PlayerStat WOOD_GATHERED = Registry.register(RegistryKey.WOOD_GATHERED);

    PlayerStat CARROT_HARVESTED = Registry.register(RegistryKey.CARROT_HARVESTED);
    PlayerStat MELON_HARVESTED = Registry.register(RegistryKey.MELON_HARVESTED);
    PlayerStat POTATO_HARVESTED = Registry.register(RegistryKey.POTATO_HARVESTED);
    PlayerStat WHEAT_HARVESTED = Registry.register(RegistryKey.WHEAT_HARVESTED);

    PlayerStat FISH_CAUGHT = Registry.register(RegistryKey.FISH_CAUGHT);

    PlayerStat ENCHANTMENTS_APPLIED = Registry.register(RegistryKey.ENCHANTMENTS_APPLIED);
    PlayerStat SUPPLY_CRATES_OPENED = Registry.register(RegistryKey.SUPPLY_CRATES_OPENED);

    PlayerStat BLOCKS_PLACED = Registry.register(RegistryKey.BLOCKS_PLACED);
    PlayerStat BLOCKS_BROKEN = Registry.register(RegistryKey.BLOCKS_BROKEN);

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
     * Returns all registered {@link PlayerStat} instances.
     *
     * @return an unmodifiable collection of all player statistics.
     */
    static @NotNull @Unmodifiable Collection<PlayerStat> stats() {
        return Registry.getAll();
    }

    /**
     * Returns the key associated with this statistic type.
     *
     * @return the key.
     */
    @NotNull String getKey();

    class PlayerStatImpl implements PlayerStat {

        private final String key;

        public PlayerStatImpl(@NotNull String key) {
            this.key = key;
        }

        @Override
        public @NotNull String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PlayerStatImpl that)) return false;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }

        @Override
        public String toString() {
            return key;
        }

    }

    class Adapter implements JsonSerializer<PlayerStat>, JsonDeserializer<PlayerStat> {

        @Override
        public PlayerStat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String key = json.getAsString();
            PlayerStat stat = PlayerStat.Registry.fromKey(key);
            if (stat == null) {
                throw new JsonParseException("Unknown PlayerStat key: " + key);
            }
            return stat;
        }

        @Override
        public JsonElement serialize(PlayerStat src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getKey());
        }

    }

    final class Registry {

        private static final Map<String, PlayerStat> REGISTRY = new LinkedHashMap<>();

        private Registry() { }

        static PlayerStat register(String key) {
            PlayerStat stat = new PlayerStatImpl(key);
            REGISTRY.put(key, stat);
            return stat;
        }

        static PlayerStat fromKey(String key) {
            return REGISTRY.get(key);
        }

        static Collection<PlayerStat> getAll() {
            return Collections.unmodifiableCollection(REGISTRY.values());
        }
    }

    interface RegistryKey {
        String PLAY_TIME = "play_time";
        String KILLS = "kills";
        String DEATHS = "deaths";
        String ASSISTS = "assists";
        String HEART_DAMAGE = "heart_damage";
        String TOWERS_DESTROYED = "towers_destroyed";
        String COAL_MINED = "coal_mined";
        String COPPER_MINED = "copper_mined";
        String IRON_MINED = "iron_mined";
        String GOLD_MINED = "gold_mined";
        String DIAMOND_MINED = "diamond_mined";
        String EMERALD_MINED = "emerald_mined";
        String LAPIS_MINED = "lapis_mined";
        String AMETHYST_MINED = "amethyst_mined";
        String QUARTZ_MINED = "quartz_mined";
        String NETHERITE_MINED = "netherite_mined";
        String WOOD_GATHERED = "wood_gathered";
        String CARROT_HARVESTED = "carrot_harvested";
        String MELON_HARVESTED = "melon_harvested";
        String POTATO_HARVESTED = "potato_harvested";
        String WHEAT_HARVESTED = "wheat_harvested";
        String FISH_CAUGHT = "fish_caught";
        String ENCHANTMENTS_APPLIED = "enchantments_applied";
        String SUPPLY_CRATES_OPENED = "supply_crates_opened";
        String BLOCKS_PLACED = "blocks_placed";
        String BLOCKS_BROKEN = "blocks_broken";
    }

}
