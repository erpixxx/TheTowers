package dev.erpix.thetowers.model.game;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.GameStatKey;
import dev.erpix.thetowers.util.Components;
import dev.erpix.thetowers.util.OrderedAttackerCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a game in The Towers.
 */
public class GameSession {

    public static final int MAX_PLAYERS = 32;

    private final Map<String, GameTeam> teams = new LinkedHashMap<>();
    private final Map<String, GamePlayer> spectators = new HashMap<>();
    private GameMap map;
    private int maxPlayersPerTeam;
    private Stage stage;
    private LocalDateTime startTime;

    public GameSession() {
        this.stage = Stage.LOBBY;
    }

    /**
     * Retrieves the current chosen map in the game.
     *
     * @return the current {@link GameMap} object representing the game's map.
     */
    public @NotNull GameMap getMap() {
        return map;
    }

    /**
     * Sets the map for the game and updates the maximum players per team based on the map's team setup.
     * Removes any teams that exceed the maximum allowed players per team.
     *
     * @param map the {@link GameMap} object representing the new map for the game.
     */
    public void setMap(@NotNull GameMap map) {
        this.map = map;
        this.maxPlayersPerTeam = GameMap.TeamSetup.getMaxPlayersInTeam(map.getTeamSetup());
        for (GameTeam team : teams.values()) {
            Collection<GamePlayer> members = team.getMembers();
            if (members.size() > maxPlayersPerTeam) {
                removeTeam(team);
            }
        }
        // TODO: Update placeholder %tt_game_team_setup%, %tt_game_team_map%, %tt_game_teams_ready%, %tt_game_teams_count%
    }

    /**
     * Retrieves the maximum number of players allowed per team.
     *
     * @return the maximum number of players allowed per team.
     */
    public int getMaxPlayersPerTeam() {
        return maxPlayersPerTeam;
    }

    /**
     * Sets the maximum number of players allowed per team.
     *
     * @param maxPlayersPerTeam the maximum number of players to set for each team.
     */
    public void setMaxPlayersPerTeam(int maxPlayersPerTeam) {
        this.maxPlayersPerTeam = maxPlayersPerTeam;
    }

    /**
     * Adds a team to the game.
     *
     * @param team the {@link GameTeam} object representing the team to add.
     */
    public void addTeam(@NotNull GameTeam team) {
        teams.put(team.getTag(), team);
    }

    /**
     * Removes all teams from the game.
     */
    public void clearTeams() {
        teams.values().forEach(this::removeTeam);
    }

    /**
     * Retrieves a team by its tag.
     *
     * @param tag the unique identifier of the team.
     * @return the {@link GameTeam} object representing the team, or null if no team with the given tag exists.
     */
    public @Nullable GameTeam getTeam(@NotNull String tag) {
        return teams.get(tag);
    }

