package dev.erpix.thetowers.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.erpix.thetowers.util.Colors;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class HelpCommand implements CommandBase {

    private static final String[] HELP_OUTPUT = {
            "",
            "<gradient:#" + Colors.PRIMARY + ":#" + Colors.SECONDARY + "><b>     Szybki poradnik gry w The Towers</b></gradient>",
            "",
            "<gray>Celem gry jest zniszczenie <white>serc</white> wszystkich wrogich wież. Każda wieża skrywa swoje serce wewnątrz, a jego domyślne HP wynosi <white>100</white>. Aby je uszkodzić, należy użyć <white>⛏ kilofa</white> — im lepszy i szybszy kilof, tym szybciej serce zostanie zniszczone. Gdy drużyna straci swoje serce, traci wtedy <white>możliwość respawnu</white>.",
            "",
            "Każda drużyna zaczyna z własną <color:#d93853>❤ wieżą</color> oraz <color:#308bd1>⛏ kopalnią</color>, w której wydobyć można minerały potrzebne do ulepszania ekwipunku: zbroi, miecza i narzędzi. W pobliżu znajduje się również <color:#60d130>🪓 tartak</color> będący źródłem drewna, a także <color:#d1c630>✂ farma</color> oraz miejsce do połowu <color:#30b6d1>🎣 ryb</color>, które zapewniają jedzenie.",
            "",
            "Do urozmaicenia gry w <white>centrum mapy</white> znajduje się ulepszona kopalnia zawierająca rzadkie surowce takie jak <white>netherite</white> i <white>kwarc</white>, które umożliwiają tworzenie potężniejszego ekwipunku. Dodatkowo, co pewien czas na środek mapy spada <gold>supply crate</gold>, specjalna skrzynka z unikalnymi przedmiotami.",
            "",
            "Oprócz tego na mapie rozrzucone są również małe <white>dungeony</white>, w których można znaleźć wartościowy loot, dający drużynie przewagę w walce.",
            "",
    };

    @Override
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("help")
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    String output = String.join("<br>", HELP_OUTPUT);
                    sender.sendRichMessage(output);

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    @Override
    public List<String> aliases() {
        return List.of("pomoc", "h", "?", "info");
    }

}
