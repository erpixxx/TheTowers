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

    PlayerStat PLAY_TIME = Registry.register("play_time");

    PlayerStat KILLS = Registry.register("kills");
    PlayerStat DEATHS = Registry.register("deaths");
    PlayerStat ASSISTS = Registry.register("assists");
    PlayerStat HEART_DAMAGE = Registry.register("heart_damage");
    PlayerStat TOWERS_DESTROYED = Registry.register("towers_destroyed");

    PlayerStat COAL_MINED = Registry.register("coal_mined");
    PlayerStat COPPER_MINED = Registry.register("copper_mined");
    PlayerStat IRON_MINED = Registry.register("iron_mined");
    PlayerStat GOLD_MINED = Registry.register("gold_mined");
    PlayerStat DIAMOND_MINED = Registry.register("diamond_mined");
    PlayerStat EMERALD_MINED = Registry.register("emerald_mined");
    PlayerStat LAPIS_MINED = Registry.register("lapis_mined");
    PlayerStat AMETHYST_MINED = Registry.register("amethyst_mined");
    PlayerStat QUARTZ_MINED = Registry.register("quartz_mined");
    PlayerStat NETHERITE_MINED = Registry.register("netherite_mined");

    PlayerStat BLOCKS_PLACED = Registry.register("blocks_placed");
    PlayerStat BLOCKS_BROKEN = Registry.register("blocks_broken");

    PlayerStat WOOD_GATHERED = Registry.register("wood_gathered");

    PlayerStat CARROT_HARVESTED = Registry.register("carrot_harvested");
    PlayerStat MELON_HARVESTED = Registry.register("melon_harvested");
    PlayerStat POTATO_HARVESTED = Registry.register("potato_harvested");
    PlayerStat WHEAT_HARVESTED = Registry.register("wheat_harvested");

    PlayerStat FISH_CAUGHT = Registry.register("fish_caught");

    PlayerStat ENCHANTMENTS_APPLIED = Registry.register("enchantments_applied");
    PlayerStat SUPPLY_CRATES_OPENED = Registry.register("supply_crates_opened");

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
    static @NotNull @Unmodifiable Collection<PlayerStat> values() {
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

}
