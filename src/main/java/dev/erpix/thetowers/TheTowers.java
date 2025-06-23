package dev.erpix.thetowers;

import dev.erpix.thetowers.command.*;
import dev.erpix.thetowers.config.Config;
import dev.erpix.thetowers.config.ConfigLoader;
import dev.erpix.thetowers.listener.EntityListener;
import dev.erpix.thetowers.listener.PlayerListener;
import dev.erpix.thetowers.listener.TABHandler;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GameMap;
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

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
    @NotNull @Getter
    private final Config config;
    @Getter
    private Location lobbyLocation;
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

        this.config = ConfigLoader.load(plugin);
    }

    public static @NotNull NamespacedKey key(@NotNull String key) {
        return new NamespacedKey(TheTowers.getInstance().getPlugin(), key);
    }

    public void enable() {
        EventBus eventBus = TabAPI.getInstance().getEventBus();
        if (eventBus == null) {
            plugin.getComponentLogger().error(Components.color("<red>TabAPI Event Bus is not available."));
            return;
        }
        eventBus.register(PlayerLoadEvent.class, TABHandler.ON_PLAYER_LOAD);

        config.getMaps().forEach((name, map) -> {
            GameMap converted = map.convert(name);
            this.maps.put(name, converted);
        });
        logger.info(Components.color("<green>Maps: " + String.join(", ", this.maps.keySet())));

        this.lobbyLocation = config.getLobby().convert();

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
