package dev.erpix.thetowers.util;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.model.game.GamePlayer;
import dev.erpix.thetowers.model.game.GameTeam;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Utility class for managing player disguises.
 */
public final class DisguiseHandler {

    private DisguiseHandler() { }

    /**
     * Creates a disguise for the player that shows their team and health.
     *
     * @param player The player to create a disguise for.
     */
    public static void create(@NotNull Player player) {
        PlayerDisguise disguise = new PlayerDisguise(player, player);
        disguise.setMultiName(getNameForPlayer(player));
        disguise.setEntity(player);
        disguise.setSkin(player.getName());
        disguise.setSelfDisguiseVisible(false);
        disguise.setNotifyBar(null);
        disguise.startDisguise();
    }

    /**
     * Refreshes the player's disguise with a specific health value.
     *
     * @param player The player whose disguise should be refreshed.
     * @param hp The health value to display.
     */
    public static void refresh(@NotNull Player player, double hp) {
        Disguise disguise = DisguiseAPI.getDisguise(player);
        if (disguise == null) return;
        disguise.setMultiName(getNameForPlayer(player, hp));
        disguise.startDisguise();
    }

    /**
     * Refreshes the player's disguise with their current health.
     *
     * @param player The player whose disguise should be refreshed.
     */
    public static void refresh(@NotNull Player player) {
        refresh(player, player.getHealth());
    }

    /**
     * Toggles the visibility of the player's name tag.
     *
     * <p>Works only for {@link PlayerDisguise} instances.</p>
     *
     * @param player The player whose name tag should be toggled.
     */
    public static void toggleName(@NotNull Player player) {
        if (!(DisguiseAPI.getDisguise(player) instanceof PlayerDisguise disguise)) {
            return;
        }
        if (disguise.getMultiNameLength() > 0) {
            disguise.setMultiName("");
        } else {
            disguise.setMultiName(getNameForPlayer(player));
        }
        disguise.startDisguise();
    }

    private static @NotNull String[] getNameForPlayer(@NotNull Player player) {
        return getNameForPlayer(player, player.getHealth());
    }

    private static @NotNull String[] getNameForPlayer(@NotNull Player player, double hp) {
        hp = MathUtil.round(hp, 1);
        if (hp < 0 || player.isDead()) {
            return new String[] { };
        }

        String name = player.getName();
        Optional<GamePlayer> optPlayer = TheTowers.getInstance().getPlayerManager().getPlayer(name);
        if (optPlayer.isEmpty()) {
            return new String[] { };
        }
        GamePlayer gamePlayer = optPlayer.get();
        GameTeam team = gamePlayer.getTeam();
        if (team == null) {
            return new String[] { gamePlayer.getDisplayNameNoTag() };
        }

        String line1 = String.format("<#%s>[%s]", team.getColor().getColorHex(), team.getTag());
        String line2 = String.format("%s", gamePlayer.getDisplayNameNoTag());
        String line3 = String.format("<red>%s <dark_red>❤", hp);

        return new String[] { line1, line2, line3 };
    }

}
