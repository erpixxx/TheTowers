package dev.erpix.thetowers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

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
    LiteralCommandNode<CommandSourceStack> create();

    /**
     * Aliases for the command.
     *
     * @return a list of aliases.
     */
    default List<String> aliases() {
        return Collections.emptyList();
    }

}
