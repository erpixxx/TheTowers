package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.TTPlayerProfile;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class ProfileCommand implements CommandBase {

    @Override
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("profile")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendRichMessage("<red>/profil <gracz>");
                        return Command.SINGLE_SUCCESS;
                    }
                    return profile(sender, player.getName());
                })
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(ctx -> profile(ctx.getSource().getSender(), ctx.getArgument("player", String.class)))
                        .build())
                .build();
    }

    private int profile(CommandSender sender, String playerName) {
        Optional<TTPlayerProfile> profile = TheTowers.getInstance().getProfileManager().getProfile(playerName);

        if (profile.isEmpty()) {
            sender.sendRichMessage("<red>Nie można znaleźć profilu gracza");
            return 0;
        }

        profile.get().getStats().forEach(stat -> {
            sender.sendRichMessage(stat.getKey() + ": " + stat.getValue());
        });

        return Command.SINGLE_SUCCESS;
    }

    @Override
    public List<String> aliases() {
        return List.of("profil");
    }

}
