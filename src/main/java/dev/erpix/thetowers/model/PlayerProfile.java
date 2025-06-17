package dev.erpix.thetowers.model;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the profile of a player in the game.
 */
@Getter
public class PlayerProfile {

    @NotNull
    private final String name;
    @NotNull
    private final StatsTracker stats;

    public PlayerProfile(@NotNull String name) {
        this.name = name;
        this.stats = new StatsTracker();
    }

}