    /**
     * Retrieves a team by its color.
     *
     * @param color the color of the team to retrieve.
     * @return the {@link GameTeam} object representing the team with the specified color, or null if no such team exists.
     */
    public @Nullable GameTeam getTeam(@NotNull GameTeam.Color color) {
        return teams.values().stream()
                .filter(team -> team.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a collection of all teams currently in the game.
     *
     * @return a collection of teams in the game.
     */
    public @NotNull @Unmodifiable Collection<GameTeam> getTeams() {
        return Collections.unmodifiableCollection(teams.values());
    }

    /**
     * Removes a team from the game and converts its members to spectators.
     *
     * @param team the {@link GameTeam} to remove.
     */
    public void removeTeam(@NotNull GameTeam team) {
        teams.remove(team.getTag());
        Collection<GamePlayer> members = team.getMembers();
        members.forEach(member -> {
            member.sendMessage("<gray>Twoja drużyna została usunięta.");
            member.setTeam(null);
            addSpectator(member);
        });
    }

    /**
     * Adds a player to the spectators list.
     *
     * @param player the {@link GamePlayer} to add as a spectator.
     */
    public void addSpectator(@NotNull GamePlayer player) {
        spectators.put(player.getName(), player);
        player.setAlive(false);
    }

    /**
     * Removes a player from the spectators list.
     *
     * @param player the {@link GamePlayer} to remove from the spectators list.
     */
    public void removeSpectator(@NotNull GamePlayer player) {
        spectators.remove(player.getName());
        player.setAlive(true);
    }

    /**
     * Retrieves a collection of all spectators currently in the game.
     *
     * @return a collection of all spectators in the game.
     */
    public @NotNull @Unmodifiable Collection<GamePlayer> getSpectators() {
        return spectators.values();
    }

    /**
     * Retrieves the current stage of the game.
     *
     * @return the current {@link Stage} of the game.
     */
    public @NotNull Stage getStage() {
        return stage;
    }

    /**
     * Sets the current stage of the game.
     *
     * @param stage the {@link Stage} to set as the current stage of the game.
     */
    public void setStage(@NotNull Stage stage) {
        this.stage = stage;
    }

    // TODO
    public void start() {
        if (stage == Stage.LOBBY) {
            Bukkit.broadcast(Components.standard("<green>Rozpoczynanie nowej gry...<br>"));
            stage = Stage.WAITING;

            teams.forEach((k, v) -> v.getMembers().forEach(member -> member.doAsBukkitPlayer(player -> {
                player.teleport(map.getWaitingRoomLocation());
                player.setGameMode(GameMode.ADVENTURE);
                Title.Times times = Title.Times.times(Ticks.duration(8), Ticks.duration(100), Ticks.duration(8));
                Title title = Title.title(Component.empty(), Components.color("<gray>Grasz jako drużyna " + v.getDisplayName()), times);
                Bukkit.getScheduler().runTaskLater(TheTowers.getInstance().getPlugin(), () -> {
                    player.showTitle(title);
                    player.playSound(player, Sound.UI_TOAST_IN, SoundCategory.MASTER, 1.0f, 1.0f);
                }, 20);
                Bukkit.getScheduler().runTaskLater(TheTowers.getInstance().getPlugin(), () ->
                        player.playSound(player, Sound.UI_TOAST_OUT, SoundCategory.MASTER, 1.0f, 1.0f), 120);
                player.sendMessage(Components.standard("<gray>Wybrana mapa: <white>" + map.getName()));
                player.sendMessage(Components.standard("<gray>Konfiguracja gry: <white>" + map.getTeamSetup().getFormattedTeamSetup()));
                player.sendMessage(Components.standard("<br><aqua>Gra rozpocznie się za 30 sekund!<br>"));
            })));
        } else {
            throw new IllegalStateException("Game cannot be started in the current stage: " + stage);
        }
    }

    /**
     * Stops the game and resets the stage to LOBBY.
     */
    // TODO
    public void stop() {
        stage = Stage.LOBBY;
    }

    /**
     * Handles the death of a player in the game.
     *
     * @param victim   the {@link GamePlayer} who died.
     * @param attacker the {@link Entity} responsible for the death.
     */
    public void death(@NotNull GamePlayer victim, @NotNull Entity attacker) {
        Optional<Player> playerOpt = victim.getBukkitPlayer();
        if (playerOpt.isEmpty()) return;
        Player victimPlayer = playerOpt.get();
        victim.getStats().incrementStat(GameStatKey.DEATHS);
        victim.setAlive(false);

        victimPlayer.setGameMode(GameMode.SPECTATOR);
        playLightningSound();

        if (attacker instanceof Player attackerPlayer) {
            handlePlayerAttacker(victim, victimPlayer, attackerPlayer);
        } else {
            handleNonPlayerAttacker(victim, attacker);
        }

        respawn(victimPlayer);
    }

    /**
     * Plays a lightning sound effect for all online players.
     */
    private void playLightningSound() {
        Bukkit.getOnlinePlayers().forEach(pl -> {
            Location location = pl.getLocation();
            location.add(0, 100, 0);
            pl.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, Float.MAX_VALUE, 1.0f);
        });
    }

    /**
     * Handles the death caused by another player.
     *
     * <p>Responsible for updating the victim's and attacker's stats.</p>
     *
     * @param victim         the {@link GamePlayer} who died.
     * @param victimPlayer   the bukkit {@link Player} of the victim.
     * @param attackerPlayer the bukkit {@link Player} of the attacker.
     */
    private void handlePlayerAttacker(@NotNull GamePlayer victim, @NotNull Player victimPlayer, @NotNull Player attackerPlayer) {
        TheTowers.getInstance().getPlayerManager().getPlayer(attackerPlayer.getName()).ifPresent(attackerTPlayer -> {
            victim.getAttackers().forEach(atk -> {
                if (!atk.attacker().equals(attackerTPlayer)) {
                    atk.attacker().getStats().incrementStat(GameStatKey.ASSISTS);
                }
            });
            victim.getAttackers().cleanUp();
            attackerTPlayer.getStats().incrementStat(GameStatKey.KILLS);
            attackerTPlayer.getTeam().addSouls(1);
            broadcastDeathMessage(victim, attackerTPlayer.getDisplayName());
            attackerPlayer.playSound(victimPlayer, Sound.BLOCK_METAL_BREAK, 1.0f, 1.0f);
        });
    }

    /**
     * Handles the death caused by a non-player entity.
     *
     * @param victim   the {@link GamePlayer} who died.
     * @param attacker the {@link Entity} responsible for the death, or null if unknown.
     */
    private void handleNonPlayerAttacker(@NotNull GamePlayer victim, @Nullable Entity attacker) {
        Component attackerName = attacker != null ? attacker.customName() : null;
        if (attackerName != null) {
            broadcastDeathMessage(victim, PlainTextComponentSerializer.plainText().serialize(attackerName));
        } else {
            // fallback to last attacker or general death
            Optional<OrderedAttackerCache.AttackerEntry> lastAttacker = victim.getAttackers().getAttackers().stream().findFirst();
            if (lastAttacker.isPresent()) {
                broadcastDeathMessage(victim, lastAttacker.get().attacker().getDisplayName());
            } else {
                broadcastDeathMessage(victim, null);
            }
        }
    }

    /**
     * Broadcasts a death message to all online players.
     *
     * @param victim       the {@link GamePlayer} who died.
     * @param attackerName the name of the attacker, or null if no attacker is available.
     */
    private void broadcastDeathMessage(@NotNull GamePlayer victim, @Nullable String attackerName) {
        String message = attackerName == null
                ? String.format("<gray>☠ <dark_gray>» <red>%s umarł", victim.getDisplayName())
                : String.format("<gray>☠ <dark_gray>» <red>%s został zabity przez %s",
                victim.getDisplayName(), attackerName);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(Components.standard(message)));
    }

