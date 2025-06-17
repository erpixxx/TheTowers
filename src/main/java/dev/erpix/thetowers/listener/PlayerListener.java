package dev.erpix.thetowers.listener;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.util.Components;
import dev.erpix.thetowers.util.DisguiseHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private final TheTowers theTowers = TheTowers.getInstance();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DisguiseHandler.create(player);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        DisguiseHandler.toggleName(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(joinMessage(player));

        GamePlayer gamePlayer = theTowers.getPlayerManager().addPlayer(player);
        theTowers.getProfileManager().load(player.getName());

        GameTeam team = gamePlayer.getTeam();
        if (team == null) {
            theTowers.getGameManager().addSpectator(gamePlayer);
        }

        GameManager game = theTowers.getGameManager();
        GameManager.Stage stage = game.getStage();
        if (stage == GameManager.Stage.LOBBY) {
            player.teleport(theTowers.getSpawnLocation());
        }
        else if (stage == GameManager.Stage.WAITING) {
            player.teleport(game.getMap().getWaitingRoomLocation());
        } else {
            if (team != null) {
                player.teleport(game.getMap().getTeamSpawnLocations().get(team.getColor()));
            }
            // Otherwise just keep the player in the same location
            // It would be only for spectators who are not in a team
        }

        theTowers.getTabManager().updateLayout(player.getName());

        DisguiseHandler.create(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        event.quitMessage(quitMessage(player));

        theTowers.getProfileManager().save(playerName);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        theTowers.getPlayerManager().getPlayer(player.getName()).ifPresent(tPlayer -> {
            Component result = Components.standard(String.format("%s <dark_gray>» <white>%s",
                    tPlayer.getDisplayName(), message));
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(result));
            Bukkit.getConsoleSender().sendMessage(result);
        });
    }

    private Component joinMessage(Player player) {
        return Components.standard("<dark_gray>[<green>+</green><dark_gray>] <gray>" + player.getName());
    }

    private Component quitMessage(Player player) {
        return Components.standard("<dark_gray>[<red>-</red><dark_gray>] <gray>" + player.getName());
    }

}
