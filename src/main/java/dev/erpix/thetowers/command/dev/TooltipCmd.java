package dev.erpix.thetowers.command.dev;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.command.CommandBase;
import dev.erpix.thetowers.util.Components;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("UnstableApiUsage")
public class TooltipCmd implements CommandBase {

    @Override
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("tooltip")
                .requires(source -> source.getSender().isOp())
                .then(Commands.argument("name", StringArgumentType.greedyString())

                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            if (!(sender instanceof Player player)) return 1;

                            String name = StringArgumentType.getString(ctx, "name");

                            ItemStack item = player.getInventory().getItemInMainHand();
                            ItemMeta meta = item.getItemMeta();
                            meta.setTooltipStyle(NamespacedKey.fromString("thetowers:tooltip"));
                            item.setItemMeta(meta);

                            return Command.SINGLE_SUCCESS;
                        })
                        .build())
                .build();
    }

}
