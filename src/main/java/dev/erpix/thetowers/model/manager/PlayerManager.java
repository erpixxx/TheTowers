package dev.erpix.thetowers.model.manager;

import dev.erpix.thetowers.model.game.GamePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Manages in-game player entities during runtime.
 *
 * <p>This class provides a central registry for all players currently active in the game.</p>
 */
public class PlayerManager {

    private final Map<String, GamePlayer> players = new HashMap<>();

    // TODO
    public @NotNull GamePlayer addPlayer(@NotNull Player player) {
        String playerName = player.getName();
        if (players.containsKey(playerName)) {
            return players.get(playerName);
        }
        GamePlayer gamePlayer = new GamePlayer(playerName);
        players.put(playerName, gamePlayer);
        return gamePlayer;
    }

    /**
     * Retrieves a player by name.
     *
     * @param name the name of the player to retrieve.
     * @return an {@link Optional} containing the player if found, or empty if not.
     */
    public @NotNull Optional<GamePlayer> getPlayer(@NotNull String name) {
        return Optional.ofNullable(players.get(name));
    }

    /**
     * Checks if a player exists in the manager.
     *
     * @param name the name of the player to check.
     * @return true if the player exists, false otherwise.
     */
    public boolean hasPlayer(@NotNull String name) {
        return players.containsKey(name);
    }

    /**
     * Removes a player from the manager.
     *
     * @param name the name of the player to remove.
     */
    public void removePlayer(@NotNull String name) {
        players.remove(name);
    }

    /**
     * Retrieves a collection of all active players.
     *
     * @return a collection of all active players.
     */
    public @NotNull @Unmodifiable Collection<GamePlayer> getPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

}
