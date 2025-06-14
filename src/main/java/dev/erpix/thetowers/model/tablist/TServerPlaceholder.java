package dev.erpix.thetowers.model.tablist;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.TGame;
import dev.erpix.thetowers.model.TMap;
import dev.erpix.thetowers.model.TPlayer;
import dev.erpix.thetowers.model.TTeam;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.placeholder.Placeholder;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.ServerPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Enum representing all server placeholders used in the tab list.
 * <p>
 * Each enum constant defines a placeholder identifier, a refresh interval,
 * and a supplier function that provides the placeholder's dynamic value.
 * These placeholders are designed to integrate with the TAB API and
 * represent various game-related information such as map name, teams,
 * and team members.
 * </p>
 */
public enum TServerPlaceholder {

    // Game placeholders
    GAME_MAP("%tt_game_map%", -1,
            fromMap(TMap::getName)),
    GAME_TEAM_SETUP("%tt_game_team_setup%", -1,
            fromMap(map -> map.getTeamSetup().getFormattedTeamSetup())),
    GAME_TEAMS_READY("%tt_game_teams_ready%", -1,
            fromGame(game -> String.valueOf(game.getTeams().stream()
                    .filter(team -> team.getMembers().size() >= game.getMaxPlayersPerTeam())
                    .count()))),
    GAME_TEAMS_COUNT("%tt_game_teams_count%", -1,
            fromGame(game -> String.valueOf(game.getTeams().size()))),

    // Red team placeholders
    GAME_RED_TEAM_NAME("%tt_game_red_team_name%", -1,
            fromTeam(TTeam.Color.RED, TTeam::getDisplayName)),
    GAME_RED_TEAM_MEMBER_1("%tt_game_red_team_member_1%", -1,
            getNTeamMember(TTeam.Color.RED, 0)),
    GAME_RED_TEAM_MEMBER_2("%tt_game_red_team_member_2%", -1,
            getNTeamMember(TTeam.Color.RED, 1)),
    GAME_RED_TEAM_MEMBER_3("%tt_game_red_team_member_3%", -1,
            getNTeamMember(TTeam.Color.RED, 2)),
    GAME_RED_TEAM_MEMBER_4("%tt_game_red_team_member_4%", -1,
            getNTeamMember(TTeam.Color.RED, 3)),
    GAME_RED_TEAM_MEMBER_5("%tt_game_red_team_member_5%", -1,
            getNTeamMember(TTeam.Color.RED, 4)),
    GAME_RED_TEAM_MEMBER_6("%tt_game_red_team_member_6%", -1,
            getNTeamMember(TTeam.Color.RED, 5)),
    GAME_RED_TEAM_MEMBER_7("%tt_game_red_team_member_7%", -1,
            getNTeamMember(TTeam.Color.RED, 6)),
    GAME_RED_TEAM_MEMBER_8("%tt_game_red_team_member_8%", -1,
            getNTeamMember(TTeam.Color.RED, 7)),
    GAME_RED_TEAM_HEART_HEALTH("%tt_game_red_team_heart_health%", -1,
            fromTeam(TTeam.Color.RED, team -> String.valueOf(team.getHeartHealth()))),

    // Blue team placeholders
    GAME_BLUE_TEAM_NAME("%tt_game_blue_team_name%", -1,
            fromTeam(TTeam.Color.BLUE, TTeam::getDisplayName)),
    GAME_BLUE_TEAM_MEMBER_1("%tt_game_blue_team_member_1%", -1,
            getNTeamMember(TTeam.Color.BLUE, 0)),
    GAME_BLUE_TEAM_MEMBER_2("%tt_game_blue_team_member_2%", -1,
            getNTeamMember(TTeam.Color.BLUE, 1)),
    GAME_BLUE_TEAM_MEMBER_3("%tt_game_blue_team_member_3%", -1,
            getNTeamMember(TTeam.Color.BLUE, 2)),
    GAME_BLUE_TEAM_MEMBER_4("%tt_game_blue_team_member_4%", -1,
            getNTeamMember(TTeam.Color.BLUE, 3)),
    GAME_BLUE_TEAM_MEMBER_5("%tt_game_blue_team_member_5%", -1,
            getNTeamMember(TTeam.Color.BLUE, 4)),
    GAME_BLUE_TEAM_MEMBER_6("%tt_game_blue_team_member_6%", -1,
            getNTeamMember(TTeam.Color.BLUE, 5)),
    GAME_BLUE_TEAM_MEMBER_7("%tt_game_blue_team_member_7%", -1,
            getNTeamMember(TTeam.Color.BLUE, 6)),
    GAME_BLUE_TEAM_MEMBER_8("%tt_game_blue_team_member_8%", -1,
            getNTeamMember(TTeam.Color.BLUE, 7)),
    GAME_BLUE_TEAM_HEART_HEALTH("%tt_game_blue_team_heart_health%", -1,
            fromTeam(TTeam.Color.BLUE, team -> String.valueOf(team.getHeartHealth()))),

