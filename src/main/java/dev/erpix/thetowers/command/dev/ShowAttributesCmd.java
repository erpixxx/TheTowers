package dev.erpix.thetowers.command.dev;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.command.CommandBase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("UnstableApiUsage")
public class ShowAttributesCmd implements CommandBase {

    @Override
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("showattributes")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    if (!(sender instanceof Player player)) {
                        return 1;
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    meta.getAttributeModifiers().forEach((att, mod) -> {
                        String attributeName = att.getKey().getKey();
                        double value = mod.getAmount();
                        player.sendMessage("Attribute: " + attributeName + ", Value: " + value);
                    });

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

}
