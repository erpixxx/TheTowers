package dev.erpix.thetowers.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the keys for various profile statistics.
 * Each key corresponds to a specific type of statistic that can be tracked.
 */
public enum ProfileStatKey implements TypedStat {

    GAMES_PLAYED("games_played"),
    WINS("wins"),
    LOSSES("losses"),

    PLAY_TIME("play_time"),
    KILLS("kills"),
    DEFENSE_KILLS("defense_kills"),
    ASSISTS("assists"),
    DEATHS("deaths"),
    HEART_DAMAGE("heart_damage"),
    TOWERS_DESTROYED("towers_destroyed"),

    COAL_MINED("coal_mined"),
    COPPER_MINED("copper_mined"),
    IRON_MINED("iron_mined"),
    GOLD_MINED("gold_mined"),
    DIAMOND_MINED("diamond_mined"),
    EMERALD_MINED("emerald_mined"),
    LAPIS_MINED("lapis_mined"),
    NETHERITE_MINED("netherite_mined"),
    QUARTZ_MINED("quartz_mined"),
    AMETHYST_MINED("amethyst_mined"),

    WOOD_GATHERED("wood_gathered"),

    MELONS_HARVESTED("melons_harvested"),
    WHEAT_HARVESTED("wheat_harvested"),
    CARROTS_HARVESTED("carrots_harvested"),
    POTATOES_HARVESTED("potatoes_harvested"),

    FISH_CAUGHT("fish_caught"),

    ENCHANTMENTS_APPLIED("enchantments_applied"),
    SUPPLY_CRATES_OPENED("supply_crates_opened");

    private final String key;

    ProfileStatKey(@NotNull String key) {
        this.key = key;
    }

    public @NotNull String getKey() {
        return key;
    }

    public static @Nullable ProfileStatKey fromKey(@NotNull String key) {
        for (ProfileStatKey type : values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return null;
    }

}
