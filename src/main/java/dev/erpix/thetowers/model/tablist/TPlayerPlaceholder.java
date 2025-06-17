package dev.erpix.thetowers.model.tablist;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.PlayerProfile;
import dev.erpix.thetowers.model.PlayerStat;
import dev.erpix.thetowers.model.PlayerTotalStat;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import lombok.Getter;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.Placeholder;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Enum representing all player-related placeholders used in the tab list.
 * <p>
 * Each enum constant defines a placeholder identifier, a refresh interval,
 * and a function that generates the placeholder's dynamic value based on
 * the {@link TabPlayer} instance.
 * </p>

 */
@Getter
public enum TPlayerPlaceholder {

    // Player's team placeholders
    PLAYER_TEAM("%tt_player_team%", -1,
            fromPlayerTeam(GameTeam::getDisplayName)),
    PLAYER_TEAM_LEADER("%tt_player_team_leader%", -1,
            fromPlayerTeam(team -> team.getLeader().getDisplayName())),
    PLAYER_TEAM_MEMBER_1("%tt_player_team_member_1%", -1,
            getNTeamMember(0)),
    PLAYER_TEAM_MEMBER_2("%tt_player_team_member_2%", -1,
            getNTeamMember(1)),
    PLAYER_TEAM_MEMBER_3("%tt_player_team_member_3%", -1,
            getNTeamMember(2)),
    PLAYER_TEAM_MEMBER_4("%tt_player_team_member_4%", -1,
            getNTeamMember(3)),
    PLAYER_TEAM_MEMBER_5("%tt_player_team_member_5%", -1,
            getNTeamMember(4)),
    PLAYER_TEAM_MEMBER_6("%tt_player_team_member_6%", -1,
            getNTeamMember(5)),
    PLAYER_TEAM_MEMBER_7("%tt_player_team_member_7%", -1,
            getNTeamMember(6)),
    PLAYER_TEAM_MEMBER_8("%tt_player_team_member_8%", -1,
            getNTeamMember(7)),

    // Player's profile placeholders
    PLAYER_TOTAL_ASSISTS("%tt_player_total_assists%", -1,
            getStat(PlayerTotalStat.ASSISTS)),
    PLAYER_TOTAL_DEATHS("%tt_player_total_deaths%", -1,
            getStat(PlayerTotalStat.DEATHS)),
    PLAYER_TOTAL_GAMES_PLAYED("%tt_player_total_games_played%", -1,
            getStat(PlayerTotalStat.GAMES_PLAYED)),
    PLAYER_TOTAL_HEART_DAMAGE("%tt_player_total_heart_damage%", -1,
            getStat(PlayerTotalStat.HEART_DAMAGE)),
    PLAYER_TOTAL_KD("%tt_player_total_kd%", -1,
            fromPlayerProfile(profile -> String.valueOf(profile.getStats()
                    .getRatio(PlayerTotalStat.KILLS, PlayerTotalStat.DEATHS)))),
    PLAYER_TOTAL_KILLS("%tt_player_total_kills%", -1,
            getStat(PlayerTotalStat.KILLS)),
    PLAYER_TOTAL_TOWERS_DESTROYED("%tt_player_total_towers_destroyed%", -1,
            getStat(PlayerTotalStat.TOWERS_DESTROYED));

    private static final TabAPI TAB = TabAPI.getInstance();
    @NotNull
    private final String placeholder;
    private final int refresh;
    @NotNull
    private final Function<TabPlayer, String> function;

    TPlayerPlaceholder(@NotNull String placeholder, int refresh, @NotNull Function<TabPlayer, String> function) {
        this.placeholder = placeholder;
        this.refresh = refresh;
        this.function = function;
    }

    /**
     * Attempts to resolve this placeholder into a {@link PlayerPlaceholder}
     * from the TAB API.
     *
     * @return the resolved {@link PlayerPlaceholder}.
     * @throws IllegalArgumentException if the placeholder is not a {@link PlayerPlaceholder}.
     */
    public @NotNull PlayerPlaceholder resolve() {
        PlaceholderManager pm = TAB.getPlaceholderManager();
        Placeholder tabPlaceholder = pm.getPlaceholder(placeholder);
        if (tabPlaceholder instanceof PlayerPlaceholder playerPlaceholder) {
            return playerPlaceholder;
        }
        throw new IllegalArgumentException("Placeholder is not a PlayerPlaceholder: " + placeholder);
    }

    /**
     * Finds a {@link TPlayerPlaceholder} matching the given placeholder string.
     *
     * @param placeholder the placeholder string to match.
     * @return the matching {@link TPlayerPlaceholder}, or null if none matches.
     */
    public static @Nullable TPlayerPlaceholder from(@NotNull String placeholder) {
        for (TPlayerPlaceholder placeholders : values()) {
            if (placeholders.placeholder.equals(placeholder)) {
                return placeholders;
            }
        }
        return null;
    }

    //
    // Helper methods to create functions
    //

    private static @NotNull Function<TabPlayer, String> fromPlayer(@NotNull Function<GamePlayer, String> fn) {
        return tabPlayer -> TheTowers.getInstance().getPlayerManager().getPlayer(tabPlayer.getName())
                .map(fn)
                .orElse("");
    }

    private static @NotNull Function<TabPlayer, String> fromPlayerTeam(@NotNull Function<GameTeam, String> fn) {
        return tabPlayer -> fromPlayer(player -> {
            GameTeam team = player.getTeam();
            return team != null ? fn.apply(team) : "";
        }).apply(tabPlayer);
    }

    private static @NotNull Function<TabPlayer, String> getNTeamMember(int n) {
        return tabPlayer -> fromPlayerTeam(team -> team.getMembers().stream()
                .skip(n)
                .findFirst()
                .map(GamePlayer::getDisplayName)
                .orElse("")
        ).apply(tabPlayer);
    }

    private static @NotNull Function<TabPlayer, String> fromPlayerProfile(@NotNull Function<PlayerProfile, String> fn) {
        return tabPlayer -> TheTowers.getInstance().getProfileManager().getProfile(tabPlayer.getName())
                .map(fn)
                .orElse("");
    }

    private static @NotNull Function<TabPlayer, String> getStat(@NotNull PlayerStat stat) {
        return tabPlayer -> fromPlayerProfile(profile ->
                String.valueOf(profile.getStats().getStat(stat))
        ).apply(tabPlayer);
    }

}
