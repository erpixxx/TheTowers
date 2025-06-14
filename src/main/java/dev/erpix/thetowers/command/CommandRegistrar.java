package dev.erpix.thetowers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class CommandRegistrar {

    private final LifecycleEventManager<?> lifecycle;

    public CommandRegistrar(LifecycleEventManager<?> lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void register(@NotNull CommandBase cmd) {
        LiteralCommandNode<CommandSourceStack> node = cmd.create();
        lifecycle.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(node, cmd.aliases());
        });
    }

    public void registerAll(@NotNull CommandBase... cmds) {
        for (@NotNull CommandBase cmd : cmds) {
            register(cmd);
        }
    }

}
