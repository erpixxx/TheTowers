package dev.erpix.thetowers;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Enum that defines the {@link NamespacedKey} for various in-game attributes.
 */
public enum AttributeKey {

    DEFENSE(TheTowers.key("defense")),
    MELEE_DAMAGE(TheTowers.key("melee_damage")),
    MELEE_SPEED(TheTowers.key("attack_speed")),
    PROJECTILE_DAMAGE(TheTowers.key("projectile_damage"));

    private final NamespacedKey key;

    AttributeKey(NamespacedKey key) {
        this.key = key;
    }

    /**
     * Returns the underlying namespaced key associated with the game attribute.
     *
     * @return the {@link NamespacedKey} for the game attribute.
     */
    public @NotNull NamespacedKey key() {
        return key;
    }

}
