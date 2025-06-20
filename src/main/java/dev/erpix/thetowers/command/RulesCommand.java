package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.util.Colors;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class RulesCommand implements CommandBase {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("rules")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    sender.sendRichMessage(String.format(
                            "<br><gradient:#%s:#%s><b>Regulamin gry w The Towers</b></gradient><gray><br>" +
                            "1. <white>Korzystanie z wszelkich wspomagaczy takich jak cheaty, makra, czy inne programy jest zabronione.</white><br>" +
                            "2. <white>Nazwy drużyn nie mogą być obraźliwe (lub powszechnie uważane za obraźliwe). Mogą natomiast zawierać wulgarne słowa.</white><br>" +
                            "3. <white>Serwer był robiony 4fun, więc wszystkie błędy powinny być natychmiastowo zgłaszane, aby można było je jak najszybciej naprawić.</white><br>" +
                            "<color:#%s>Ogólnie to mam nadzieje nie musze się rozpisywać o zasadach, bo każdy wie, że nie można oszukiwać i trzeba grać fair, oraz że trzeba zachować tzw. 'common sense'. Także miłej zabawy :D</color><gray><br>",
                            Colors.PRIMARY, Colors.SECONDARY, Colors.SECONDARY));

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    @Override
    public @NotNull List<String> aliases() {
        return List.of("regulamin");
    }
}
