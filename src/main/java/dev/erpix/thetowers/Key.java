package dev.erpix.thetowers;

import org.bukkit.NamespacedKey;

public enum Key {

    DEFENSE(TheTowers.key("defense")),
    MELEE_DAMAGE(TheTowers.key("melee_damage")),
    MELEE_SPEED(TheTowers.key("attack_speed")),
    PROJECTILE_DAMAGE(TheTowers.key("projectile_damage"));

    private final NamespacedKey key;

    Key(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey key() {
        return key;
    }

}
