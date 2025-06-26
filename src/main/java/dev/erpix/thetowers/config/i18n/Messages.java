package dev.erpix.thetowers.config.i18n;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.config.YamlLoader;
import dev.erpix.thetowers.util.Colors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class Messages {

    private static final Logger logger = TheTowers.getInstance().getLogger();

    private static final Map<String, String> TRANSLATABLE = new HashMap<>();
    private static MessagesFile file = new MessagesFile();

    public static final Message PLAYER_ONLY = () -> file.playerOnly;
    public static final Message ITEM_NOT_FOUND = () -> file.itemNotFound;
    public static final Message ITEM_GIVEN = () -> file.itemGiven;
    public static final Message TEAM_NOT_FOUND = () -> file.teamNotFound;
    public static final Message TEAM_DISBANDED = () -> file.teamDisbanded;
    public static final Message TEAM_INVALID_NAME = () -> file.teamInvalidName;
    public static final Message TEAM_ALREADY_EXISTS = () -> file.teamAlreadyExists;
    public static final Message TEAM_CHANGED_NAME = () -> file.teamChangedName;
    public static final Message TEAM_INVALID_COLOR = () -> file.teamInvalidColor;
    public static final Message TEAM_COLOR_ALREADY_TAKEN = () -> file.teamColorAlreadyTaken;
    public static final Message TEAM_COLOR_CHANGED = () -> file.teamColorChanged;
    public static final Message TEAM_SOULS_UPDATED = () -> file.teamSoulsUpdated;
    public static final List<Message> ADMIN_TEAM_INFO = toMessageList(file.adminTeamInfo);
    public static final Message ADMIN_PROFILE_INFO = () -> file.adminProfileInfo;
    public static final Message PROFILE_NOT_FOUND = () -> file.profileNotFound;
    public static final Message ADMIN_STATS_RESET = () -> file.adminStatsReset;
    public static final Message INVALID_STAT_KEY = () -> file.invalidStatKey;
    public static final Message ADMIN_UPDATE_STAT = () -> file.adminUpdateStat;
    public static final List<Message> TEAM_COMMAND_USAGE = toMessageList(file.teamCommandUsage);
    public static final Message TEAM_CREATED = () -> file.teamCreated;
    public static final Message NOT_IN_TEAM = () -> file.notInTeam;
    public static final Message HAVE_TO_BE_LEADER_TO_DISBAND = () -> file.haveToBeLeaderToDisband;
    public static final Message TEAM_FULL = () -> file.teamFull;
    public static final Message PLAYER_JOINED_TEAM = () -> file.playerJoinedTeam;
    public static final Message TEAM_JOIN = () -> file.teamJoin;
    public static final Message TEAM_LEAVE = () -> file.teamLeave;
    public static final Message PLAYER_LEFT_TEAM = () -> file.playerLeftTeam;
    public static final Message PLAYER_KICKED_FROM_TEAM = () -> file.playerKickedFromTeam;
    public static final Message PLAYER_NOT_IN_TEAM = () -> file.PlayerNotInTeam;
    public static final Message KICKED_FROM_TEAM = () -> file.kickedFromTeam;
    public static final Message TEAM_TRANSFERRED_LEADER = () -> file.teamTransferredLeader;
    public static final Message TEAM_LEADERSHIP_GIVEN = () -> file.teamLeadershipGiven;
    public static final Message ALREADY_IN_TEAM = () -> file.alreadyInTeam;
    public static final Message MUST_BE_A_LEADER = () -> file.mustBeALeader;
    public static final Message CANNOT_REMOVE_YOURSELF_FROM_TEAM = () -> file.cannotRemoveYourselfFromTeam;
    public static final Message CANNOT_LEAVE_IF_LEADER = () -> file.cannotLeaveIfLeader;
    public static final Message MAP_NOT_FOUND = () -> file.mapNotFound;
    public static final Message MAP_SET = () -> file.mapSet;
    public static final Message CHANGED_MAX_PLAYERS = () -> file.changedMaxPlayers;
    public static final Message GAME_ACTIVE = () -> file.gameActive;
    public static final Message MUST_BE_AT_LEAST_2_TEAMS = () -> file.mustBeAtLeast2Teams;
    public static final Message TOO_FEW_MEMBERS = () -> file.tooFewMembers;
    public static final List<Message> GAME_STATUS = toMessageList(file.gameStatus);
    public static final Message GAME_STATUS_TEAM = () -> file.gameStatusTeam;
    public static final Message GAME_NOT_RUNNING = () -> file.gameNotRunning;
    public static final Message GAME_STOPPED = () -> file.gameStopped;
    public static final List<Message> HELP_COMMAND = toMessageList(file.helpCommand);
    public static final List<Message> RULES_COMMAND = toMessageList(file.rulesCommand);

    public static void load(@NotNull Plugin plugin) {
        Path messagesPath = plugin.getDataPath().resolve("messages.yml");
        if (!Files.exists(messagesPath)) {
            try {
                Files.createDirectories(messagesPath.getParent());
                YamlLoader.save(messagesPath, file);
                logger.info("Default messages.yml created at: {}", messagesPath);
            } catch (Exception e) {
                logger.error("Failed to create default messages.yml", e);
                return;
            }
        }

        try {
            file = YamlLoader.load(messagesPath, MessagesFile.class);
            loadTranslatable();
            logger.info("Messages loaded successfully.");
        } catch (IOException e) {
            logger.error("Failed to load messages.yml", e);
        }
    }

    public static @NotNull String translate(@NotNull String text) {
        return TRANSLATABLE.getOrDefault(text, text);
    }

    /**
     * Loads all translatable keywords from the messages file into the map using reflection.
     */
    private static void loadTranslatable() {
        Field[] fields = MessagesFile.class.getDeclaredFields();
        for (Field field : fields) {
            Translatable translatable = field.getAnnotation(Translatable.class);
            if (translatable != null) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(file);
                    TRANSLATABLE.put(translatable.value(), value);
                } catch (IllegalAccessException e) {
                    logger.error("Failed to access message field: {}", field.getName(), e);
                }
            }
        }
    }

    private static @NotNull List<Message> toMessageList(@NotNull List<String> messages) {
        return messages.stream()
                .map(msg -> (Message) () -> msg)
                .toList();
    }

    /**
     * Extended Supplier interface for string retrieval with formatting support.
     */
    public interface Message extends Supplier<String> {

        /**
         * Retrieves the message and formats it with the provided arguments.
         *
         * @param args the arguments to format the message with.
         * @return the formatted message.
         */
        default String get(Object... args) {
            return String.format(get(), args);
        }

    }

    @Getter @Setter
    public static class MessagesFile {

        private String playerOnly = "<red>This command can only be used by players.";
        private String itemNotFound = "<red>Item '%s' does not exist.";
        private String itemGiven = "<green>Gave you: <gray>%s";
        private String teamNotFound = "<red>Cannot find team with name: %s";
        private String teamDisbanded = "<green>Team <gray>%s <gray>has been disbanded.";
        private String teamInvalidName = "<red>Invalid team name <gray>%s</gray>. Team names must be 2-5 characters long and can only contain letters and numbers.";
        private String teamAlreadyExists = "<red>Team with name <gray>%s</gray> already exists.";
        private String teamChangedName = "<green>Changed team name from <gray>%s</gray> to <gray>%s</gray>.";
        private String teamInvalidColor = "<red>Invalid team color: %s";
        private String teamColorAlreadyTaken = "<red>Color <gray>%s</gray> is already taken by another team.";
        private String teamColorChanged = "<green>Changed team color from to %s.";
        private String teamSoulsUpdated = "<green>Updated souls for team %s to <gray>%d</gray>.";
        private List<String> adminTeamInfo = List.of(
                Colors.format(Colors.PRIMARY) + "Team %s:",
                Colors.format(Colors.SECONDARY) + "Leader: <gray>%s</gray>",
                Colors.format(Colors.SECONDARY) + "Members: <gray>%s</gray>",
                Colors.format(Colors.SECONDARY) + "Color: <gray>%s</gray>",
                Colors.format(Colors.SECONDARY) + "Souls: <gray>%s</gray>",
                Colors.format(Colors.SECONDARY) + "Heart health: <gray>%s</gray>"
        );
        private String adminProfileInfo = Colors.format(Colors.PRIMARY) + "Profile for <gray>%s</gray>:";
        private String profileNotFound = "<red>Profile for <gray>%s</gray> not found.";
        private String adminStatsReset = "<green>Reset all stats for player <gray>%s</gray>.";
        private String invalidStatKey = "<red>Invalid stat key: %s.";
        private String adminUpdateStat = "<green>Updated stat <gray>%s</gray> for player <gray>%s</gray> to <gray>%d</gray>.";
        private List<String> teamCommandUsage = List.of(
                "<color:#4aa1ff>Creating a team</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> create <green><color></green> <green><name></green>",
                "<color:#4aa1ff>Disbanding a team</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> disband",
                "<color:#4aa1ff>Joining a team</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> join <green><name></green>",
                "<color:#4aa1ff>Leaving a current team</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> leave",
                "<color:#4aa1ff>Removing a member from team</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> remove <green><player></green>",
                "<color:#4aa1ff>Renaming a team</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> rename <green><new_name></green>",
                "<color:#4aa1ff>Setting a team color</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> set-color <green><color></green>",
                "<color:#4aa1ff>Changing team leader</color>",
                " <color:#4aa1ff>»</color> <gray>/team</gray> set-leader <green><player></green>"
        );
        private String teamCreated = "<green>Successfully created team %s!";
        private String notInTeam = "<red>You are not in a team.";
        private String haveToBeLeaderToDisband = "<red>You have to be the team leader to disband the team. If you want to leave the team, use /team leave.";
        private String teamFull = "<red>The team is full. You cannot join it.";
        private String playerJoinedTeam = "<gray>%s has joined your team.";
        private String teamJoin = "<green>You have joined the team %s.";
        private String teamLeave = "<green>You have left the team %s.";
        private String playerLeftTeam = "<gray>%s has left your team.";
        private String playerKickedFromTeam = "<gray>%s has been removed from your team.";
        private String PlayerNotInTeam = "<red>There's no such player in your team";
        private String kickedFromTeam = "<red>You have been kicked from the team %s.";
        private String teamTransferredLeader = "<green>Transferred team leadership to %s.";
        private String teamLeadershipGiven = "<green>You have been given leadership of the team %s.";
        private String alreadyInTeam = "<red>You are already in a team. If you want to leave it, use /team leave.";
        private String mustBeALeader = "<red>You must be a team leader to take this action.";
        private String cannotRemoveYourselfFromTeam = "<red>You cannot remove yourself from the team. If you want to leave it, use /team leave.";
        private String cannotLeaveIfLeader = "<red>You cannot leave the team if you are its leader. Transfer leadership to someone else using '/team set-leader <player>' or disband it by '/team disband'.";
        private String mapNotFound = "<red>Map with name <gray>%s</gray> not found.";
        private String mapSet = "<green>Set the map to <gray>%s</gray>.";
        private String changedMaxPlayers = "<green>Changed max players per team to <gray>%d</gray>.";
        private String gameActive = "<red>There is already an active game. Please wait until it finishes.";
        private String mustBeAtLeast2Teams = "<red>There must be at least 2 teams to start a game.";
        private String tooFewMembers = "<red>There are too few members in one or more team to start a game. Minimum is %d players.";
        private List<String> gameStatus = List.of(
                Colors.format(Colors.PRIMARY) + "Current game status:",
                "  " + Colors.format(Colors.SECONDARY) + "Map: <gray>%s</gray>",
                "  " + Colors.format(Colors.SECONDARY) + "Stage: <gray>%s</gray>",
                "  " + Colors.format(Colors.SECONDARY) + "Teams:"
        );
        private String gameStatusTeam = Colors.format(Colors.SECONDARY) + "  » %s <gray>(%d members)";
        private String gameNotRunning = "<red>There is no game running at the moment.";
        private String gameStopped = "<green>Game has been stopped.";
        private List<String> helpCommand = List.of(
                ""
        );
        private List<String> rulesCommand = List.of(
                ""
        );

        @Translatable("play_time")
        private String playTime = "Play time";
        @Translatable("kills")
        private String kills = "Kills";
        @Translatable("deaths")
        private String deaths = "Deaths";
        @Translatable("assists")
        private String assists = "Assists";
        @Translatable("heart_damage")
        private String heartDamage = "Heart damage";
        @Translatable("towers_destroyed")
        private String towersDestroyed = "Towers destroyed";
        @Translatable("coal_mined")
        private String coalMined = "Coal mined";
        @Translatable("copper_mined")
        private String copperMined = "Copper mined";
        @Translatable("iron_mined")
        private String ironMined = "Iron mined";
        @Translatable("gold_mined")
        private String goldMined = "Gold mined";
        @Translatable("diamond_mined")
        private String diamondMined = "Diamond mined";
        @Translatable("emerald_mined")
        private String emeraldMined = "Emerald mined";
        @Translatable("lapis_mined")
        private String lapisMined = "Lapis mined";
        @Translatable("amethyst_mined")
        private String amethystMined = "Amethyst mined";
        @Translatable("quartz_mined")
        private String quartzMined = "Quartz mined";
        @Translatable("netherite_mined")
        private String netheriteMined = "Netherite mined";
        @Translatable("wood_gathered")
        private String woodGathered = "Wood gathered";
        @Translatable("carrot_harvested")
        private String carrotHarvested = "Carrot harvested";
        @Translatable("melon_harvested")
        private String melonHarvested = "Melon harvested";
        @Translatable("potato_harvested")
        private String potatoHarvested = "Potato harvested";
        @Translatable("wheat_harvested")
        private String wheatHarvested = "Wheat harvested";
        @Translatable("fish_caught")
        private String fishCaught = "Fish caught";
        @Translatable("enchantments_applied")
        private String enchantmentsApplied = "Enchantments applied";
        @Translatable("supply_crates_opened")
        private String supplyCratesOpened = "Supply crates opened";
        @Translatable("blocks_placed")
        private String blocksPlaced = "Blocks placed";
        @Translatable("blocks_broken")
        private String blocksBroken = "Blocks broken";
        @Translatable("games_played")
        private String gamesPlayed = "Games played";
        @Translatable("wins")
        private String wins = "Wins";
        @Translatable("losses")
        private String losses = "Losses";

    }

}