    // Green team placeholders
    GAME_GREEN_TEAM_NAME("%tt_game_green_team_name%", -1,
            fromTeam(TTeam.Color.GREEN, TTeam::getDisplayName)),
    GAME_GREEN_TEAM_MEMBER_1("%tt_game_green_team_member_1%", -1,
            getNTeamMember(TTeam.Color.GREEN, 0)),
    GAME_GREEN_TEAM_MEMBER_2("%tt_game_green_team_member_2%", -1,
            getNTeamMember(TTeam.Color.GREEN, 1)),
    GAME_GREEN_TEAM_MEMBER_3("%tt_game_green_team_member_3%", -1,
            getNTeamMember(TTeam.Color.GREEN, 2)),
    GAME_GREEN_TEAM_MEMBER_4("%tt_game_green_team_member_4%", -1,
            getNTeamMember(TTeam.Color.GREEN, 3)),
    GAME_GREEN_TEAM_MEMBER_5("%tt_game_green_team_member_5%", -1,
            getNTeamMember(TTeam.Color.GREEN, 4)),
    GAME_GREEN_TEAM_MEMBER_6("%tt_game_green_team_member_6%", -1,
            getNTeamMember(TTeam.Color.GREEN, 5)),
    GAME_GREEN_TEAM_MEMBER_7("%tt_game_green_team_member_7%", -1,
            getNTeamMember(TTeam.Color.GREEN, 6)),
    GAME_GREEN_TEAM_MEMBER_8("%tt_game_green_team_member_8%", -1,
            getNTeamMember(TTeam.Color.GREEN, 7)),
    GAME_GREEN_TEAM_HEART_HEALTH("%tt_game_green_team_heart_health%", -1,
            fromTeam(TTeam.Color.GREEN, team -> String.valueOf(team.getHeartHealth()))),

