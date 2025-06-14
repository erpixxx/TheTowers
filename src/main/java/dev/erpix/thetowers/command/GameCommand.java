package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.TGame;
import dev.erpix.thetowers.model.TMap;
import dev.erpix.thetowers.model.TPlayer;
import dev.erpix.thetowers.model.TTeam;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

import java.util.Collection;

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
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("game")
                .then(Commands.literal("map")
                        .then(Commands.argument("map", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    for (TMap map : theTowers.getMaps()) {
                                        builder.suggest(map.getName());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    String mapName = StringArgumentType.getString(ctx, "map");
                                    TMap map = theTowers.getMap(mapName);

                                    if (map == null) {
                                        sender.sendRichMessage("<red>Nie znaleziono mapy o nazwie " + mapName);
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    theTowers.getGame().setMap(map);
                                    sender.sendRichMessage("<green>Ustawiono mapę na <gray>" + map.getName());

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build())
                .then(Commands.literal("start")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            TGame game = theTowers.getGame();
                            game.start();

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .then(Commands.literal("status")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            TGame game = theTowers.getGame();

                            sender.sendRichMessage("Status gry: " + game.getStage());
                            sender.sendRichMessage("Mapa: " + game.getMap().getName());
                            Collection<TTeam> teams = game.getTeams();
                            sender.sendRichMessage("<gray>Drużyny:");
                            if (teams.isEmpty()) {
                                sender.sendRichMessage("<red>Brak drużyn w grze.");
                            } else {
                                for (TTeam team : teams) {
                                    String teamInfo = String.format("  <color:#%s>%s</color> <gray>(%d graczy)",
                                            team.getColor().getColorHex(), team.getDisplayName(), team.getMembers().size());
                                    sender.sendRichMessage(teamInfo);
                                }
                            }
                            Collection<TPlayer> spectators = game.getSpectators();
                            sender.sendRichMessage("<gray>Widzowie:");
                            if (spectators.isEmpty()) {
                                sender.sendRichMessage("  <red>Brak widzów w grze.");
                            } else {
                                for (TPlayer spectator : spectators) {
                                    sender.sendRichMessage("  <gray>" + spectator.getDisplayName());
                                }
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .then(Commands.literal("stop")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            TGame game = theTowers.getGame();

                            if (game.getStage() != TGame.Stage.IN_PROGRESS) {
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

        TGame game = theTowers.getGame();
        TGame.Stage stage = game.getStage();
        if (stage != TGame.Stage.LOBBY) {
            sender.sendRichMessage("<red>Gra nie jest w stanie lobby, nie można jej rozpocząć.");
            return 0;
        }

        Collection<TTeam> teams = game.getTeams();
        if (teams.isEmpty()) {
            sender.sendRichMessage("<red>Brak drużyn w grze.");
            return 0;
        }

        int firstTeamSize = -1;
        boolean sameSize = true;
        for (TTeam team : teams) {
            Collection<TPlayer> members = team.getMembers();
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
