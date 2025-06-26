package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.config.i18n.Messages;
import dev.erpix.thetowers.model.*;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.util.Colors;
import dev.erpix.thetowers.util.ItemGenerator;
import dev.erpix.thetowers.util.TriConsumer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/*
 *
 * /ttadmin broadcast <message> - Broadcasts a message to all players.
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
        return Commands.literal("thetowersadmin")
                .requires(source -> source.getSender().hasPermission("thetowers.admin"))
                .then(Commands.literal("broadcast")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String message = ctx.getArgument("message", String.class);
                                    TheTowers.getInstance().getPlayerManager().broadcast(message);
                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build())
                .then(Commands.literal("item")
                        .then(Commands.argument("item_name", StringArgumentType.word())
                                .suggests(SuggestionProviders.fromCollection(ItemGenerator.ITEMS.keySet()))
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    if (!(sender instanceof Player player)) {
                                        sender.sendRichMessage(Messages.PLAYER_ONLY.get());
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    String itemName = ctx.getArgument("item_name", String.class);

                                    if (!ItemGenerator.ITEMS.containsKey(itemName)) {
                                        sender.sendRichMessage(Messages.ITEM_NOT_FOUND.get(itemName));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    player.getInventory().addItem(ItemGenerator.ITEMS.get(itemName).get());
                                    sender.sendRichMessage(Messages.ITEM_GIVEN.get(itemName));

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
        return List.of("ttadmin");
    }

    /*
     * Team Commands
     */

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
            sender.sendRichMessage(Messages.TEAM_NOT_FOUND.get(teamName));
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
                        sender.sendRichMessage(Messages.TEAM_DISBANDED.get(team.getDisplayName()));
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
                                    sender.sendRichMessage(Messages.TEAM_INVALID_NAME.get(newName));
                                    return;
                                }
                                if (TheTowers.getInstance().getGameManager().getTeam(newName).isPresent()) {
                                    sender.sendRichMessage(Messages.TEAM_ALREADY_EXISTS.get(newName));
                                    return;
                                }
                                team.setName(newName);
                                sender.sendRichMessage(Messages.TEAM_CHANGED_NAME.get(oldName, newName));
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
                                    sender.sendRichMessage(Messages.TEAM_INVALID_COLOR.get(colorName));
                                    return;
                                }
                                if (TheTowers.getInstance().getGameManager().getTeams().stream()
                                        .anyMatch(t -> t != team && t.getColor() == color)) {
                                    sender.sendRichMessage(Messages.TEAM_COLOR_ALREADY_TAKEN.get(color.name()));
                                    return;
                                }
                                team.setColor(color);
                                sender.sendRichMessage(Messages.TEAM_COLOR_CHANGED.get("<color:#" + color.getColorHex() + ">" + color.name() + "</color>"));
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
                sender.sendRichMessage(Messages.TEAM_SOULS_UPDATED.get(team.getDisplayName(), team.getSouls()));
            });
            return Command.SINGLE_SUCCESS;
        };
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamInfo() {
        return Commands.literal("info")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    findTeam(ctx, team -> {
                        Iterator<Messages.Message> messages = Messages.ADMIN_TEAM_INFO.iterator();
                        sender.sendRichMessage(messages.next().get(team.getDisplayName()));
                        sender.sendRichMessage(messages.next().get(team.getLeader().getName()));
                        sender.sendRichMessage(messages.next().get(String.join(", ", team.getMembers().stream()
                                .map(GamePlayer::getName).toList())));
                        sender.sendRichMessage(messages.next().get(team.getColor().name()));
                        sender.sendRichMessage(messages.next().get(team.getSouls()));
                        sender.sendRichMessage(messages.next().get(team.getHeartHealth()));
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
                        sender.sendRichMessage(Messages.ADMIN_PROFILE_INFO.get(playerName));
                        for (PlayerStat stat : PlayerTotalStat.totalStats()) {
                            int value = profile.getStats().getStat(stat);
                            sender.sendRichMessage(Colors.format(Colors.PRIMARY) + "» " + Colors.format(Colors.SECONDARY) +
                                    Messages.translate(stat.getKey()) + ": <gray>" + value);
                        }
                    }, () -> sender.sendRichMessage(Messages.PROFILE_NOT_FOUND.get(playerName)));

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
                                sender.sendRichMessage(Messages.ADMIN_STATS_RESET.get(playerName));
                            }, () -> sender.sendRichMessage(Messages.PROFILE_NOT_FOUND.get(playerName)));

                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private RequiredArgumentBuilder<CommandSourceStack, String> editProfileStat(TriConsumer<PlayerProfile, PlayerStat, Integer> action) {
        return Commands.argument("stat", StringArgumentType.word())
                .suggests(SuggestionProviders.fromCollection(PlayerTotalStat.stats().stream()
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
                                sender.sendRichMessage(Messages.INVALID_STAT_KEY.get(statKey));
                                return Command.SINGLE_SUCCESS;
                            }

                            ProfileManager pm = TheTowers.getInstance().getProfileManager();
                            pm.getProfile(playerName).ifPresentOrElse(profile -> {
                                action.accept(profile, stat, value);
                                sender.sendRichMessage(Messages.ADMIN_UPDATE_STAT.get(stat.getKey(), playerName, value));
                            }, () -> sender.sendRichMessage(Messages.PROFILE_NOT_FOUND.get(playerName)));

                            return Command.SINGLE_SUCCESS;
                        }));
    }

}
