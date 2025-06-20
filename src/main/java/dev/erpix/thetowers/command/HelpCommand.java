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
public class HelpCommand implements CommandBase {

    private static final String HELP_OUTPUT = String.join("<br>", new String[] {
            "Placeholder",
            "Placeholder",
            "Placeholder"
    });

    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("help")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    sender.sendRichMessage(HELP_OUTPUT);
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    @Override
    public @NotNull List<String> aliases() {
        return List.of("pomoc", "h", "?", "info");
    }

}
