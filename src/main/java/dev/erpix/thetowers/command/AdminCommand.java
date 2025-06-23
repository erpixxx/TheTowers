package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.*;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.util.Colors;
import dev.erpix.thetowers.util.ItemGenerator;
import dev.erpix.thetowers.util.TriConsumer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/*
 *
 * /ttadmin item <item_name> - Gives an item by name.
 * /ttadmin team <tag> disband - Disbands a team by name.
 * /ttadmin team <tag> edit name <new_name> - Changes the name of a team.
 * /ttadmin team <tag> edit color <color> - Changes the color of a team.
 * /ttadmin team <tag> edit souls add <number> - Adds a number of souls to a team.
 * /ttadmin team <tag> edit souls set <number> - Sets the number of souls for a team.
 * /ttadmin team <tag> edit souls remove <number> - Removes a number of souls from a team.
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
                        .then(teamName()
                                .then(teamDisband())
                                .then(Commands.literal("edit")
                                        .then(teamEditName())
                                        .then(teamEditColor())
                                        .then(teamEditSouls())
                                        .build())
                                .then(teamInfo())
                                .build())
                        .build())
                .then(Commands.literal("profile")
                        .then(Commands.argument("player_name", StringArgumentType.word())
                                .then(profileInfo())
                                .then(profileStats())
                                .build())
                        .build())
                .build();
    }

    @Override
    public @NotNull List<String> aliases() {
        return List.of("thetowersadmin");
    }

    private RequiredArgumentBuilder<CommandSourceStack, String> teamName() {
        return Commands.argument("team_name", StringArgumentType.word())
                .suggests(SuggestionProviders.fromCollection(TheTowers.getInstance().getGameManager().getTeams().stream()
                        .map(GameTeam::getName).toList()));
    }

    private void findTeam(CommandContext<CommandSourceStack> ctx, Consumer<GameTeam> action) {
        CommandSender sender = ctx.getSource().getSender();
        String teamName = ctx.getArgument("team_name", String.class);

        Optional<GameTeam> teamOpt = TheTowers.getInstance().getGameManager().getTeam(teamName);
        if (teamOpt.isEmpty()) {
            sender.sendRichMessage("<red>Cannot find team with name: " + teamName);
            return;
        }

        GameTeam team = teamOpt.get();
        action.accept(team);
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamDisband() {
        return Commands.literal("disband")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    findTeam(ctx, team -> {
                        TheTowers.getInstance().getGameManager().removeTeam(team);
                        sender.sendRichMessage("<green>Disbanded team " + team.getDisplayName() + ".");
                    });
                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamEditName() {
        return Commands.literal("name")
                .then(Commands.argument("new_name", StringArgumentType.word())
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String newName = ctx.getArgument("new_name", String.class);
                            findTeam(ctx, team -> {
                                String oldName = team.getName();
                                if (!GameTeam.isValidName(newName)) {
                                    sender.sendRichMessage("<red>Invalid team name: " + newName);
                                    return;
                                }
                                if (newName.equals(oldName)) {
                                    sender.sendRichMessage("<red>Team name is already set to '" + newName + "'.");
                                    return;
                                }
                                if (TheTowers.getInstance().getGameManager().getTeam(newName).isPresent()) {
                                    sender.sendRichMessage("<red>Team with name '" + newName + "' already exists.");
                                    return;
                                }
                                team.setName(newName);
                                sender.sendRichMessage("<green>Changed team name from '<gray>" + oldName + "</gray>' to '<gray>" + newName + "</gray>'.");
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamEditColor() {
        return Commands.literal("color")
                .then(Commands.argument("color", StringArgumentType.word())
                        .suggests(SuggestionProviders.fromCollection(
                                Arrays.stream(GameTeam.Color.values())
                                        .map(GameTeam.Color::getName)
                                        .toList()))
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String colorName = ctx.getArgument("color", String.class);
                            findTeam(ctx, team -> {
                                GameTeam.Color color = GameTeam.Color.from(colorName);
                                if (color == null) {
                                    sender.sendRichMessage("<red>Invalid color: " + colorName);
                                    return;
                                }
                                if (team.getColor() == color) {
                                    sender.sendRichMessage("<red>Team color is already set to '" + color.name() + "'.");
                                    return;
                                }
                                if (TheTowers.getInstance().getGameManager().getTeams().stream()
                                        .anyMatch(t -> t != team && t.getColor() == color)) {
                                    sender.sendRichMessage("<red>Another team already has the color '" + color.name() + "'.");
                                    return;
                                }
                                team.setColor(color);
                                sender.sendRichMessage("<green>Changed team color to <color:#" + color.getColorHex() + ">" + color.name() + "</color>.");
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamEditSouls() {
        return Commands.literal("souls")
                .then(Commands.literal("add")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                .executes(teamEditSoulsCommand(GameTeam::addSouls))
                                .build())
                        .build())
                .then(Commands.literal("set")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                .executes(teamEditSoulsCommand(GameTeam::setSouls))
                                .build())
                        .build())
                .then(Commands.literal("remove")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                .executes(teamEditSoulsCommand(GameTeam::removeSouls))
                                .build())
                        .build());
    }

    private Command<CommandSourceStack> teamEditSoulsCommand(BiConsumer<GameTeam, Integer> action) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            int value = ctx.getArgument("value", Integer.class);
            findTeam(ctx, team -> {
                action.accept(team, value);
                sender.sendRichMessage("<green>Updated souls for team " + team.getDisplayName() + ": <white>" + team.getSouls() + "</white>.");
            });
            return Command.SINGLE_SUCCESS;
        };
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamInfo() {
        return Commands.literal("info")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    findTeam(ctx, team -> {
                        sender.sendRichMessage(Colors.format(Colors.PRIMARY) + "Team <white>" + team.getDisplayName() + "</white>:");
                        sender.sendRichMessage(Colors.format(Colors.SECONDARY) + "Leader: <white>" + team.getLeader().getName());
                        sender.sendRichMessage(Colors.format(Colors.SECONDARY) + "Color: <color:#" + team.getColor().getColorHex() + ">" + team.getColor().name());
                        sender.sendRichMessage(Colors.format(Colors.SECONDARY) + "Souls: <white>" + team.getSouls());
                        sender.sendRichMessage(Colors.format(Colors.SECONDARY) + "Heart Health: <white>" + team.getHeartHealth());
                        sender.sendRichMessage(Colors.format(Colors.SECONDARY) + "Members: <white>" + String.join(", ", team.getMembers().stream()
                                .map(GamePlayer::getName).toList()));
                    });
                    return Command.SINGLE_SUCCESS;
                });
    }

    /*
     * Profile commands
     */

    private LiteralArgumentBuilder<CommandSourceStack> profileInfo() {
        return Commands.literal("info")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    String playerName = ctx.getArgument("player_name", String.class);

                    ProfileManager pm = TheTowers.getInstance().getProfileManager();
                    pm.getProfile(playerName).ifPresentOrElse(profile -> {
                        sender.sendRichMessage(Colors.format(Colors.PRIMARY) + "Profile for <white>" + playerName + "</white>:");
                        for (PlayerStat stat : PlayerTotalStat.values()) {
                            int value = profile.getStats().getStat(stat);
                            sender.sendRichMessage(Colors.format(Colors.SECONDARY) + stat.getKey() + ": <white>" + value);
                        }
                    }, () -> sender.sendRichMessage("<red>Profile for player '" + playerName + "' not found."));

                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> profileStats() {
        return Commands.literal("stats")
                .then(Commands.literal("add")
                        .then(editProfileStat((profile, stat, value) -> profile.getStats().incrementStat(stat, value))))
                .then(Commands.literal("set")
                        .then(editProfileStat((profile, stat, value) -> profile.getStats().setStat(stat, value))))
                .then(Commands.literal("remove")
                        .then(editProfileStat((profile, stat, value) -> profile.getStats().decrementStat(stat, value))))
                .then(Commands.literal("reset")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String playerName = ctx.getArgument("player_name", String.class);

                            ProfileManager pm = TheTowers.getInstance().getProfileManager();
                            pm.getProfile(playerName).ifPresentOrElse(profile -> {
                                profile.getStats().reset();
                                sender.sendRichMessage("<green>Reset all stats for player <gray>" + playerName + "</gray>.");
                            }, () -> sender.sendRichMessage("<red>Profile for player '" + playerName + "' not found."));

                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private RequiredArgumentBuilder<CommandSourceStack, String> editProfileStat(TriConsumer<PlayerProfile, PlayerStat, Integer> action) {
        return Commands.argument("stat", StringArgumentType.word())
                .suggests(SuggestionProviders.fromCollection(Stream.of(PlayerStat.values(), PlayerTotalStat.values())
                        .flatMap(Collection::stream)
                        .map(PlayerStat::getKey)
                        .toList()))
                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String playerName = ctx.getArgument("player_name", String.class);
                            String statKey = ctx.getArgument("stat", String.class);
                            int value = ctx.getArgument("value", Integer.class);

                            PlayerStat stat = PlayerTotalStat.fromKey(statKey);
                            if (stat == null) {
                                sender.sendRichMessage("<red>Invalid stat key: " + statKey);
                                return Command.SINGLE_SUCCESS;
                            }

                            ProfileManager pm = TheTowers.getInstance().getProfileManager();
                            pm.getProfile(playerName).ifPresentOrElse(profile -> {
                                action.accept(profile, stat, value);
                                sender.sendRichMessage("<green>Updated stat <gray>" + stat.getKey() + "</gray> for player <gray>" + playerName + "</gray> to <gray>" + value + "</gray>.");
                            }, () -> sender.sendRichMessage("<red>Profile for player '" + playerName + "' not found."));

                            return Command.SINGLE_SUCCESS;
                        }));
    }

}
