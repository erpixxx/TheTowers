package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GameMap;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/*
 *
 * /game new
 * /game start
 * /game status
 * /game stop
 *
 */

@SuppressWarnings("UnstableApiUsage")
public class GameCommand implements CommandBase {

    private final TheTowers theTowers = TheTowers.getInstance();

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("game")
                .then(Commands.literal("map")
                        .then(Commands.argument("map", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (GameMap map : theTowers.getMaps()) {
                                        builder.suggest(map.getName());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    String mapName = StringArgumentType.getString(ctx, "map");
                                    Optional<GameMap> map = theTowers.getMap(mapName);

                                    if (map.isEmpty()) {
                                        sender.sendRichMessage("<red>Nie znaleziono mapy o nazwie " + mapName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    theTowers.getGameManager().setMap(map.get());
                                    sender.sendRichMessage("<green>Ustawiono mapę na <gray>" + map.get().getName());

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build())
                .then(Commands.literal("start")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            GameManager game = theTowers.getGameManager();
                            game.start();

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .then(Commands.literal("status")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            GameManager game = theTowers.getGameManager();

                            sender.sendRichMessage("Status gry: " + game.getStage());
                            sender.sendRichMessage("Mapa: " + game.getMap().getName());
                            Collection<GameTeam> teams = game.getTeams();
                            sender.sendRichMessage("<gray>Drużyny:");
                            if (teams.isEmpty()) {
                                sender.sendRichMessage("<red>Brak drużyn w grze.");
                            } else {
                                for (GameTeam team : teams) {
                                    String teamInfo = String.format("  <color:#%s>%s</color> <gray>(%d graczy)",
                                            team.getColor().getColorHex(), team.getDisplayName(), team.getMembers().size());
                                    sender.sendRichMessage(teamInfo);
                                }
                            }
                            Collection<GamePlayer> spectators = game.getSpectators();
                            sender.sendRichMessage("<gray>Widzowie:");
                            if (spectators.isEmpty()) {
                                sender.sendRichMessage("  <red>Brak widzów w grze.");
                            } else {
                                for (GamePlayer spectator : spectators) {
                                    sender.sendRichMessage("  <gray>" + spectator.getDisplayName());
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .then(Commands.literal("stop")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            GameManager game = theTowers.getGameManager();

                            if (game.getStage() != GameManager.Stage.IN_PROGRESS) {
                                sender.sendRichMessage("<red>Nie ma aktywnej gry do zatrzymania.");
                                return Command.SINGLE_SUCCESS;
                            }

                            game.stop();
                            sender.sendRichMessage("<green>Gra została zatrzymana.");

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .build();
    }

    private int gameStart(CommandSender sender, boolean force) {

        GameManager game = theTowers.getGameManager();
        GameManager.Stage stage = game.getStage();
        if (stage != GameManager.Stage.LOBBY) {
            sender.sendRichMessage("<red>Gra nie jest w stanie lobby, nie można jej rozpocząć.");
            return 0;
        }

        Collection<GameTeam> teams = game.getTeams();
        if (teams.isEmpty()) {
            sender.sendRichMessage("<red>Brak drużyn w grze.");
            return 0;
        }

        int firstTeamSize = -1;
        boolean sameSize = true;
        for (GameTeam team : teams) {
            Collection<GamePlayer> members = team.getMembers();
            if (members.isEmpty()) {
                sender.sendRichMessage("<red>Drużyna " + team.getDisplayName() + " nie ma żadnych graczy.");
                return 0;
            }
            int size = members.size();
            if (firstTeamSize < 0) {
                firstTeamSize = size;
            } else if (size != firstTeamSize) {
                sameSize = false;
                break;
            }
        }

        if (!sameSize && !force) {
            sender.sendRichMessage("<red>Drużyny muszą mieć tę samą liczbę graczy, aby rozpocząć grę.");
            return 0;
        }

        game.start();

        return Command.SINGLE_SUCCESS;
    }

}