    // Yellow team placeholders
    GAME_YELLOW_TEAM_NAME("%tt_game_yellow_team_name%", -1,
            fromTeam(TTeam.Color.YELLOW, TTeam::getDisplayName)),
    GAME_YELLOW_TEAM_MEMBER_1("%tt_game_yellow_team_member_1%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 0)),
    GAME_YELLOW_TEAM_MEMBER_2("%tt_game_yellow_team_member_2%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 1)),
    GAME_YELLOW_TEAM_MEMBER_3("%tt_game_yellow_team_member_3%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 2)),
    GAME_YELLOW_TEAM_MEMBER_4("%tt_game_yellow_team_member_4%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 3)),
    GAME_YELLOW_TEAM_MEMBER_5("%tt_game_yellow_team_member_5%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 4)),
    GAME_YELLOW_TEAM_MEMBER_6("%tt_game_yellow_team_member_6%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 5)),
    GAME_YELLOW_TEAM_MEMBER_7("%tt_game_yellow_team_member_7%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 6)),
    GAME_YELLOW_TEAM_MEMBER_8("%tt_game_yellow_team_member_8%", -1,
            getNTeamMember(TTeam.Color.YELLOW, 7)),
    GAME_YELLOW_TEAM_HEART_HEALTH("%tt_game_yellow_team_heart_health%", -1,
            fromTeam(TTeam.Color.YELLOW, team -> String.valueOf(team.getHeartHealth()))),

    // Orange team placeholders
    GAME_ORANGE_TEAM_NAME("%tt_game_orange_team_name%", -1,
            fromTeam(TTeam.Color.ORANGE, TTeam::getDisplayName)),
    GAME_ORANGE_TEAM_MEMBER_1("%tt_game_orange_team_member_1%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 0)),
    GAME_ORANGE_TEAM_MEMBER_2("%tt_game_orange_team_member_2%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 1)),
    GAME_ORANGE_TEAM_MEMBER_3("%tt_game_orange_team_member_3%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 2)),
    GAME_ORANGE_TEAM_MEMBER_4("%tt_game_orange_team_member_4%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 3)),
    GAME_ORANGE_TEAM_MEMBER_5("%tt_game_orange_team_member_5%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 4)),
    GAME_ORANGE_TEAM_MEMBER_6("%tt_game_orange_team_member_6%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 5)),
    GAME_ORANGE_TEAM_MEMBER_7("%tt_game_orange_team_member_7%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 6)),
    GAME_ORANGE_TEAM_MEMBER_8("%tt_game_orange_team_member_8%", -1,
            getNTeamMember(TTeam.Color.ORANGE, 7)),
    GAME_ORANGE_TEAM_HEART_HEALTH("%tt_game_orange_team_heart_health%", -1,
            fromTeam(TTeam.Color.ORANGE, team -> String.valueOf(team.getHeartHealth()))),

    // Purple team placeholders
    GAME_PURPLE_TEAM_NAME("%tt_game_purple_team_name%", -1,
            fromTeam(TTeam.Color.PURPLE, TTeam::getDisplayName)),
    GAME_PURPLE_TEAM_MEMBER_1("%tt_game_purple_team_member_1%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 0)),
    GAME_PURPLE_TEAM_MEMBER_2("%tt_game_purple_team_member_2%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 1)),
    GAME_PURPLE_TEAM_MEMBER_3("%tt_game_purple_team_member_3%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 2)),
    GAME_PURPLE_TEAM_MEMBER_4("%tt_game_purple_team_member_4%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 3)),
    GAME_PURPLE_TEAM_MEMBER_5("%tt_game_purple_team_member_5%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 4)),
    GAME_PURPLE_TEAM_MEMBER_6("%tt_game_purple_team_member_6%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 5)),
    GAME_PURPLE_TEAM_MEMBER_7("%tt_game_purple_team_member_7%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 6)),
    GAME_PURPLE_TEAM_MEMBER_8("%tt_game_purple_team_member_8%", -1,
            getNTeamMember(TTeam.Color.PURPLE, 7)),
    GAME_PURPLE_TEAM_HEART_HEALTH("%tt_game_purple_team_heart_health%", -1,
            fromTeam(TTeam.Color.PURPLE, team -> String.valueOf(team.getHeartHealth())));

    private static final TabAPI TAB = TabAPI.getInstance();
    private final String placeholder;
    private final int refresh;
    private final Supplier<String> supplier;

    TServerPlaceholder(String placeholder, int refresh, Supplier<String> supplier) {
        this.placeholder = placeholder;
        this.refresh = refresh;
        this.supplier = supplier;
    }

    /**
     * Returns the string identifier of the placeholder.
     *
     * @return the placeholder string (e.g. "%tt_game_map%").
     */
    public @NotNull String getPlaceholder() {
        return placeholder;
    }

    /**
     * Returns the refresh interval for the placeholder.
     *
     * @return the refresh interval in ticks, or -1 if no automatic refresh.
     */
    public int getRefresh() {
        return refresh;
    }

    /**
     * Returns the supplier function providing the placeholder's current value.
     *
     * @return a {@link Supplier} that returns the current placeholder value.
     */
    public @NotNull Supplier<String> getSupplier() {
        return supplier;
    }

    /**
     * Attempts to resolve this placeholder into a {@link ServerPlaceholder}
     * from the TAB API.
     *
     * @return the resolved {@link ServerPlaceholder}.
     * @throws IllegalArgumentException if the placeholder is not a {@link ServerPlaceholder}.
     */
    public @NotNull ServerPlaceholder resolve() {
        PlaceholderManager pm = TAB.getPlaceholderManager();
        Placeholder tabPlaceholder = pm.getPlaceholder(placeholder);
        if (tabPlaceholder instanceof ServerPlaceholder serverPlaceholder) {
            return serverPlaceholder;
        }
        throw new IllegalArgumentException("Placeholder is not a ServerPlaceholder: " + placeholder);
    }

    /**
     * Finds a {@link TServerPlaceholder} matching the given placeholder string.
     *
     * @param placeholder the placeholder string to match.
     * @return the matching {@link TServerPlaceholder}, or null if none matches.
     */
    public static @Nullable TServerPlaceholder from(@NotNull String placeholder) {
        for (TServerPlaceholder placeholders : values()) {
            if (placeholders.getPlaceholder().equals(placeholder)) {
                return placeholders;
            }
        }
        return null;
    }

    public static @NotNull TeamPlaceholders getTeamPlaceholders(@NotNull TTeam.Color color) {
        return TeamPlaceholders.forColor(color);
    }

    public static class TeamPlaceholders {

        private static final Map<TTeam.Color, TeamPlaceholders> COLORS = new EnumMap<>(TTeam.Color.class);

        private final TTeam.Color color;
        private final TServerPlaceholder namePlaceholder;
        private final TServerPlaceholder heartHealthPlaceholder;
        private final TServerPlaceholder[] memberPlaceholders;

        private TeamPlaceholders(TTeam.Color color) {
            this.color = color;
            switch (color) {
                case RED -> {
                    this.namePlaceholder = GAME_RED_TEAM_NAME;
                    this.heartHealthPlaceholder = GAME_RED_TEAM_HEART_HEALTH;
                    this.memberPlaceholders = new TServerPlaceholder[] {
                            GAME_RED_TEAM_MEMBER_1, GAME_RED_TEAM_MEMBER_2,
                            GAME_RED_TEAM_MEMBER_3, GAME_RED_TEAM_MEMBER_4,
                            GAME_RED_TEAM_MEMBER_5, GAME_RED_TEAM_MEMBER_6,
                            GAME_RED_TEAM_MEMBER_7, GAME_RED_TEAM_MEMBER_8
                    };
                }
                case BLUE -> {
                    this.namePlaceholder = GAME_BLUE_TEAM_NAME;
                    this.heartHealthPlaceholder = GAME_BLUE_TEAM_HEART_HEALTH;
                    this.memberPlaceholders = new TServerPlaceholder[] {
                            GAME_BLUE_TEAM_MEMBER_1, GAME_BLUE_TEAM_MEMBER_2,
                            GAME_BLUE_TEAM_MEMBER_3, GAME_BLUE_TEAM_MEMBER_4,
                            GAME_BLUE_TEAM_MEMBER_5, GAME_BLUE_TEAM_MEMBER_6,
                            GAME_BLUE_TEAM_MEMBER_7, GAME_BLUE_TEAM_MEMBER_8
                    };
                }
                case GREEN -> {
                    this.namePlaceholder = GAME_GREEN_TEAM_NAME;
                    this.heartHealthPlaceholder = GAME_GREEN_TEAM_HEART_HEALTH;
                    this.memberPlaceholders = new TServerPlaceholder[] {
                            GAME_GREEN_TEAM_MEMBER_1, GAME_GREEN_TEAM_MEMBER_2,
                            GAME_GREEN_TEAM_MEMBER_3, GAME_GREEN_TEAM_MEMBER_4,
                            GAME_GREEN_TEAM_MEMBER_5, GAME_GREEN_TEAM_MEMBER_6,
                            GAME_GREEN_TEAM_MEMBER_7, GAME_GREEN_TEAM_MEMBER_8
                    };
                }
                case YELLOW -> {
                    this.namePlaceholder = GAME_YELLOW_TEAM_NAME;
                    this.heartHealthPlaceholder = GAME_YELLOW_TEAM_HEART_HEALTH;
                    this.memberPlaceholders = new TServerPlaceholder[] {
                            GAME_YELLOW_TEAM_MEMBER_1, GAME_YELLOW_TEAM_MEMBER_2,
                            GAME_YELLOW_TEAM_MEMBER_3, GAME_YELLOW_TEAM_MEMBER_4,
                            GAME_YELLOW_TEAM_MEMBER_5, GAME_YELLOW_TEAM_MEMBER_6,
                            GAME_YELLOW_TEAM_MEMBER_7, GAME_YELLOW_TEAM_MEMBER_8
                    };
                }
                case ORANGE -> {
                    this.namePlaceholder = GAME_ORANGE_TEAM_NAME;
                    this.heartHealthPlaceholder = GAME_ORANGE_TEAM_HEART_HEALTH;
                    this.memberPlaceholders = new TServerPlaceholder[] {
                            GAME_ORANGE_TEAM_MEMBER_1, GAME_ORANGE_TEAM_MEMBER_2,
                            GAME_ORANGE_TEAM_MEMBER_3, GAME_ORANGE_TEAM_MEMBER_4,
                            GAME_ORANGE_TEAM_MEMBER_5, GAME_ORANGE_TEAM_MEMBER_6,
                            GAME_ORANGE_TEAM_MEMBER_7, GAME_ORANGE_TEAM_MEMBER_8
                    };
                }
                case PURPLE -> {
                    this.namePlaceholder = GAME_PURPLE_TEAM_NAME;
                    this.heartHealthPlaceholder = GAME_PURPLE_TEAM_HEART_HEALTH;
                    this.memberPlaceholders = new TServerPlaceholder[] {
                            GAME_PURPLE_TEAM_MEMBER_1, GAME_PURPLE_TEAM_MEMBER_2,
                            GAME_PURPLE_TEAM_MEMBER_3, GAME_PURPLE_TEAM_MEMBER_4,
                            GAME_PURPLE_TEAM_MEMBER_5, GAME_PURPLE_TEAM_MEMBER_6,
                            GAME_PURPLE_TEAM_MEMBER_7, GAME_PURPLE_TEAM_MEMBER_8
                    };
                }
                default -> throw new IllegalArgumentException("Unknown team color: " + color);
            }
        }

        public @NotNull TTeam.Color getColor() {
            return color;
        }

        public @NotNull TServerPlaceholder getNamePlaceholder() {
            return namePlaceholder;
        }

        public @NotNull TServerPlaceholder getHeartHealthPlaceholder() {
            return heartHealthPlaceholder;
        }

        public @NotNull TServerPlaceholder[] getMemberPlaceholders() {
            return memberPlaceholders.clone();
        }

        public @NotNull TServerPlaceholder getMemberPlaceholder(int index) {
            if (index < 0 || index >= memberPlaceholders.length) {
                throw new IndexOutOfBoundsException("Member placeholder index out of bounds: " + index);
            }
            return memberPlaceholders[index];
        }

        public @NotNull TServerPlaceholder[] getAllPlaceholders() {
            TServerPlaceholder[] all = new TServerPlaceholder[memberPlaceholders.length + 2];
            all[0] = namePlaceholder;
            all[1] = heartHealthPlaceholder;
            System.arraycopy(memberPlaceholders, 0, all, 2, memberPlaceholders.length);
            return all;
        }

        public static @NotNull TeamPlaceholders forColor(@NotNull TTeam.Color color) {
            return COLORS.computeIfAbsent(color, TeamPlaceholders::new);
        }
    }

    //
    // Helper methods for creating suppliers
    //

    private static @NotNull Supplier<String> fromGame(@NotNull Function<TGame, String> fn) {
        return () -> fn.apply(TheTowers.getInstance().getGame());
    }

    private static @NotNull Supplier<String> fromMap(@NotNull Function<TMap, String> fn) {
        return () -> fn.apply(TheTowers.getInstance().getGame().getMap());
    }

    private static @NotNull Supplier<String> fromTeam(@NotNull TTeam.Color color, @NotNull Function<TTeam, String> fn) {
        return () -> {
            TTeam team = TheTowers.getInstance().getGame().getTeam(color);
            return team != null ? fn.apply(team) : "";
        };
    }

    private static @NotNull Supplier<String> getNTeamMember(@NotNull TTeam.Color color, int n) {
        return () -> {
            TGame game = TheTowers.getInstance().getGame();
            TTeam team = game.getTeam(color);
            if (team == null || team.getMembers().size() < n) {
                return "";
            }
            return team.getMembers().stream()
                    .skip(n)
                    .findFirst()
                    .map(TPlayer::getDisplayName)
                    .orElse("");
        };
    }

}
