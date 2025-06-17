package dev.erpix.thetowers.model.game;

import lombok.Getter;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a map in the game.
 */
public class GameMap {

    @NotNull @Getter
    private final String name;
    @NotNull @Getter
    private final TeamSetup teamSetup;
    @NotNull @Getter
    private final Location waitingRoomLocation;
    private final Map<GameTeam.Color, Location> teamSpawnLocations;
    private final Map<GameTeam.Color, Location> teamHeartLocation;
    @NotNull @Getter
    private final World world;

    public GameMap(@NotNull String name,
                   @NotNull TeamSetup teamSetup,
                   @NotNull Location waitingRoomLocation,
                   @NotNull Map<GameTeam.Color, Location> teamSpawnLocations,
                   @NotNull Map<GameTeam.Color, Location> teamHeartLocation,
                   @NotNull World world) {
        this.name = name;
        this.teamSetup = teamSetup;
        this.waitingRoomLocation = waitingRoomLocation;
        this.teamSpawnLocations = teamSpawnLocations;
        this.teamHeartLocation = teamHeartLocation;
        this.world = world;
        applyGameRules();
    }

    /**
     * Retrieves the spawn location for a specific team by its color.
     *
     * @param color the color of the team.
     * @return the {@link Location} of the team's spawn, or null if not set.
     */
    public @Nullable Location getTeamSpawnLocation(GameTeam.Color color) {
        return teamSpawnLocations.get(color);
    }

    /**
     * Retrieves an unmodifiable map of team spawn locations.
     *
     * @return a map of team colors to their respective spawn locations.
     */
    public @NotNull @Unmodifiable Map<GameTeam.Color, Location> getTeamSpawnLocations() {
        return Collections.unmodifiableMap(teamSpawnLocations);
    }

    /**
     * Retrieves the heart location for a specific team by its color.
     *
     * @param color the color of the team.
     * @return the {@link Location} of the team's heart, or null if not set.
     */
    public @Nullable Location getTeamHeartLocation(GameTeam.Color color) {
        return teamHeartLocation.get(color);
    }

    /**
     * Retrieves an unmodifiable map of team heart locations.
     *
     * @return a map of team colors to their respective heart locations.
     */
    public @NotNull @Unmodifiable Map<GameTeam.Color, Location> getTeamHeartLocations() {
        return Collections.unmodifiableMap(teamHeartLocation);
    }

    /**
     * Applies standard game rules to the world associated with this map.
     */
    private void applyGameRules() {
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_VINES_SPREAD, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.PROJECTILES_CAN_BREAK_BLOCKS, false);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
    }

    /**
     * Represents the available setup of teams in the game.
     */
    @Getter
    public enum TeamSetup {
        TWO_TEAMS(2),
        FOUR_TEAMS(4),
        SIX_TEAMS(6);

        private final int teamCount;

        TeamSetup(int teamCount) {
            this.teamCount = teamCount;
        }

        // TODO
        public String getFormattedTeamSetup() {
            int teamCount = getTeamCount();
            int maxPlayersInTeam = getMaxPlayersInTeam(this);
            return String.join("v", Collections.nCopies(teamCount, String.valueOf(maxPlayersInTeam)));
        }

        /**
         * Retrieves a {@link TeamSetup} instance based on the number of teams.
         *
         * @param teamCount the number of teams.
         * @return the corresponding {@link TeamSetup}, or null if no matching setup is found.
         */
        public static @Nullable TeamSetup from(int teamCount) {
            for (TeamSetup setup : values()) {
                if (setup.getTeamCount() == teamCount) {
                    return setup;
                }
            }
            return null;
        }

        /**
         * Calculates the maximum number of players allowed in a team based on the total
         * number of players and the team setup.
         *
         * @param setup the {@link TeamSetup} to calculate for.
         * @return the maximum number of players allowed in a team.
         */
        public static int getMaxPlayersInTeam(TeamSetup setup) {
            return (int) Math.floor((double) GameManager.MAX_PLAYERS / setup.getTeamCount());
        }

    }

}