    /**
     * Initiates the respawn process for a player.
     *
     * <p>Handles the countdown and teleportation back to the player's team spawn.</p>
     *
     * @param player the {@link Player} to respawn
     */
    private void respawn(@NotNull Player player) {
        GamePlayer gamePlayer = getPlayer(player.getName());
        if (gamePlayer == null) return;

        // Start the respawn countdown
        AtomicInteger i = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskTimerAsynchronously(TheTowers.getInstance().getPlugin(), task -> {
            if (i.get() < 5) {
                int seconds = 5 - i.getAndIncrement();
                Component subtitle = Components.standard("<gray>Odrodzisz się za: <white>" + seconds + "</white>s");
                Title.Times times = Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(0)
                );
                Title title = Title.title(Component.empty(), subtitle, times);
                player.showTitle(title);
                player.playSound(player, Sound.BLOCK_LEVER_CLICK, 1.0f, 1.0f);
            }
            else {
                Bukkit.getScheduler().runTask(TheTowers.getInstance().getPlugin(), () -> {
                    gamePlayer.setAlive(true);
                    player.heal(20);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(map.getTeamSpawnLocations().get(gamePlayer.getTeam().getColor()));
                    player.sendMessage(Components.color("<green>Odrodziłeś się!"));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
                });
                task.cancel();
            }
        }, 5 * Ticks.TICKS_PER_SECOND, Ticks.TICKS_PER_SECOND);
    }

    /**
     * Retrieves a player by their name.
     *
     * @param name the name of the player to retrieve.
     * @return the {@link GamePlayer} object if found, or null if no player with the given name exists.
     */
    public @Nullable GamePlayer getPlayer(@NotNull String name) {
        return teams.values().stream()
                .flatMap(team -> team.getMembers().stream())
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a player is part of any team in the game.
     *
     * @param player the {@link GamePlayer} to check.
     * @return true if the player is in a team, false otherwise.
     */
    public boolean isPlayerInGame(@NotNull GamePlayer player) {
        return teams.values().stream()
                .anyMatch(team -> team.hasMember(player));
    }

    /**
     * Represents the different stages of a game.
     */
    public enum Stage {
        LOBBY(false),
        WAITING(false),
        IN_PROGRESS(true),
        FINISHED(false);

        private final boolean canPvp;

        Stage(boolean canPvp) {
            this.canPvp = canPvp;
        }

        public boolean canPvp() {
            return canPvp;
        }
    }

}
