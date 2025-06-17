package dev.erpix.thetowers.model.tablist;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GameMap;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.model.PlayerManager;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import me.neznamy.tab.api.placeholder.PlayerPlaceholder;
import me.neznamy.tab.api.tablist.layout.Layout;
import me.neznamy.tab.api.tablist.layout.LayoutManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

public class TabManager {

    private static final TabAPI TAB = TabAPI.getInstance();
    private final TheTowers theTowers = TheTowers.getInstance();

    private final Layout lobbyLayout;
    private final Layout teamLobbyLayout;
    private /*final*/ Layout inGameLayout;
    private final Set<PlayerPlaceholder> lobbyPlaceholders = new HashSet<>();
    private final Set<PlayerPlaceholder> teamLobbyPlaceholders = new HashSet<>();
    private final Set<PlayerPlaceholder> inGamePlaceholders = new HashSet<>();

    public TabManager() {
        lobbyLayout = createLobbyLayout();
        teamLobbyLayout = createTeamLobbyLayout();
    }

    private @NotNull Layout createLobbyLayout() {
        LayoutManager lm = getLayoutManagerOrThrow();
        Layout lobby = lm.createNewLayout("lobby");
        lobby.addGroup(null, IntStream.range(21, 61).toArray());

        addFixedSlots(lobby, new String[][] {
                {"1", "<gray>Tworzenie drużyny"}, {"2", "<white>/team"},
                {"4", "<gray>Regulamin gry"}, {"5", "<white>/regulamin"},
                {"7", "<gray>Statystyki"}, {"8", "<white>/profile"},
                {"10", "<gray>Ranking graczy"}, {"11", "<white>/ranking"},
                {"13", "<gray>Historia gier"}, {"14", "<white>/history"},
                {"61", "<gray>❤ Rozegrane gry"}, {"62", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_GAMES_PLAYED.getPlaceholder()},
                {"64", "<gray>\uD83D\uDDE1 Zabójstwa"}, {"65", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_KILLS.getPlaceholder()},
                {"67", "<gray>⚔ Asysty"}, {"68", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_ASSISTS.getPlaceholder()},
                {"70", "<gray>☠ Śmierci"}, {"71", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_DEATHS.getPlaceholder()},
                {"73", "<gray>☄ K/D"}, {"74", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_KD.getPlaceholder()},
                {"76", "<gray>⛏ Uszkodzenia serca"}, {"77", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_HEART_DAMAGE.getPlaceholder()},
                {"79", "<gray>\uD83D\uDD25 Zniszczone wieże"}, {"80", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_TOWERS_DESTROYED.getPlaceholder()},
                {"3", "<reset>                              " }
        });

        return lobby;
    }

