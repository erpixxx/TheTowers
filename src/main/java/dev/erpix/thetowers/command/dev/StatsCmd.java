package dev.erpix.thetowers.command.dev;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.command.CommandBase;
import dev.erpix.thetowers.model.GameStatKey;
import dev.erpix.thetowers.model.StatsTracker;
import dev.erpix.thetowers.util.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class StatsCmd implements CommandBase {

    private final TheTowers theTowers = TheTowers.getInstance();

    @Override
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("stats")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    if (!(sender instanceof Player player)) {
                        return 0;
                    }

                    theTowers.getPlayerManager().getPlayer(player.getName()).ifPresent(p -> {
                        StatsTracker<GameStatKey> stats = p.getStats();
                        p.sendMessage(Components.standard("<gray>Stats:"));
                        p.sendMessage(Components.standard("<gray>- Kills: " + stats.getStat(GameStatKey.KILLS)));
                        p.sendMessage(Components.standard("<gray>- Deaths: " + stats.getStat(GameStatKey.DEATHS)));
                        p.sendMessage(Components.standard("<gray>- Assists: " + stats.getStat(GameStatKey.ASSISTS)));
                    });

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}
