package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.config.i18n.Messages;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/*
 *
 * /team
 * /team create <color> <name>
 * /team disband
 * /team join <name>
 * /team leave
 * /team remove <player>
 * /team rename <new_name>
 * /team set-color <color>
 * /team set-leader <new_leader>
 *
 */

@SuppressWarnings("UnstableApiUsage")
public class TeamCommand implements CommandBase {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("team")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team"))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Messages.TEAM_COMMAND_USAGE.forEach(line -> sender.sendRichMessage(line.get()));
                    return Command.SINGLE_SUCCESS;
                })
                .then(teamCreate())
                .then(teamDisband())
                .then(teamJoin())
                .then(teamLeave())
                .then(teamRemove())
                .then(teamRename())
                .then(teamSetColor())
                .then(teamSetLeader())
                .build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamCreate() {
        return Commands.literal("create")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.create"))
                .then(Commands.argument("color", StringArgumentType.word())
                        .suggests(SuggestionProviders.fromCollection(Arrays.stream(GameTeam.Color.values())
                                .map(Enum::name)
                                .toList()))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String colorName = ctx.getArgument("color", String.class).toUpperCase(Locale.ROOT);
                                    String teamName = ctx.getArgument("name", String.class);
                                    getGamePlayer(ctx, player -> {
                                        GameTeam.Color color = GameTeam.Color.from(colorName);
                                        if (color == null) {
                                            player.sendMessage(Messages.TEAM_INVALID_COLOR.get(colorName));
                                            return;
                                        }

                                        if (!GameTeam.isValidName(teamName)) {
                                            player.sendMessage(Messages.TEAM_INVALID_NAME.get(teamName));
                                            return;
                                        }

                                        GameManager gm = TheTowers.getInstance().getGameManager();
                                        Optional<GameTeam> existingTeam = gm.getTeam(color);
                                        if (existingTeam.isPresent()) {
                                            player.sendMessage(Messages.TEAM_COLOR_ALREADY_TAKEN.get(colorName));
                                            return;
                                        }
                                        existingTeam = gm.getTeam(teamName);
                                        if (existingTeam.isPresent()) {
                                            player.sendMessage(Messages.TEAM_ALREADY_EXISTS.get(teamName));
                                            return;
                                        }

                                        GameTeam team = GameManager.createTeam(player, teamName, color);
                                        gm.addTeam(team);
                                        player.sendMessage(Messages.TEAM_CREATED.get(team.getDisplayName()));
                                    });
                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamDisband() {
        return Commands.literal("disband")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.disband"))
                .executes(ctx -> {
                    getGamePlayer(ctx, player -> {
                        GameTeam team = player.getTeam();
                        if (team == null) {
                            player.sendMessage(Messages.NOT_IN_TEAM.get());
                            return;
                        }

                        if (team.getLeader() != player) {
                            player.sendMessage(Messages.HAVE_TO_BE_LEADER_TO_DISBAND.get());
                            return;
                        }

                        GameManager gm = TheTowers.getInstance().getGameManager();
                        gm.removeTeam(team);
                    });
                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamJoin() {
        return Commands.literal("join")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.join"))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> {
                            String teamName = ctx.getArgument("name", String.class);
                            getGamePlayer(ctx, player -> {
                                GameTeam currentTeam = player.getTeam();
                                if (currentTeam != null) {
                                    player.sendMessage(Messages.ALREADY_IN_TEAM.get());
                                    return;
                                }

                                GameManager gm = TheTowers.getInstance().getGameManager();
                                Optional<GameTeam> team = gm.getTeam(teamName);
                                if (team.isEmpty()) {
                                    player.sendMessage(Messages.TEAM_NOT_FOUND.get(teamName));
                                    return;
                                }

                                if (team.get().isFull()) {
                                    player.sendMessage(Messages.TEAM_FULL.get());
                                    return;
                                }

                                team.get().addMember(player);
                                player.sendMessage(Messages.TEAM_JOIN.get(team.get().getDisplayName()));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamLeave() {
        return Commands.literal("leave")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.leave"))
                .executes(ctx -> {
                    getGamePlayer(ctx, player -> {
                        GameTeam team = player.getTeam();
                        if (team == null) {
                            player.sendMessage(Messages.NOT_IN_TEAM.get());
                            return;
                        }

                        if (team.getLeader() == player) {
                            player.sendMessage(Messages.CANNOT_LEAVE_IF_LEADER.get());
                            return;
                        }

                        team.removeMember(player);
                        player.sendMessage(Messages.TEAM_LEAVE.get(team.getDisplayName()));
                    });
                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamRemove() {
        return Commands.literal("remove")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.remove"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            String playerName = ctx.getArgument("player", String.class);
                            getGamePlayer(ctx, player -> {
                                GameTeam team = requireTeamLeader(player);
                                if (team == null) return;

                                GamePlayer targetPlayer = requirePlayerInTeam(team, playerName, player);
                                if (targetPlayer == null) return;

                                if (targetPlayer == player) {
                                    player.sendMessage(Messages.CANNOT_REMOVE_YOURSELF_FROM_TEAM.get());
                                    return;
                                }

                                team.removeMember(targetPlayer, true);
                                targetPlayer.sendMessage(Messages.KICKED_FROM_TEAM.get(team.getDisplayName()));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamRename() {
        return Commands.literal("rename")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.rename"))
                .then(Commands.argument("new_name", StringArgumentType.word())
                        .executes(ctx -> {
                            String newName = ctx.getArgument("new_name", String.class);
                            getGamePlayer(ctx, player -> {
                                GameTeam team = requireTeamLeader(player);
                                if (team == null) return;

                                if (!GameTeam.isValidName(newName)) {
                                    player.sendMessage(Messages.TEAM_INVALID_NAME.get(newName));
                                    return;
                                }

                                if (TheTowers.getInstance().getGameManager().getTeam(newName).isPresent()) {
                                    player.sendMessage(Messages.TEAM_ALREADY_EXISTS.get(newName));
                                    return;
                                }

                                String oldName = team.getName();
                                team.setName(newName);
                                player.sendMessage(Messages.TEAM_CHANGED_NAME.get(oldName, newName));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamSetColor() {
        return Commands.literal("set-color")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.set-color"))
                .then(Commands.argument("color", StringArgumentType.word())
                        .suggests(SuggestionProviders.fromCollection(Arrays.stream(GameTeam.Color.values())
                                .map(Enum::name)
                                .toList()))
                        .executes(ctx -> {
                            String colorName = ctx.getArgument("color", String.class).toUpperCase(Locale.ROOT);
                            getGamePlayer(ctx, player -> {
                                GameTeam team = requireTeamLeader(player);
                                if (team == null) return;

                                GameTeam.Color color = GameTeam.Color.from(colorName);
                                if (color == null) {
                                    player.sendMessage(Messages.TEAM_INVALID_COLOR.get(colorName));
                                    return;
                                }

                                if (TheTowers.getInstance().getGameManager().getTeam(color).isPresent()) {
                                    player.sendMessage(Messages.TEAM_COLOR_ALREADY_TAKEN.get(colorName));
                                    return;
                                }

                                team.setColor(color);
                                player.sendMessage(Messages.TEAM_COLOR_CHANGED.get(color));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private LiteralArgumentBuilder<CommandSourceStack> teamSetLeader() {
        return Commands.literal("set-leader")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.team.set-leader"))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            getGamePlayer(ctx, player -> {
                                GameTeam team = player.getTeam();
                                if (team == null) {
                                    return;
                                }
                                team.getMembers().stream()
                                        .filter(member -> !member.equals(player))
                                        .map(GamePlayer::getName)
                                        .iterator()
                                        .forEachRemaining(builder::suggest);
                            });
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String playerName = ctx.getArgument("player", String.class);
                            getGamePlayer(ctx, player -> {
                                GameTeam team = requireTeamLeader(player);
                                if (team == null) return;

                                GamePlayer targetPlayer = requirePlayerInTeam(team, playerName, player);
                                if (targetPlayer == null) return;

                                team.setLeader(targetPlayer);
                                player.sendMessage(Messages.TEAM_TRANSFERRED_LEADER.get(targetPlayer.getDisplayNameNoTag()));
                                targetPlayer.sendMessage(Messages.TEAM_LEADERSHIP_GIVEN.get(team.getDisplayName()));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        .build());
    }

    private void getGamePlayer(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull Consumer<GamePlayer> action) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage(Messages.PLAYER_ONLY.get());
            return;
        }

        Optional<GamePlayer> gamePlayer = TheTowers.getInstance().getPlayerManager().getPlayer(player.getName());
        if (gamePlayer.isEmpty()) {
            return;
        }

        GameManager.Stage stage = TheTowers.getInstance().getGameManager().getStage();
        if (stage != GameManager.Stage.LOBBY) {
            player.sendRichMessage(Messages.GAME_ACTIVE.get());
            return;
        }

        action.accept(gamePlayer.get());
    }

    private @Nullable GameTeam requireInTeam(@NotNull GamePlayer player) {
        GameTeam team = player.getTeam();
        if (team == null) {
            player.sendMessage(Messages.NOT_IN_TEAM.get());
            return null;
        }
        return team;
    }

    private @Nullable GameTeam requireTeamLeader(@NotNull GamePlayer player) {
        GameTeam team = requireInTeam(player);
        if (team == null) {
            return null;
        }

        if (!player.equals(team.getLeader())) {
            player.sendMessage(Messages.MUST_BE_A_LEADER.get());
            return null;
        }

        return team;
    }

    private @Nullable GamePlayer requirePlayerInTeam(GameTeam team, String name, GamePlayer requester) {
        Optional<GamePlayer> target = TheTowers.getInstance().getGameManager().getPlayer(name);
        if (target.isEmpty() || !team.hasMember(target.get())) {
            requester.sendMessage(Messages.PLAYER_NOT_IN_TEAM.get());
            return null;
        }
        return target.get();
    }

}
