package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.config.i18n.Messages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HelpCommand implements CommandBase {

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("help")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    for (var line : Messages.HELP_COMMAND) {
                        sender.sendRichMessage(line.get());
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    @Override
    public @NotNull List<String> aliases() {
        return List.of("pomoc", "h", "?", "info");
    }

}
