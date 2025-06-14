package dev.erpix.thetowers.model.game;

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

    private final String name;
    private final TeamSetup teamSetup;
    private final Location waitingRoomLocation;
    private final Map<GameTeam.Color, Location> teamSpawnLocations;
    private final Map<GameTeam.Color, Location> teamHeartLocation;
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
     * Gets the name of the map.
     *
     * @return the name of the map
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Gets the team setup of the map.
     *
     * @return the team setup of the map
     */
    public @NotNull TeamSetup getTeamSetup() {
        return teamSetup;
    }

    /**
     * Gets the location of the waiting room for the map.
     *
     * @return the waiting room location
     */
    public @NotNull Location getWaitingRoomLocation() {
        return waitingRoomLocation;
    }

    /**
     * Gets the spawn locations for each team in the map.
     *
     * @return a map of team colors to their respective spawn locations
     */
    public @NotNull @Unmodifiable Map<GameTeam.Color, Location> getTeamSpawnLocations() {
        return Collections.unmodifiableMap(teamSpawnLocations);
    }

    public @Nullable Location getTeamSpawnLocation(GameTeam.Color color) {
        return teamSpawnLocations.get(color);
    }

    /**
     * Gets the heart locations for each team in the map.
     *
     * @return a map of team colors to their respective heart locations
     */
    public @NotNull @Unmodifiable Map<GameTeam.Color, Location> getTeamHeartLocations() {
        return Collections.unmodifiableMap(teamHeartLocation);
    }

    /**
     * Gets the heart location for a specific team color.
     *
     * @param color the color of the team
     * @return the heart location for the specified team color, or null if not found
     */
    public @Nullable Location getTeamHeartLocation(GameTeam.Color color) {
        return teamHeartLocation.get(color);
    }

    /**
     * Gets the world of the map.
     *
     * @return the world of the map.
     */
    public @NotNull World getWorld() {
        return world;
    }

    /**
     * Applies the game rules standard game rules for the map world.
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
    public enum TeamSetup {
        TWO_TEAMS(2),
        FOUR_TEAMS(4),
        SIX_TEAMS(6);

        private final int teamCount;

        TeamSetup(int teamCount) {
            this.teamCount = teamCount;
        }

        /**
         * Gets the number of teams in this setup.
         *
         * @return the number of teams
         */
        public int getTeamCount() {
            return teamCount;
        }

        /**
         * Gets the maximum number of players allowed in a team for this setup.
         *
         * @return the maximum number of players in a team.
         */
        public int getMaxPlayersInTeam() {
            return getMaxPlayersInTeam(this);
        }

        /**
         * Gets the formatted string representation of the team setup.
         * For example, "8v8" for TWO_TEAMS, "4v4v4v4" for FOUR_TEAMS, etc.
         *
         * @return the formatted team setup string
         */
        public String getFormattedTeamSetup() {
            int teamCount = getTeamCount();
            int maxPlayersInTeam = getMaxPlayersInTeam(this);
            return String.join("v", Collections.nCopies(teamCount, String.valueOf(maxPlayersInTeam)));
        }

        /**
         * Gets the TeamSetup based on the number of teams.
         *
         * @param teamCount the number of teams
         * @return the corresponding TeamSetup, or null if no matching setup is found
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
         * Static method to calculate maximum number of players allowed in a team for provided setup.
         *
         * @param setup the team setup to calculate for.
         * @return the maximum number of players in a team.
         * @see TeamSetup#getFormattedTeamSetup()
         */
        public static int getMaxPlayersInTeam(TeamSetup setup) {
            return (int) Math.floor((double) GameSession.MAX_PLAYERS / setup.getTeamCount());
        }

    }

}
