package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.*;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.util.Colors;
import dev.erpix.thetowers.util.ItemGenerator;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/*
 *
 * /ttadmin - The main command for administrative tasks in The Towers.
 * /ttadmin item <item_name> - Gives an item by name.
 * /ttadmin team <tag> disband - Disbands a team by name.
 * /ttadmin team <tag> edit name <new_name> - Changes the name of a team.
 * /ttadmin team <tag> edit color <color> - Changes the color of a team.
 * /ttadmin team <tag> edit souls <number> - Sets the number of souls for a team.
 * /ttadmin team <tag> info - Displays information about a team by name.
 * /ttadmin profile <player_name> - Displays the profile of a player.
 * /ttadmin profile <player_name> stats add <stat> <value> - Adds a value to a stat for a player.
 * /ttadmin profile <player_name> stats remove <stat> <value> - Removes a value from a stat for a player.
 * /ttadmin profile <player_name> stats set <stat> <value> - Sets a stat value for a player.
 * /ttadmin profile <player_name> stats reset - Resets all stats for a player.
 *
 */

@SuppressWarnings("UnstableApiUsage")
public class AdminCommand implements CommandBase {

    /*
     * Ugly ass command structure with inner execution logic
     * Will change it later (probably)
     */
    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("ttadmin")
                .requires(source -> source.getSender().hasPermission("thetowers.admin"))
                .then(Commands.literal("item")
                        .then(Commands.argument("item_name", StringArgumentType.word())
                                .suggests(SuggestionProviders.fromCollection(ItemGenerator.ITEMS.keySet()))
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    if (!(sender instanceof Player player)) {
                                        sender.sendRichMessage("<red>This command can only be used by players.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    String itemName = ctx.getArgument("item_name", String.class);

                                    if (!ItemGenerator.ITEMS.containsKey(itemName)) {
                                        sender.sendRichMessage("<red>Item '" + itemName + "' does not exist.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    player.getInventory().addItem(ItemGenerator.ITEMS.get(itemName).get());
                                    sender.sendRichMessage("<green>Gave you item: <white>" + itemName);

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build())
                .then(Commands.literal("team")
                        .then(Commands.argument("tag", StringArgumentType.string())
                                .then(Commands.literal("disband")
                                        .executes(ctx -> {
                                            CommandSender sender = ctx.getSource().getSender();

                                            String tag = ctx.getArgument("tag", String.class);

                                            TheTowers towers = TheTowers.getInstance();
                                            GameManager game = towers.getGameManager();
                                            GameTeam team = game.getTeam(tag);

                                            if (team == null) {
                                                sender.sendRichMessage("<red>Team with tag '" + tag + "' does not exist.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            game.removeTeam(team);
                                            sender.sendRichMessage("<green>Disbanded team: <white>" + team.getDisplayName());

                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .build())
                                .then(Commands.literal("edit")
                                        .then(Commands.literal("color")
                                                .then(Commands.argument("color", StringArgumentType.word())
                                                        .suggests((ctx, builder) -> {
                                                            for (GameTeam.Color color : GameTeam.Color.values()) {
                                                                builder.suggest(color.getName());
                                                            }
                                                            return builder.buildFuture();
                                                        })
                                                        .executes(ctx -> {
                                                            CommandSender sender = ctx.getSource().getSender();

                                                            String tag = ctx.getArgument("tag", String.class);
                                                            String colorName = ctx.getArgument("color", String.class);
                                                            GameTeam.Color color = GameTeam.Color.from(colorName.toLowerCase());

                                                            if (color == null) {
                                                                sender.sendRichMessage("<red>Invalid color: " + colorName);
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            TheTowers towers = TheTowers.getInstance();
                                                            GameManager game = towers.getGameManager();

                                                            Optional<GameTeam> sameColor = game.getTeams().stream()
                                                                    .filter(team -> team.getColor() == color)
                                                                    .findFirst();
                                                            if (sameColor.isPresent()) {
                                                                sender.sendRichMessage("<red>There is already a team with color '" + color.getName() + "'.");
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            GameTeam team = game.getTeam(tag);

                                                            if (team == null) {
                                                                sender.sendRichMessage("<red>Team with tag '" + tag + "' does not exist.");
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            team.setColor(color);
                                                            sender.sendRichMessage("<green>Changed team " + tag + " color to: <white>" + color.getName());

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                        .build())
                                                .build())
                                        .then(Commands.literal("souls")
                                                .then(Commands.argument("number", IntegerArgumentType.integer(0))
                                                        .executes(ctx -> {
                                                            CommandSender sender = ctx.getSource().getSender();

                                                            String tag = ctx.getArgument("tag", String.class);
                                                            int souls = ctx.getArgument("number", Integer.class);

                                                            TheTowers towers = TheTowers.getInstance();
                                                            GameManager game = towers.getGameManager();
                                                            GameTeam team = game.getTeam(tag);

                                                            if (team == null) {
                                                                sender.sendRichMessage("<red>Team with tag '" + tag + "' does not exist.");
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            team.setSouls(souls);
                                                            sender.sendRichMessage("<green>Set team " + tag + " souls to: <white>" + souls);

                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                        .build())
                                                .build())
                                        .build())
                                .then(Commands.literal("info")
                                        .executes(ctx -> {
                                            CommandSender sender = ctx.getSource().getSender();

                                            String tag = ctx.getArgument("tag", String.class);

                                            TheTowers towers = TheTowers.getInstance();
                                            GameManager game = towers.getGameManager();
                                            GameTeam team = game.getTeam(tag);

                                            if (team == null) {
                                                sender.sendRichMessage("<red>Team with tag '" + tag + "' does not exist.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            sender.sendRichMessage("<green>Team Information:");
                                            sender.sendRichMessage("<gray>Tag: <white>" + team.getTag());
                                            sender.sendRichMessage("<gray>Color: <white>" + team.getColor());
                                            sender.sendRichMessage("<gray>Leader: <white>" + team.getLeader().getName());
                                            sender.sendRichMessage("<gray>Members: <white>" + team.getMembers().size());
                                            for (GamePlayer member : team.getMembers()) {
                                                sender.sendRichMessage("<gray> - <white>" + member.getName());
                                            }
                                            sender.sendRichMessage("<gray>Is Alive: <white>" + (team.isAlive() ? "Yes" : "No"));
                                            sender.sendRichMessage("<gray>Souls: <white>" + team.getSouls());

                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .build())
                                .build())
                        .build())
                .then(Commands.literal("profile")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(SuggestionProviders.fromCollection(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()))
                                .executes(ctx -> profile(ctx.getSource().getSender(), ctx.getArgument("player", String.class)))
                                .then(Commands.literal("stats")
                                        .then(Commands.literal("add")
                                                .then(statArgument()
                                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                                .executes(ctx -> profileStatAdd(ctx.getSource().getSender(),
                                                                        ctx.getArgument("player", String.class),
                                                                        ctx.getArgument("stat", String.class),
                                                                        ctx.getArgument("value", Integer.class)))
                                                                .build())
                                                        .build())
                                                .build())
                                        .then(Commands.literal("remove")
                                                .then(statArgument()
                                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                                .executes(ctx -> profileStatRemove(ctx.getSource().getSender(),
                                                                        ctx.getArgument("player", String.class),
                                                                        ctx.getArgument("stat", String.class),
                                                                        ctx.getArgument("value", Integer.class)))
                                                                .build())
                                                        .build())
                                                .build())
                                        .then(Commands.literal("set")
                                                .then(statArgument()
                                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                                .executes(ctx -> profileStatSet(ctx.getSource().getSender(),
                                                                        ctx.getArgument("player", String.class),
                                                                        ctx.getArgument("stat", String.class),
                                                                        ctx.getArgument("value", Integer.class)))
                                                                .build())
                                                        .build())
                                                .build())
                                        .then(Commands.literal("reset")
                                                .executes(ctx -> profileStatReset(ctx.getSource().getSender(),
                                                        ctx.getArgument("player", String.class)))
                                                .build())
                                        .then(statsInfo().build())
                                        .build())
                                .build())
                        .build())
                .build();
    }

    @Override
    public @NotNull List<String> aliases() {
        return List.of("thetowersadmin");
    }

    private LiteralArgumentBuilder<CommandSourceStack> statsInfo() {
        return Commands.literal("info")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    String playerName = ctx.getArgument("player", String.class);
                    Optional<PlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);
                    if (profile.isEmpty()) {
                        sender.sendRichMessage("<red>Nie można znaleźć profilu dla gracza " + playerName);
                        return Command.SINGLE_SUCCESS;
                    }

                    sender.sendRichMessage(Colors.format(Colors.PRIMARY) + "Statystyki gracza <white>" + playerName + "</white>:");
                    PlayerTotalStat.values().forEach(stat -> {
                        int value = profile.get().getStats().getStat(stat);
                        String formattedStat = Colors.format(Colors.SECONDARY) + stat.getKey() + ": <white>" + value;
                        sender.sendRichMessage(formattedStat);
                    });

                    return Command.SINGLE_SUCCESS;
                });
    }

    private RequiredArgumentBuilder<CommandSourceStack, String> statArgument() {
        return Commands.argument("stat", StringArgumentType.word())
                .suggests(SuggestionProviders.fromCollection(PlayerStat.values().stream()
                        .map(PlayerStat::getKey)
                        .toList()));
    }

    private int profile(CommandSender sender, String playerName) {
        Optional<PlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);
        if (profile.isEmpty()) {
            sender.sendRichMessage("<red>Nie można znaleźć profilu dla gracza " + playerName);
            return 0;
        }

        sender.sendRichMessage(Colors.format(Colors.PRIMARY) + "Profil gracza <white>" + playerName + "</white>:");
        sender.sendRichMessage(Colors.format(Colors.PRIMARY) + "Statystyki:");
        profile.get().getStats().forEach(stat -> {
            String formattedStat = Colors.format(Colors.SECONDARY) + stat.getKey() + ": <white>" + stat.getValue();
            sender.sendRichMessage(formattedStat);
        });

        return Command.SINGLE_SUCCESS;
    }

    private int profileStatAdd(CommandSender sender, String playerName, String statKey, int value) {
        Optional<PlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);
        if (profile.isEmpty()) {
            sender.sendRichMessage("<red>Nie można znaleźć profilu dla gracza " + playerName);
            return 0;
        }

        PlayerStat stat = PlayerTotalStat.fromKey(statKey);
        if (stat == null) {
            sender.sendRichMessage("<red>Nie znaleziono statystyki: " + statKey);
            return 0;
        }

        profile.get().getStats().incrementStat(stat, value);
        sender.sendRichMessage("<green>Dodano <white>" + value + "</white> do statystyki <white>" + stat.getKey() + "</white> gracza <white>" + playerName + "</white>.");

        return Command.SINGLE_SUCCESS;
    }

    private int profileStatRemove(CommandSender sender, String playerName, String statKey, int value) {
        Optional<PlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);
        if (profile.isEmpty()) {
            sender.sendRichMessage("<red>Nie można znaleźć profilu dla gracza " + playerName);
            return 0;
        }

        PlayerStat stat = PlayerTotalStat.fromKey(statKey);
        if (stat == null) {
            sender.sendRichMessage("<red>Nie znaleziono statystyki: " + statKey);
            return 0;
        }

        profile.get().getStats().decrementStat(stat, value);
        sender.sendRichMessage("<green>Usunięto <white>" + value + "</white> z statystyki <white>" + stat.getKey() + "</white> gracza <white>" + playerName + "</white>.");

        return Command.SINGLE_SUCCESS;
    }

    private int profileStatSet(CommandSender sender, String playerName, String statKey, int value) {
        Optional<PlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);
        if (profile.isEmpty()) {
            sender.sendRichMessage("<red>Nie można znaleźć profilu dla gracza " + playerName);
            return 0;
        }

        PlayerStat stat = PlayerTotalStat.fromKey(statKey);
        if (stat == null) {
            sender.sendRichMessage("<red>Nie znaleziono statystyki: " + statKey);
            return 0;
        }

        profile.get().getStats().setStat(stat, value);
        sender.sendRichMessage("<green>Ustawiono <white>" + value + "</white> dla statystyki <white>" + stat.getKey() + "</white> gracza <white>" + playerName + "</white>.");

        return Command.SINGLE_SUCCESS;
    }

    private int profileStatReset(CommandSender sender, String playerName) {
        Optional<PlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);
        if (profile.isEmpty()) {
            sender.sendRichMessage("<red>Nie można znaleźć profilu dla gracza " + playerName);
            return 0;
        }

        profile.get().getStats().reset();
        sender.sendRichMessage("<green>Zresetowano wszystkie statystyki gracza <white>" + playerName + "</white>.");

        return Command.SINGLE_SUCCESS;
    }

}
