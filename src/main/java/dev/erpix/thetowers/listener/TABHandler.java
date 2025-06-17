package dev.erpix.thetowers.listener;

import dev.erpix.thetowers.TheTowers;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.event.EventHandler;
import me.neznamy.tab.api.event.player.PlayerLoadEvent;

/**
 * Handles TAB events.
 */
public class TABHandler {

    /**
     * Event handler for when a player loads into the game.
     */
    public static final EventHandler<PlayerLoadEvent> ON_PLAYER_LOAD = event -> {
        TheTowers tt = TheTowers.getInstance();
        if (tt == null) {
            return;
        }

        TabPlayer player = event.getPlayer();
        tt.getTabManager().updateLayout(player.getName());
    };

}
