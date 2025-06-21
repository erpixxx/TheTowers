package dev.erpix.thetowers;

import dev.erpix.thetowers.command.*;
import dev.erpix.thetowers.listener.EntityListener;
import dev.erpix.thetowers.listener.PlayerListener;
import dev.erpix.thetowers.listener.TABHandler;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GameMap;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.model.PlayerManager;
import dev.erpix.thetowers.model.ProfileManager;
import dev.erpix.thetowers.model.tablist.TabManager;
import dev.erpix.thetowers.util.Components;
import dev.erpix.thetowers.util.OrderedAttackerCache;
import lombok.Getter;
import me.libraryaddict.disguise.LibsDisguises;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.event.EventBus;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class TheTowers {

    @NotNull @Getter
    private final ComponentLogger logger;

    @Getter
    private static TheTowers instance;

    private final Map<String, GameMap> maps = new HashMap<>();
    @NotNull @Getter
    private final Plugin plugin;
    @NotNull @Getter
    private final LibsDisguises libsDisguises;
    @NotNull @Getter
    private final CommandRegistrar commandRegistrar;
    @Getter
    private Location spawnLocation;
    @Getter
    private PlayerManager playerManager;
    @Getter
    private ProfileManager profileManager;
    @Getter
    private TabManager tabManager;
    @Getter
    private GameManager gameManager;

    @SuppressWarnings("UnstableApiUsage")
    public TheTowers(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.libsDisguises = LibsDisguises.getInstance();
        this.commandRegistrar = new CommandRegistrar(plugin.getLifecycleManager());
        this.logger = plugin.getComponentLogger();
        instance = this;
    }

    public static @NotNull NamespacedKey key(@NotNull String key) {
        return new NamespacedKey(TheTowers.getInstance().getPlugin(), key);
    }

    public void enable() {
        EventBus eventBus = TabAPI.getInstance().getEventBus();
        if (eventBus == null) {
            plugin.getComponentLogger().error(Components.color("<red>TabAPI EventBus is not available."));
            return;
        }
        eventBus.register(PlayerLoadEvent.class, TABHandler.ON_PLAYER_LOAD);

        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection spawnSection = config.getConfigurationSection("spawn");
        if (spawnSection == null) {
            logger.error(Components.color("<red>'spawn' section is missing in the config.yml!"));
            return;
        }
        double spawnX = spawnSection.getDouble("x");
        double spawnY = spawnSection.getDouble("y");
        double spawnZ = spawnSection.getDouble("z");
        double spawnPitch = spawnSection.getDouble("pitch");
        double spawnYaw = spawnSection.getDouble("yaw");
        String spawnWorldName = spawnSection.getString("world");
        World spawnWorld = spawnWorldName != null ? Bukkit.getWorld(spawnWorldName) : null;
        if (spawnWorld == null) {
            logger.error(Components.color("<red>Cannot find world '" + spawnWorldName + "' for spawn location!"));
            return;
        }
        this.spawnLocation = new Location(spawnWorld, spawnX, spawnY, spawnZ, (float) spawnYaw, (float) spawnPitch);

        ConfigurationSection mapsSection = config.getConfigurationSection("maps");
        if (mapsSection == null) {
            logger.error(Components.color("<red>'maps' section is missing in the config.yml!"));
            return;
        }
        Set<String> maps = mapsSection.getKeys(false);
        for (String map : maps) {

            ConfigurationSection singleMapSection = mapsSection.getConfigurationSection(map);
            if (singleMapSection == null) {
                logger.error(Components.color("<red>Map configuration for '" + map + "' is missing!"));
                continue;
            }

            String worldName = singleMapSection.getString("world");
            if (worldName == null) {
                logger.error(Components.color("<red>'world' is missing for map '" + map + "'!"));
                continue;
            }
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                logger.error(Components.color("<red>World '" + worldName + "' for map '" + map + "' does not exist!"));
                continue;
            }

            ConfigurationSection teamsSection = singleMapSection.getConfigurationSection("teams");
            if (teamsSection == null) {
                logger.error(Components.color("<red>'teams' section is missing for map '" + map + "'!"));
                continue;
            }

            Set<GameTeam.Color> teams = teamsSection.getKeys(false).stream().map(
                    t -> GameTeam.Color.valueOf(t.toUpperCase(Locale.ROOT))).collect(Collectors.toSet());
            GameMap.TeamSetup teamSetup = GameMap.TeamSetup.from(teams.size());
            if (teamSetup == null) {
                logger.error(Components.color("<red>Invalid team setup for map '" + map + "'!"));
                continue;
            }
            Map<GameTeam.Color, Location> heartLocations = new EnumMap<>(GameTeam.Color.class);
            Map<GameTeam.Color, Location> spawnLocations = new EnumMap<>(GameTeam.Color.class);
            for (var team : teams) {
                ConfigurationSection singleTeamSection = teamsSection.getConfigurationSection(team.toString());
                if (singleTeamSection == null) {
                    logger.error(Components.color("<red>Team configuration for '" + team + "' is missing in map '" + map + "'!"));
                    continue;
                }
                int heartX = singleTeamSection.getInt("heart.x");
                int heartY = singleTeamSection.getInt("heart.y");
                int heartZ = singleTeamSection.getInt("heart.z");
                Location heartLocation = new Location(world, heartX, heartY, heartZ);
                heartLocations.put(team, heartLocation);
                double teamSpawnX = singleTeamSection.getDouble("spawn.x");
                double teamSpawnY = singleTeamSection.getDouble("spawn.y");
                double teamSpawnZ = singleTeamSection.getDouble("spawn.z");
                double teamSpawnPitch = singleTeamSection.getDouble("spawn.pitch");
                double teamSpawnYaw = singleTeamSection.getDouble("spawn.yaw");
                Location spawnLocation = new Location(world, teamSpawnX, teamSpawnY, teamSpawnZ, (float) teamSpawnYaw, (float) teamSpawnPitch);
                spawnLocations.put(team, spawnLocation);
            }
            ConfigurationSection waitingRoomSection = singleMapSection.getConfigurationSection("waiting_room");
            if (waitingRoomSection == null) {
                logger.error(Components.color("<red>'waiting_room' section is missing for map '" + map + "'!"));
                continue;
            }
            double waitingRoomX = waitingRoomSection.getDouble("x");
            double waitingRoomY = waitingRoomSection.getDouble("y");
            double waitingRoomZ = waitingRoomSection.getDouble("z");
            double waitingRoomPitch = waitingRoomSection.getDouble("pitch");
            double waitingRoomYaw = waitingRoomSection.getDouble("yaw");

            Location waitingRoomLocation = new Location(world, waitingRoomX, waitingRoomY, waitingRoomZ, (float) waitingRoomYaw, (float) waitingRoomPitch);

            GameMap gameMap = new GameMap(map, teamSetup, waitingRoomLocation, spawnLocations, heartLocations, world);
            this.maps.put(map, gameMap);
        }
        if (this.maps.isEmpty()) {
            logger.error(Components.color("<red>No valid maps found in the configuration!"));
        } else {
            logger.info(Components.color("<green>Loaded " + this.maps.size() + " map(s) successfully."));
            logger.info("Maps: {}", this.maps.keySet().stream()
                    .map(m -> m + " (" + this.maps.get(m).getTeamSetup().getTeamCount() + " teams)")
                    .collect(Collectors.joining(", ")));
        }

        gameManager = new GameManager();
        this.maps.values().stream().findFirst().ifPresent(tMap -> {
            gameManager.setMap(tMap);
            logger.info(Components.color("<green>Game map set to: <white>" + tMap.getName()));
        });

        registerCommands();

        profileManager = new ProfileManager();
        profileManager.init();
        playerManager = new PlayerManager();
        Bukkit.getOnlinePlayers().forEach(player -> {
            this.playerManager.addPlayer(player);
            this.profileManager.load(player.getName());
        });

        tabManager = new TabManager();
        tabManager.registerDefaultPlaceholders();

        registerListeners();
    }

    public void disable() {
        this.profileManager.saveAll();
        OrderedAttackerCache.Manager.getInstance().shutdown();
    }

    private void registerCommands() {
        commandRegistrar.registerAll(
                new AdminCommand(),
                new BroadcastCommand(),
                new GameCommand(),
                new TeamCommand(),
                new HelpCommand(),
                new ProfileCommand(),
                new RulesCommand()
        );
    }

    private void registerListeners() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new EntityListener(), plugin);
    }

    public @NotNull Optional<GameMap> getMap(@NotNull String name) {
        return Optional.ofNullable(maps.get(name));
    }

    public @NotNull @Unmodifiable Collection<GameMap> getMaps() {
        return Collections.unmodifiableCollection(maps.values());
    }

}
