package dev.erpix.thetowers.model;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameSession;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.model.manager.PlayerManager;
import dev.erpix.thetowers.util.Disguises;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class DamageCalculator {

    /**
     * Private constructor to prevent instantiation.
     */
    private DamageCalculator() { }

    /**
     * Applies damage to a player without an attacker.
     *
     * @param target the entity receiving the damage
     * @param damage the amount of damage to apply
     */
    public static void applyDamage(@NotNull LivingEntity target, double damage) {
        applyDamage(target, null, damage);
    }

    /**
     * Applies and handles the damage logic for a player.
     *
     * @param target the entity receiving the damage
     * @param attacker the potential attacker, can be null if not applicable
     * @param damage the amount of damage to apply
     */
    public static void applyDamage(@NotNull LivingEntity target, @Nullable LivingEntity attacker, double damage) {
        if (damage <= 0) {
            return;
        }

        double currentHealth = target.getHealth();
        double newHealth = currentHealth - damage;

        TheTowers theTowers = TheTowers.getInstance();
        GameSession game = theTowers.getGame();
        PlayerManager playerManager = theTowers.getPlayerManager();

        // Get attacker and target TPlayer instances
        Optional<GamePlayer> attackerTPlayer = playerManager.getPlayer(
                attacker instanceof Player p ? p.getName() : "");
        Optional<GamePlayer> targetTPlayer = playerManager.getPlayer(
                target instanceof Player p ? p.getName() : "");

        if (attackerTPlayer.isPresent() && targetTPlayer.isPresent()) {
            targetTPlayer.get().addAttacker(attackerTPlayer.get(), damage);
        }

        // Check if the target is below the minimum Y
        if (target.getLocation().getY() <= -127) {
            if (target instanceof Player targetPlayer && targetTPlayer.isPresent()) {
                GameTeam team = targetTPlayer.get().getTeam();
                Location spawn = game.getMap().getTeamSpawnLocation(team.getColor());
                targetPlayer.teleport(spawn);
                game.death(targetTPlayer.get(), attacker);
            }
            else {
                target.setHealth(0);
            }
            return;
        }

        // Death handle
        if (newHealth <= 0) {
            if (target instanceof Player && targetTPlayer.isPresent()) {
                game.death(targetTPlayer.get(), attacker);
            }
            else {
                target.setHealth(0);
            }
            Location location = target.getLocation();
            location.getWorld().spawnParticle(Particle.BLOCK, location, 200, 0.5, 1.25, 0.5, Material.REDSTONE_BLOCK.createBlockData());
            return;
        }

        target.setHealth(newHealth);

        // If the target is an attacker, update their health and attacker information
        if (target instanceof Player targetPlayer) {
            Disguises.refresh(targetPlayer);
        }
    }

    /**
     * Calculate the damage dealt based on the base damage and defense.
     *
     * @param baseDamage the base damage value
     * @param defense the defense value to reduce the damage
     * @return the calculated damage, ensuring it is not negative
     */
    public static double calculateDamage(double baseDamage, double defense) {
        if (baseDamage <= 0) {
            return 0;
        }
        if (defense < 0) {
            defense = 0;
        }

        double damage = baseDamage * baseDamage / (baseDamage + defense);
        return Math.max(damage, 0);
    }

    /**
     * Retrieve the damage value from an ItemStack's persistent data container.
     *
     * @param item the ItemStack to retrieve the damage from
     * @return the damage value, or 0 if the item is null or does not have a damage value set
     */
    public static double getDamageFromItem(@Nullable ItemStack item) {
        if (item == null) {
            return 0;
        }

        // Default damage for empty item slot
        if (item.getType() == Material.AIR) {
            return 1;
        }

        PersistentDataContainerView data = item.getPersistentDataContainer();
        Double damage = data.get(TheTowers.key("damage"), PersistentDataType.DOUBLE);
        if (damage == null) {
            return 0;
        }

        return damage;
    }

    /**
     * Retrieve the defense value from an ItemStack's persistent data container.
     *
     * @param item the ItemStack to retrieve the defense from
     * @return the defense value, or 0 if the item is null or does not have a defense value set
     */
    public static double getDefenseFromItem(@Nullable ItemStack item) {
        if (item == null) {
            return 0;
        }

        PersistentDataContainerView data = item.getPersistentDataContainer();
        Double defense = data.get(TheTowers.key("defense"), PersistentDataType.DOUBLE);
        if (defense == null) {
            return 0;
        }

        return defense;
    }

    public static double calculateEquipmentDefense(@NotNull LivingEntity entity) {
        EntityEquipment entityEq = entity.getEquipment();
        if (entityEq != null) {
            return DamageCalculator.getDefenseFromItem(entityEq.getChestplate())
                    + DamageCalculator.getDefenseFromItem(entityEq.getLeggings())
                    + DamageCalculator.getDefenseFromItem(entityEq.getBoots())
                    + DamageCalculator.getDefenseFromItem(entityEq.getHelmet());
        }
        return 0;
    }

}
