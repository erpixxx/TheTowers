package dev.erpix.thetowers.config;

import dev.erpix.thetowers.model.game.GameMap;
import dev.erpix.thetowers.model.game.GameTeam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the configuration for The Towers game.
 */
@Getter @Setter @ToString
public class Config {

    private int baseHeartHealth;
    private Material towerHeartMaterial;
    private Lobby lobby;
    private Map<String, MapEntry> maps;

    @Getter @Setter @ToString
    public static class Lobby extends CoordinateWithYawPitch {
        private String world;

        public Location toLocation() {
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld == null) {
                throw new IllegalArgumentException("World '" + world + "' not found.");
            }
            return new Location(bukkitWorld, x, y, z, (float) yaw, (float) pitch);
        }
    }

    @Getter @Setter @ToString
    public static class MapEntry {
        private String world;
        private Map<String, Team> teams;
        private Coordinate supplyCrate;
        private CoordinateWithYawPitch waitingRoom;

        public GameMap toGameMap(String name) {
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld == null) {
                throw new IllegalArgumentException("World '" + world + "' not found.");
            }
            var setup = GameMap.TeamSetup.from(teams.size());
            if (setup == null) {
                throw new IllegalArgumentException("Invalid team setup for map: " + name);
            }
            Location supplyCrateLocation = new Location(bukkitWorld, supplyCrate.getX(), supplyCrate.getY(), supplyCrate.getZ());
            Location waitingRoomLocation = new Location(bukkitWorld, waitingRoom.getX(), waitingRoom.getY(), waitingRoom.getZ(),
                    (float) waitingRoom.getYaw(), (float) waitingRoom.getPitch());
            HashMap<GameTeam.Color, Location> teamSpawnLocations = new HashMap<>();
            HashMap<GameTeam.Color, Location> teamHeartLocations = new HashMap<>();
            teams.forEach((teamName, team) -> {
                var spawn = team.getSpawn();
                Location spawnLocation = new Location(bukkitWorld, spawn.getX(), spawn.getY(), spawn.getZ(),
                        (float) spawn.getYaw(), (float) spawn.getPitch());
                var heart = team.getHeart();
                Location heartLocation = new Location(bukkitWorld, heart.getX(), heart.getY(), heart.getZ());
                GameTeam.Color color = GameTeam.Color.from(teamName);

                teamSpawnLocations.put(color, spawnLocation);
                teamHeartLocations.put(color, heartLocation);
            });
            return new GameMap(name, setup, supplyCrateLocation, waitingRoomLocation,
                    teamSpawnLocations, teamHeartLocations, bukkitWorld);
        }

        @Getter @Setter @ToString
        public static class Team {
            private Coordinate heart;
            private CoordinateWithYawPitch spawn;
        }
    }

    @Getter @Setter @ToString
    public static class Coordinate {
        protected double x;
        protected double y;
        protected double z;
    }

    @Getter @Setter @ToString
    public static class CoordinateWithYawPitch extends Coordinate {
        protected double yaw;
        protected double pitch;
    }

}
