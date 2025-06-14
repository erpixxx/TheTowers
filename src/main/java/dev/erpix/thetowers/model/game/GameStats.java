package dev.erpix.thetowers.model.game;

import dev.erpix.thetowers.model.GameStatKey;
import dev.erpix.thetowers.model.StatsTracker;

/**
 * Represents the in-game statistics storage.
 */
public class GameStats extends StatsTracker<GameStatKey> {

    public GameStats() {
        super(GameStatKey.class);
    }

}
