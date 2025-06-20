package dev.erpix.thetowers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Base interface for commands.
 */
@SuppressWarnings("UnstableApiUsage")
public interface CommandBase {

    /**
     * Creates a command node for the command.
     *
     * @return the command node.
     */
    @NotNull LiteralCommandNode<CommandSourceStack> create();

    /**
     * Aliases for the command.
     *
     * @return a list of aliases.
     */
    default @NotNull List<String> aliases() {
        return Collections.emptyList();
    }

}
