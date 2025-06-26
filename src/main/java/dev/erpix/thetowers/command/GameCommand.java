package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.config.i18n.Messages;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GameMap;
import dev.erpix.thetowers.model.game.GameTeam;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/*
 *
 * /ttgame map <map_name>
 * /ttgame max-players <number>
 * /ttgame start [force]
 * /ttgame status
 * /ttgame stop
 *
 */

@SuppressWarnings("UnstableApiUsage")
public class GameCommand implements CommandBase {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("game")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.game"))
                .then(map())
                .then(maxPlayers())
                .then(start())
                .then(status())
                .then(stop())
                .build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> map() {
        return Commands.literal("map")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.game.map"))
                .then(Commands.argument("map_name", StringArgumentType.string())
                        .suggests(SuggestionProviders.fromCollection(TheTowers.getInstance().getMaps().stream()
                                .map(GameMap::getName)
                                .toList()))
                        .executes(ctx -> {
                            TheTowers theTowers = TheTowers.getInstance();
                            CommandSender sender = ctx.getSource().getSender();
                            String mapName = ctx.getArgument("map_name", String.class);
                            Optional<GameMap> map = theTowers.getMap(mapName);
                            if (map.isEmpty()) {
                                sender.sendRichMessage(Messages.MAP_NOT_FOUND.get(mapName));
                                return Command.SINGLE_SUCCESS;
                            }
                            theTowers.getGameManager().setMap(map.get());
                            sender.sendRichMessage(Messages.MAP_SET.get(mapName));
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private LiteralArgumentBuilder<CommandSourceStack> maxPlayers() {
        return Commands.literal("max-players")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.game.max-players"))
                .then(Commands.argument("number", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            int maxPlayers = ctx.getArgument("number", Integer.class);
                            TheTowers.getInstance().getGameManager().setMaxPlayersPerTeam(maxPlayers);
                            sender.sendRichMessage(Messages.CHANGED_MAX_PLAYERS.get(maxPlayers));
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    private LiteralArgumentBuilder<CommandSourceStack> start() {
        return Commands.literal("start")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.game.start"))
                .executes(ctx -> gameStart(ctx.getSource().getSender(), false))
                .then(Commands.literal("force")
                        .executes(ctx -> gameStart(ctx.getSource().getSender(), true)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> status() {
        return Commands.literal("status")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.game.status"))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    GameManager gm = TheTowers.getInstance().getGameManager();

                    Iterator<Messages.Message> messages = Messages.GAME_STATUS.iterator();
                    sender.sendRichMessage(messages.next().get());
                    sender.sendRichMessage(messages.next().get(gm.getMap().getName()));
                    sender.sendRichMessage(messages.next().get(gm.getStage().name()));
                    sender.sendRichMessage(messages.next().get());
                    for (GameTeam team : gm.getTeams()) {
                        sender.sendRichMessage(Messages.GAME_STATUS_TEAM.get(team.getDisplayName(), team.getMembers().size()));
                    }

                    return Command.SINGLE_SUCCESS;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> stop() {
        return Commands.literal("stop")
                .requires(ctx -> ctx.getSender().hasPermission("thetowers.command.game.stop"))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    GameManager game = TheTowers.getInstance().getGameManager();
                    if (game.getStage() == GameManager.Stage.LOBBY) {
                        sender.sendRichMessage(Messages.GAME_NOT_RUNNING.get());
                        return 0;
                    }
                    game.stop();
                    sender.sendRichMessage(Messages.GAME_STOPPED.get());
                    return Command.SINGLE_SUCCESS;
                });
    }

    private int gameStart(CommandSender sender, boolean force) {
        GameManager gm = TheTowers.getInstance().getGameManager();
        GameManager.Stage stage = gm.getStage();
        if (stage != GameManager.Stage.LOBBY) {
            sender.sendRichMessage(Messages.GAME_ACTIVE.get());
            return 0;
        }

        Collection<GameTeam> teams = gm.getTeams();
        if (teams.size() < 2) {
            sender.sendRichMessage(Messages.MUST_BE_AT_LEAST_2_TEAMS.get());
            return 0;
        }

        int minPlayers = gm.getMaxPlayersPerTeam() / 2;
        for (GameTeam team : teams) {
            if (team.getMembers().size() < minPlayers && !force) {
                sender.sendRichMessage(Messages.TOO_FEW_MEMBERS.get(minPlayers));
                return 0;
            }
        }

        gm.start();

        return Command.SINGLE_SUCCESS;
    }

}
