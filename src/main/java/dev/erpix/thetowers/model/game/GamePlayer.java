package dev.erpix.thetowers.model.game;

import dev.erpix.thetowers.model.GameStatKey;
import dev.erpix.thetowers.model.StatsTracker;
import dev.erpix.thetowers.util.OrderedAttackerCache;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a player in the game.
 */
public class GamePlayer {

    private final String name;
    private final OrderedAttackerCache attackers = new OrderedAttackerCache(10);
    private final GameStats stats = new GameStats();
    private GameTeam team;
    private boolean isAlive = true;

    public GamePlayer(@NotNull String name) {
        this.name = name;
    }

    /**
     * Returns the Bukkit Player instance if online, otherwise returns an empty Optional.
     *
     * @return Optional containing the Bukkit Player if online, otherwise empty.
     */
    public @NotNull Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(name));
    }

    /**
     * Executes the provided action for the Bukkit Player if they are online, otherwise fails silently.
     *
     * @param action The action to perform on the Bukkit Player.
     * @return true if the attacker is online and the action was executed, false otherwise.
     */
    public boolean doAsBukkitPlayer(@NotNull Consumer<Player> action) {
        Optional<Player> player = getBukkitPlayer();
        player.ifPresent(action);
        return player.isPresent();
    }

    /**
     * Checks if the player is currently online.
     *
     * @return true if the attacker is online, false otherwise.
     */
    public boolean isOnline() {
        return getBukkitPlayer().isPresent();
    }

    /**
     * Gets the name of the player.
     *
     * @return The name of the player.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the display name of the attacker, formatted with team tag and color.
     *
     * @return The formatted display name of the player.
     */
    public @NotNull String getDisplayName() {
        return getFormattedName(true);
    }

    /**
     * Gets the display name of the player without the team tag.
     *
     * @return The formatted display name of the player without the team tag.
     */
    public @NotNull String getDisplayNameNoTag() {
        return getFormattedName(false);
    }

    // Helper method to format the player's name with or without team tag
    private @NotNull String getFormattedName(boolean includeTag) {
        if (!isOnline()) {
            return "<dark_gray>" + name + "</dark_gray>";
        }
        if (team == null) {
            return "<gray>" + name + "</gray>";
        }

        String leaderIndicator = team.getLeader() == this ? " <yellow>👑</yellow>" : "";
        if (includeTag) {
            return String.format("<color:#%s>[%s]</color> <color:#%s>%s</color>%s",
                    team.getColor().getColorHex(), team.getTag(),
                    team.getColor().getSecondaryColorHex(), name, leaderIndicator);
        } else {
            return String.format("<color:#%s>%s</color>%s",
                    team.getColor().getSecondaryColorHex(), name, leaderIndicator);
        }
    }

    /**
     * Gets the cache of all recent players who have dealt damage to this player
     * within the last 10 seconds, sorted by damage dealt in descending order.
     *
     * @return The cache of attackers.
     */
    public @NotNull OrderedAttackerCache getAttackers() {
        return attackers;
    }

    /**
     * Adds an attacker to the player's attacker cache with the specified damage.
     *
     * @param player The attacker who dealt the damage.
     * @param damage The amount of damage dealt by the attacker.
     */
    public void addAttacker(@NotNull GamePlayer player, double damage) {
        attackers.put(player, damage);
    }

    /**
     * Gets the statistics container for this player.
     *
     * @return The stats container containing various in-game statistics for the player.
     */
    public @NotNull StatsTracker<GameStatKey> getStats() {
        return stats;
    }

    /**
     * Gets the team this player is currently on.
     *
     * @return The team of the player, or null if not on a team.
     */
    public @Nullable GameTeam getTeam() {
        return team;
    }

    /**
     * Checks if the player is currently in a team.
     *
     * @return true if the player is in a team, false otherwise.
     */
    public boolean isInTeam() {
        return team != null;
    }

    /**
     * Sets the team for this player.
     *
     * @param team The team to set for the player.
     */
    public void setTeam(@Nullable GameTeam team) {
        this.team = team;
    }

    /**
     * Checks if the player is currently alive.
     *
     * @return true if the player is alive, false otherwise.
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Sets the alive status of the player.
     *
     * @param alive true to set the player as alive, false to set as dead.
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * Sends a message to the player.
     *
     * @param message The message to send, can be plain text or rich text.
     */
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) return;
        player.sendRichMessage(message);
    }

    /**
     * Sends a message to the player.
     *
     * @param message The message to send as a Component.
     */
    public void sendMessage(Component message) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) return;
        player.sendMessage(message);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayer gamePlayer = (GamePlayer) o;
        return Objects.equals(name, gamePlayer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "TPlayer{" +
                "team=" + team +
                ", name='" + name + '\'' +
                '}';
    }

}
