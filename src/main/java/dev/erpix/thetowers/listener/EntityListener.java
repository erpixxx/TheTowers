package dev.erpix.thetowers.listener;

import dev.erpix.thetowers.AttributeKey;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.*;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.PlayerManager;
import dev.erpix.thetowers.util.DisguiseHandler;
import dev.erpix.thetowers.util.MathUtil;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class EntityListener implements Listener {

    private final TheTowers theTowers = TheTowers.getInstance();

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        Entity entity = event.getEntity();

        if (event instanceof EntityDamageByEntityEvent) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            event.setCancelled(true);
            return;
        }

        // Check if the entity has invulnerability ticks
        if (isInvulnerable(livingEntity)) {
            event.setCancelled(true);
            return;
        }

        DamageCalculator.applyDamage(livingEntity, event.getFinalDamage());

        // Prevent default damage handling
        event.setDamage(0);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        GameManager game = theTowers.getGameManager();
        PlayerManager playerManager = theTowers.getPlayerManager();
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        double damage = 0;
        double defense = 0;

        // Cancel for non-living entities
        if (!(entity instanceof LivingEntity victim)) {
            event.setCancelled(true);
            return;
        }

        // Check if PvP is allowed in current game stage
        if (!game.getStage().canPvp()) {
            event.setCancelled(true);
            return;
        }

        // Check if the entity has invulnerability ticks
        if (isInvulnerable(victim)) {
            event.setCancelled(true);
            return;
        }

        // Handle arrow damage
        if (!(damager instanceof LivingEntity attacker)) {
            if (!(damager instanceof Arrow arrow)) {
                event.setCancelled(true);
                return;
            }
            if (!(arrow.getShooter() instanceof LivingEntity shooter)) {
                event.setCancelled(true);
                return;
            }
            PersistentDataContainer arrowData = arrow.getPersistentDataContainer();
            Double projDamage = arrowData.get(AttributeKey.PROJECTILE_DAMAGE.key(), PersistentDataType.DOUBLE);
            if (projDamage == null) {
                event.setCancelled(true);
                return;
            }

            damage = projDamage;
            if (arrow.isCritical()) {
                damage *= 1.5;
            }

            defense += DamageCalculator.calculateEquipmentDefense(victim);
            double finalDamage = DamageCalculator.calculateDamage(damage, defense);

            DamageCalculator.applyDamage(victim, shooter, finalDamage);

            event.setDamage(0.0);
            return;
        }

        // Get attacker and victim TPlayer instances
        Optional<GamePlayer> attackerTPlayer = playerManager.getPlayer(
                attacker instanceof Player p ? p.getName() : "");
        Optional<GamePlayer> victimTPlayer = playerManager.getPlayer(
                victim instanceof Player p ? p.getName() : "");

        // Prevent friendly fire
        if (attackerTPlayer.isPresent() && victimTPlayer.isPresent()) {
            if (attackerTPlayer.get().getTeam() == victimTPlayer.get().getTeam()) {
                event.setCancelled(true);
                return;
            }
        }

        // Defense calculation
        defense += DamageCalculator.calculateEquipmentDefense(victim);

        // Get mob damage
        if (attacker instanceof Monster monster) {
            PersistentDataContainer persistent = monster.getPersistentDataContainer();
            damage = persistent.getOrDefault(TheTowers.key("mob_damage"), PersistentDataType.DOUBLE, 1.0);
        }

        // Get weapon damage
        EntityEquipment attackerEq = attacker.getEquipment();
        if (attackerEq != null) {
            ItemStack item = attackerEq.getItemInMainHand();
            damage += DamageCalculator.getDamageFromItem(item);
        }

        // Check for critical hits
        if (event.isCritical())
            damage *= 1.5;

        // Check attack cooldown
        if (attacker instanceof Player attackerPlayer) {
            float cooldown = attackerPlayer.getAttackCooldown();
            damage *= cooldown;
        }

        // Prevent default damage handling
        event.setDamage(0.0);

        // Calculate and apply final damage
        double finalDamage = DamageCalculator.calculateDamage(damage, defense);
        DamageCalculator.applyDamage(victim, attacker, finalDamage);
    }

    /**
     * Checks if the entity is invulnerable.
     *
     * @param entity The entity to check.
     * @return True if the entity is invulnerable, false otherwise.
     */
    private boolean isInvulnerable(LivingEntity entity) {
        entity.setMaximumNoDamageTicks(10);
        return entity.getNoDamageTicks() > 0;
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            double hp = Math.min(MathUtil.round(player.getHealth() + event.getAmount(), 1), 20.0);
            DisguiseHandler.refresh(player, hp);
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (bow == null || bow.getType() == Material.AIR) {
            return;
        }

        PersistentDataContainerView bowData = bow.getPersistentDataContainer();
        Double damage = bowData.get(AttributeKey.PROJECTILE_DAMAGE.key(), PersistentDataType.DOUBLE);
        if (damage == null) {
            event.setCancelled(true);
            return;
        }

        // (Bullshit, it's not up to 1.0 as Bukkit documentation suggests)
        damage *= event.getForce();

        Entity projectile = event.getProjectile();
        PersistentDataContainer data = projectile.getPersistentDataContainer();
        data.set(AttributeKey.PROJECTILE_DAMAGE.key(), PersistentDataType.DOUBLE, damage);
    }

}
