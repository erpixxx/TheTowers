package dev.erpix.thetowers.model.game;

import dev.erpix.thetowers.model.StatsTracker;
import dev.erpix.thetowers.util.OrderedAttackerCache;
import lombok.Getter;
import lombok.Setter;
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
@Getter
public class GamePlayer {

    @NotNull
    private final String name;
    @NotNull
    private final OrderedAttackerCache attackers = new OrderedAttackerCache(10);
    @NotNull
    private final StatsTracker stats = new StatsTracker();
    @Nullable @Setter
    private GameTeam team;
    @Setter
    private boolean isAlive = true;

    public GamePlayer(@NotNull String name) {
        this.name = name;
    }

    /**
     * Returns the Bukkit Player instance if online.
     *
     * @return An {@link Optional} containing the Bukkit Player if online, otherwise an empty.
     */
    public @NotNull Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(name));
    }

    /**
     * Executes the provided action for the Bukkit Player if they are online, otherwise fails silently.
     *
     * @param action The action to perform on the Bukkit Player.
     * @return true if the action was executed, false otherwise.
     */
    public boolean doAsBukkitPlayer(@NotNull Consumer<Player> action) {
        Optional<Player> player = getBukkitPlayer();
        player.ifPresent(action);
        return player.isPresent();
    }

    /**
     * Checks if the player is currently online.
     *
     * @return true if the player is online, false otherwise.
     */
    public boolean isOnline() {
        return getBukkitPlayer().isPresent();
    }

    /**
     * Retrieves the display name of the player, formatted with team tag and color.
     *
     * @return the formatted display name of the player.
     */
    public @NotNull String getDisplayName() {
        return getFormattedName(true);
    }

    /**
     * Retrieves the display name of the player without the team tag.
     *
     * @return the formatted display name of the player without the team tag.
     */
    public @NotNull String getDisplayNameNoTag() {
        return getFormattedName(false);
    }

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
                    team.getColor().getColorHex(), team.getName(),
                    team.getColor().getSecondaryColorHex(), name, leaderIndicator);
        } else {
            return String.format("<color:#%s>%s</color>%s",
                    team.getColor().getSecondaryColorHex(), name, leaderIndicator);
        }
    }

    /**
     * Adds an attacker to the player's {@link OrderedAttackerCache} with the specified damage.
     *
     * @param player The player who dealt the damage.
     * @param damage The amount of damage dealt by the attacker.
     */
    public void addAttacker(@NotNull GamePlayer player, double damage) {
        attackers.put(player, damage);
    }

    /**
     * Checks if the player is currently in any team.
     *
     * @return true if the player is in a team, false otherwise.
     */
    public boolean isInAnyTeam() {
        return team != null;
    }

    /**
     * Sends a message to the player.
     *
     * @param message the message to send, formatted as a rich text string.
     */
    public void sendMessage(@NotNull String message) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) return;
        player.sendRichMessage(message);
    }

    /**
     * Sends a message to the player.
     *
     * @param message the message to send, formatted as a {@link Component}.
     */
    public void sendMessage(@NotNull Component message) {
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
        return "GamePlayer{" +
                "team=" + team +
                ", name='" + name + '\'' +
                '}';
    }

}
