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
    public static class Lobby {
        private double x, y, z, yaw, pitch;
        private String world;

        public Location convert() {
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld == null) {
                throw new IllegalArgumentException("World '" + world + "' not found.");
            }
            return new Location(bukkitWorld, x, y, z, (float) yaw, (float) pitch);
        }
    }

    @Getter @Setter @ToString
    public static class MapEntry {
        private String name, world;
        private Map<String, Team> teams;
        private SupplyCrate supplyCrate;
        private WaitingRoom waitingRoom;

        public GameMap convert() {
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld == null) {
                throw new IllegalArgumentException("World '" + world + "' not found.");
            }
            var setup = GameMap.TeamSetup.from(teams.size());
            if (setup == null) {
                throw new IllegalArgumentException("Invalid team setup for map '" + name + "'.");
            }
            Location supplyCrateLocation = new Location(bukkitWorld, supplyCrate.getX(), supplyCrate.getY(), supplyCrate.getZ());
            Location waitingRoomLocation = new Location(bukkitWorld, waitingRoom.getX(), waitingRoom.getY(), waitingRoom.getZ(),
                    (float) waitingRoom.getYaw(), (float) waitingRoom.getPitch());
            HashMap<GameTeam.Color, Location> teamSpawnLocations = new HashMap<>();
            HashMap<GameTeam.Color, Location> teamHeartLocations = new HashMap<>();
            teams.forEach((name, team) -> {
                var spawn = team.getSpawn();
                Location spawnLocation = new Location(bukkitWorld, spawn.getX(), spawn.getY(), spawn.getZ(),
                        (float) spawn.getYaw(), (float) spawn.getPitch());
                var heart = team.getHeart();
                Location heartLocation = new Location(bukkitWorld, heart.getX(), heart.getY(), heart.getZ());
                GameTeam.Color color = GameTeam.Color.from(name);

                teamSpawnLocations.put(color, spawnLocation);
                teamHeartLocations.put(color, heartLocation);
            });
            return new GameMap(name, setup, supplyCrateLocation, waitingRoomLocation,
                    teamSpawnLocations, teamHeartLocations, bukkitWorld);
        }

        @Getter @Setter @ToString
        public static class Team {
            private Heart heart;
            private Spawn spawn;

            @Getter @Setter @ToString
            public static class Heart {
                private double x, y, z;
            }

            @Getter @Setter @ToString
            public static class Spawn {
                private double x, y, z, yaw, pitch;
            }
        }

        @Getter @Setter @ToString
        public static class SupplyCrate {
            private double x, y, z;
        }

        @Getter @Setter @ToString
        public static class WaitingRoom {
            private double x, y, z, yaw, pitch;
        }
    }
}
