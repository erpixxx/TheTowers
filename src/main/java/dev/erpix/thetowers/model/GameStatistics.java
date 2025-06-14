package dev.erpix.thetowers.model;

/**
 * Represents the in-game statistics storage.
 */
public class GameStatistics extends StatisticsStorage<GameStatKey> {

    public GameStatistics() {
        super(GameStatKey.class);
    }

}
