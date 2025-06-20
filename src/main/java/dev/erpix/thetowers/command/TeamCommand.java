package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GameManager;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import dev.erpix.thetowers.util.DisguiseHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class TeamCommand implements CommandBase {

    private final TheTowers theTowers = TheTowers.getInstance();

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        GameManager game = theTowers.getGameManager();
        return Commands.literal("team")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    sender.sendRichMessage("<color:#4aa1ff>Tworzenie drużyny</color>");
                    sender.sendRichMessage(" <color:#4aa1ff>»</color> <gray>/team</gray> create <green><kolor></green> <green><tag></green> <green><pełna_nazwa></green>");
                    sender.sendRichMessage("<color:#4aa1ff>Usuwanie drużyny</color>");
                    sender.sendRichMessage(" <color:#4aa1ff>»</color> <gray>/team</gray> disband");
                    sender.sendRichMessage("<color:#4aa1ff>Dołączenie do drużyny</color>");
                    sender.sendRichMessage(" <color:#4aa1ff>»</color> <gray>/team</gray> join <green><tag></green>");
                    sender.sendRichMessage("<color:#4aa1ff>Wyjście z bieżącej drużyny</color>");
                    sender.sendRichMessage(" <color:#4aa1ff>»</color> <gray>/team</gray> leave");
                    sender.sendRichMessage("<color:#4aa1ff>Usuwanie członka z drużyny</color>");
                    sender.sendRichMessage(" <color:#4aa1ff>»</color> <gray>/team</gray> remove <green><gracz></green>");

                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("create")
                        .then(Commands.argument("color", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    Set<GameTeam.Color> takenColors = game.getTeams().stream()
                                            .map(GameTeam::getColor)
                                            .collect(Collectors.toSet());
                                    for (GameTeam.Color color : GameTeam.Color.values()) {
                                        if (takenColors.contains(color)) continue;
                                        builder.suggest(color.name().toLowerCase());
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("tag", StringArgumentType.word())

                                        .executes(ctx -> {
                                            CommandSender sender = ctx.getSource().getSender();

                                            if (!(sender instanceof Player player)) {
                                                sender.sendRichMessage("<red>Ta komenda może być używana tylko przez graczy.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            GameTeam.Color color = GameTeam.Color.from(ctx.getArgument("color", String.class));
                                            if (color == null) {
                                                sender.sendRichMessage("<red>Nieprawidłowy kolor drużyny.");
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            String tag = ctx.getArgument("tag", String.class).toUpperCase(Locale.ROOT);

                                            if (tag.length() < 2 || tag.length() > 5) {
                                                sender.sendRichMessage("<red>Tag drużyny musi mieć od 2 do 5 znaków.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            if (game.getStage() == GameManager.Stage.WAITING || game.getStage() == GameManager.Stage.IN_PROGRESS) {
                                                sender.sendRichMessage("<red>Nie można utworzyć drużyny, ponieważ gra już trwa.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            Optional<GameTeam> sameColor = game.getTeams().stream()
                                                    .filter(team -> team.getColor() == color)
                                                    .findFirst();
                                            if (sameColor.isPresent()) {
                                                sender.sendRichMessage("<red>Drużyna o kolorze <color:#" + color.getColorHex() + ">" + color.getName() + "</color> już istnieje.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            Optional<GameTeam> sameTag = game.getTeams().stream()
                                                    .filter(team -> team.getTag().equalsIgnoreCase(tag))
                                                    .findFirst();
                                            if (sameTag.isPresent()) {
                                                sender.sendRichMessage("<red>Drużyna o tagu " + tag + " już istnieje.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            Optional<GamePlayer> optPlayer = theTowers.getPlayerManager().getPlayer(player.getName());
                                            if (optPlayer.isEmpty()) {
                                                sender.sendRichMessage("<red>Coś poszło kurwesko nie tak jak powinno.");
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            GamePlayer gamePlayer = optPlayer.get();
                                            GameTeam newTeam = new GameTeam(gamePlayer, tag, color);
                                            game.addTeam(newTeam);

                                            sender.sendRichMessage("<green>Stworzono drużynę o nazwie o tagu <color:#" + color.getColorHex() + ">" + tag + "</color>.");

                                            return Command.SINGLE_SUCCESS;
                                        })
                                        .build())
                                .build())
                        .build())
                .then(Commands.literal("disband")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            if (!(sender instanceof Player player)) {
                                sender.sendRichMessage("<red>Ta komenda może być używana tylko przez graczy.");
                                return Command.SINGLE_SUCCESS;
                            }

                            Optional<GamePlayer> optPlayer = theTowers.getPlayerManager().getPlayer(player.getName());
                            if (optPlayer.isEmpty()) {
                                sender.sendRichMessage("<red>Coś poszło kurwesko nie tak jak powinno.");
                                return Command.SINGLE_SUCCESS;
                            }
                            GamePlayer gamePlayer = optPlayer.get();

                            GameTeam team = gamePlayer.getTeam();
                            if (team == null) {
                                sender.sendRichMessage("<red>Nie jesteś w żadnej drużynie.");
                                return Command.SINGLE_SUCCESS;
                            }

                            if (!team.getLeader().equals(gamePlayer)) {
                                sender.sendRichMessage("<red>Tylko lider drużyny może ją rozwiązać.");
                                return Command.SINGLE_SUCCESS;
                            }

                            game.removeTeam(team);
                            game.addSpectator(gamePlayer);
                            sender.sendRichMessage("<green>Drużyna <color:#" + team.getColor().getColorHex() + ">" + team.getDisplayName() + "</color> została rozwiązana.");
                            team.getMembers().forEach(member -> {
                                member.setTeam(null);
                                DisguiseHandler.refresh(Bukkit.getPlayer(member.getName()));
                            });

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .then(Commands.literal("join")
                        .then(Commands.argument("tag", StringArgumentType.word())
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    if (!(sender instanceof Player player)) {
                                        sender.sendRichMessage("<red>Ta komenda może być używana tylko przez graczy.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    String tag = ctx.getArgument("tag", String.class);
                                    Optional<GameTeam> optTeam = game.getTeams().stream()
                                            .filter(team -> team.getTag().equalsIgnoreCase(tag))
                                            .findFirst();

                                    if (optTeam.isEmpty()) {
                                        sender.sendRichMessage("<red>Drużyna o tagu " + tag + " nie istnieje.");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    GameTeam team = optTeam.get();

                                    Optional<GamePlayer> optPlayer = theTowers.getPlayerManager().getPlayer(player.getName());
                                    if (optPlayer.isEmpty()) {
                                        sender.sendRichMessage("<red>Coś poszło kurwesko nie tak jak powinno.");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    GamePlayer gamePlayer = optPlayer.get();

                                    if (gamePlayer.getTeam() != null) {
                                        sender.sendRichMessage("<red>Już jesteś w drużynie.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (team.getMembers().size() >= game.getMaxPlayersPerTeam()) {
                                        sender.sendRichMessage("<red>Drużyna <color:#" + team.getColor().getColorHex() + ">" + team.getDisplayName() + "</color> jest pełna.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    team.addMember(gamePlayer);
                                    sender.sendRichMessage("<green>Dołączono do drużyny <color:#" + team.getColor().getColorHex() + ">" + team.getDisplayName() + "</color>.");

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build())
                .then(Commands.literal("leave")
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();

                            if (!(sender instanceof Player player)) {
                                sender.sendRichMessage("<red>Ta komenda może być używana tylko przez graczy.");
                                return Command.SINGLE_SUCCESS;
                            }

                            Optional<GamePlayer> optPlayer = theTowers.getPlayerManager().getPlayer(player.getName());
                            if (optPlayer.isEmpty()) {
                                sender.sendRichMessage("<red>Coś poszło kurwesko nie tak jak powinno.");
                                return Command.SINGLE_SUCCESS;
                            }
                            GamePlayer gamePlayer = optPlayer.get();

                            GameTeam team = gamePlayer.getTeam();
                            if (team == null) {
                                sender.sendRichMessage("<red>Nie jesteś w żadnej drużynie.");
                                return Command.SINGLE_SUCCESS;
                            }

                            if (team.getLeader().equals(gamePlayer)) {
                                sender.sendRichMessage("<red>Nie możesz opuścić drużyny jako jej lider.");
                                return Command.SINGLE_SUCCESS;
                            }

                            team.removeMember(gamePlayer);
                            gamePlayer.setTeam(null);
                            game.addSpectator(gamePlayer);
                            DisguiseHandler.refresh(player);

                            sender.sendRichMessage("<green>Opuszczono drużynę <color:#" + team.getColor().getColorHex() + ">" + team.getDisplayName() + "</color>.");

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .then(Commands.literal("remove")
                        .then(Commands.argument("member", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    if (sender instanceof Player player) {
                                        Optional<GamePlayer> optPlayer = theTowers.getPlayerManager().getPlayer(player.getName());
                                        if (optPlayer.isEmpty()) {
                                            return builder.buildFuture();
                                        }
                                        GamePlayer gamePlayer = optPlayer.get();

                                        GameTeam team = gamePlayer.getTeam();
                                        if (team == null) {
                                            return builder.buildFuture();
                                        }

                                        Set<String> members = team.getMembers().stream()
                                                .map(GamePlayer::getName)
                                                .collect(Collectors.toSet());
                                        for (String member : members) {
                                            builder.suggest(member);
                                        }
                                    }

                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    if (!(sender instanceof Player player)) {
                                        sender.sendRichMessage("<red>Ta komenda może być używana tylko przez graczy.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    Optional<GamePlayer> optPlayer = theTowers.getPlayerManager().getPlayer(player.getName());
                                    if (optPlayer.isEmpty()) {
                                        sender.sendRichMessage("<red>Coś poszło kurwesko nie tak jak powinno.");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    GamePlayer gamePlayer = optPlayer.get();

                                    GameTeam team = gamePlayer.getTeam();
                                    if (team == null) {
                                        sender.sendRichMessage("<red>Nie jesteś w żadnej drużynie.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!team.getLeader().equals(gamePlayer)) {
                                        sender.sendRichMessage("<red>Tylko lider drużyny może usuwać członków.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    String memberName = ctx.getArgument("member", String.class);
                                    Optional<GamePlayer> optMember = theTowers.getPlayerManager().getPlayer(memberName);
                                    if (optMember.isEmpty()) {
                                        sender.sendRichMessage("<red>Gracz " + memberName + " nie jest w twojej drużynie.");
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    GamePlayer member = optMember.get();

                                    if (!team.hasMember(member)) {
                                        sender.sendRichMessage("<red>Gracz " + memberName + " nie jest w twojej drużynie.");
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    team.removeMember(member);
                                    member.setTeam(null);
                                    game.addSpectator(member);
                                    DisguiseHandler.refresh(Bukkit.getPlayer(member.getName()));

                                    sender.sendRichMessage("<green>Usunięto gracza <color:#" + member.getTeam().getColor().getColorHex() + ">" + member.getName() + "</color> z drużyny <color:#" + team.getColor().getColorHex() + ">" + team.getDisplayName() + "</color>.");

                                    return Command.SINGLE_SUCCESS;
                                })
                                .build())
                        .build())
                .build();
    }

}