    private @NotNull Layout createTeamLobbyLayout() {
        LayoutManager lm = getLayoutManagerOrThrow();
        Layout lobby = lm.createNewLayout("team-lobby");
        lobby.addGroup(null, IntStream.range(21, 61).toArray());

        addFixedSlots(lobby, new String[][]{
                {"1", "<gray>Tryb gry: <white>" + TServerPlaceholder.GAME_TEAM_SETUP.getPlaceholder()},
                {"3", "<gray>Mapa: <white>" + TServerPlaceholder.GAME_MAP.getPlaceholder()},
                {"5", "<gray>Gotowe drużyny: <white>" + TServerPlaceholder.GAME_TEAMS_READY.getPlaceholder() + "</white>/<white>" + TServerPlaceholder.GAME_TEAMS_COUNT.getPlaceholder()},
                {"7", "<gray>Twoja drużyna: <white>" + TPlayerPlaceholder.PLAYER_TEAM.getPlaceholder()},
                {"9", "<gray>Lider drużyny"}, {"10", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_LEADER.getPlaceholder()},
                {"12", "<gray>Gracze w drużynie"},
                {"13", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_1.getPlaceholder()},
                {"14", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_2.getPlaceholder()},
                {"15", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_3.getPlaceholder()},
                {"16", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_4.getPlaceholder()},
                {"17", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_5.getPlaceholder()},
                {"18", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_6.getPlaceholder()},
                {"19", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_7.getPlaceholder()},
                {"20", "<gray>» <white>" + TPlayerPlaceholder.PLAYER_TEAM_MEMBER_8.getPlaceholder()},
                {"61", "<gray>❤ Rozegrane gry"}, {"62", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_GAMES_PLAYED.getPlaceholder()},
                {"64", "<gray>\uD83D\uDDE1 Zabójstwa"}, {"65", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_KILLS.getPlaceholder()},
                {"67", "<gray>⚔ Asysty"}, {"68", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_ASSISTS.getPlaceholder()},
                {"70", "<gray>☠ Śmierci"}, {"71", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_DEATHS.getPlaceholder()},
                {"73", "<gray>☄ K/D"}, {"74", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_KD.getPlaceholder()},
                {"76", "<gray>⛏ Uszkodzenia serca"}, {"77", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_HEART_DAMAGE.getPlaceholder()},
                {"79", "<gray>\uD83D\uDD25 Zniszczone wieże"}, {"80", "<white>" + TPlayerPlaceholder.PLAYER_TOTAL_TOWERS_DESTROYED.getPlaceholder()},
                {"2", "<reset>                              "}
        });

        return lobby;
    }

    private @NotNull Layout createGameLayout(@NotNull GameManager game) {
        LayoutManager lm = getLayoutManagerOrThrow();
        Layout layout = lm.createNewLayout("in-game");

        Collection<GameTeam> teams = game.getTeams();

        return null;
    }

    private void appendToLayoutBasedOnTeamSetup(@NotNull Layout layout, @NotNull GameMap.TeamSetup setup) {
        switch (setup) {
            case TWO_TEAMS -> {
                
            }
        }
    }

    private @NotNull LayoutManager getLayoutManagerOrThrow() {
        LayoutManager lm = TAB.getLayoutManager();
        if (lm == null) {
            throw new IllegalStateException("Layout feature is probably not enabled in TAB config.");
        }
        return lm;
    }

    private void addFixedSlots(@NotNull Layout layout, String[][] slots) {
        for (String[] slot : slots) {
            layout.addFixedSlot(Integer.parseInt(slot[0]), slot[1]);
        }
    }

    public void updateLayout(@NotNull String playerName) {
        TabPlayer tabPlayer = TAB.getPlayer(playerName);
        if (tabPlayer == null) {
            return;
        }

        PlayerManager pm = theTowers.getPlayerManager();
        Optional<GamePlayer> tPlayer = pm.getPlayer(playerName);
        if (tPlayer.isEmpty()) {
            return;
        }

        LayoutManager lm = getLayoutManagerOrThrow();

        GameManager game = theTowers.getGameManager();
        switch (game.getStage()) {
            case LOBBY:
                Layout layout = tPlayer.get().isInAnyTeam() ? teamLobbyLayout : lobbyLayout;
                System.out.println("Updating layout for player: " + playerName + " Layout: " + layout.getName());
                lm.sendLayout(tabPlayer, layout);
                break;
            case WAITING, IN_PROGRESS:
                lm.sendLayout(tabPlayer, inGameLayout);
                break;
            default:
                throw new IllegalStateException("Cannot update layout for '" + playerName + "'. Unexpected game stage");
        }
    }

    public void registerDefaultPlaceholders() {
        PlaceholderManager pm = TAB.getPlaceholderManager();

        for (TServerPlaceholder placeholder : TServerPlaceholder.values()) {
            pm.registerServerPlaceholder(placeholder.getPlaceholder(), placeholder.getRefresh(), placeholder.getSupplier());
        }
        for (TPlayerPlaceholder placeholder : TPlayerPlaceholder.values()) {
            pm.registerPlayerPlaceholder(placeholder.getPlaceholder(), placeholder.getRefresh(), placeholder.getFunction());
        }
    }

}
