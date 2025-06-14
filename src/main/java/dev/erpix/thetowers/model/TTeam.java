package dev.erpix.thetowers.model;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.util.Disguises;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Represents a team in the game.
 */
public class TTeam {

    private final TheTowers theTowers = TheTowers.getInstance();

    private final Map<String, TPlayer> members = new LinkedHashMap<>();
    private String tag;
    private TPlayer leader;
    private Color color;
    private int heartHealth;
    private int souls;

    public TTeam(@NotNull TPlayer leader, @NotNull String tag, @NotNull Color color) {
        this.leader = leader;
        this.tag = tag;
        this.color = color;
        this.heartHealth = 100;
        addMember(leader);
    }

    /**
     * Returns a collection of all team members.
     *
     * @return Collection of all team members.
     */
    public @NotNull @Unmodifiable Collection<TPlayer> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    /**
     * Adds a player to the team and updates their status to a team member.
     *
     * @param player The {@link TPlayer} to be added to the team.
     */
    public void addMember(@NotNull TPlayer player) {
        members.put(player.getName(), player);
        player.setTeam(this);
        player.getBukkitPlayer().ifPresent(Disguises::refresh);
        theTowers.getGame().removeSpectator(player);
        theTowers.getTabManager().updateLayout(player.getName());
    }

    /**
     * Checks if a player is a member of the team.
     *
     * @param player The {@link TPlayer} to check.
     * @return true if the player is a member of the team, false otherwise.
     */
    public boolean hasMember(@NotNull TPlayer player) {
        return members.containsKey(player.getName());
    }

    /**
     * Removes a player from the team and updates their status to spectator.
     *
     * @param player The {@link TPlayer} to remove from the team.
     */
    public void removeMember(@NotNull TPlayer player) {
        members.remove(player.getName());
        player.setTeam(null);
        player.getBukkitPlayer().ifPresent(Disguises::refresh);
        theTowers.getGame().addSpectator(player);
        theTowers.getTabManager().updateLayout(player.getName());
    }

    /**
     * Gets the leader of the team.
     *
     * @return The leader of the team.
     */
    public @NotNull TPlayer getLeader() {
        return leader;
    }

    /**
     * Gets the display name of the team, formatted with color and tag.
     *
     * @return The formatted display name of the team.
     */
    public @NotNull String getDisplayName() {
        return String.format("<color:#%s>[%s]</color>", color.getColorHex(), tag);
    }

    /**
     * Gets the tag of the team.
     *
     * @return The tag of the team.
     */
    public @NotNull String getTag() {
        return tag;
    }

    /**
     * Gets the color of the team.
     *
     * @return The color of the team.
     */
    public @NotNull Color getColor() {
        return color;
    }

    /**
     * Sets the color of the team.
     *
     * @param color The new color of the team.
     */
    public void setColor(@NotNull Color color) {
        this.color = color;
    }

    /**
     * Damages the heart health of the team by 1.
     *
     * @return The new heart health value after applying the damage.
     */
    public int damageHeart() {
        return damageHeart(1);
    }

    /**
     * Damages the heart health of the team by a specified amount.
     *
     * @param amount The amount of damage to apply to the heart health.
     * @return The new heart health value after applying the damage.
     */
    public int damageHeart(int amount) {
        if (heartHealth > 0) {
            heartHealth -= amount;
        }
        return heartHealth;
    }

    /**
     * Gets the current heart health of the team.
     *
     * @return The current heart health value.
     */
    public int getHeartHealth() {
        return heartHealth;
    }

    /**
     * Sets the heart health of the team.
     *
     * @param heartHealth The new heart health value.
     */
    public void setHeartHealth(int heartHealth) {
        this.heartHealth = heartHealth;
    }

    /**
     * Checks if the team is currently alive.
     *
     * @return true if the team is alive, false otherwise.
     */
    public boolean isAlive() {
        return heartHealth > 0;
    }

    /**
     * Adds souls to the team's total.
     *
     * @param value The number of souls to add.
     */
    public void addSouls(@Range(from = 0, to = Integer.MAX_VALUE) int value) {
        souls += value;
    }

    /**
     * Removes souls from the team's total, ensuring it does not go below zero.
     *
     * @param value The number of souls to remove.
     */
    public void removeSoul(@Range(from = 0, to = Integer.MAX_VALUE) int value) {
        souls = Math.max(0, souls - value);
    }

    /**
     * Gets the number of souls collected by the team.
     *
     * @return The number of souls.
     */
    public int getSouls() {
        return souls;
    }

    /**
     * Sets the number of souls collected by the team.
     *
     * @param souls The new number of souls.
     */
    public void setSouls(int souls) {
        this.souls = souls;
    }

    /**
     * Represents the color of a team.
     */
    public enum Color {
        RED("red", "CC3933", "FE6C67"),
        BLUE("blue", "0094FF", "66BFFF"),
        GREEN("green", "4FCC33", "95E085"),
        YELLOW("yellow", "D5CC59", "E0D985"),
        ORANGE("orange", "E56F19", "F0A875"),
        PURPLE("purple", "C126D9", "E093EC");

        private final String name;
        private final String colorHex;
        private final String secondaryColorHex;

        Color(String name, String colorHex, String secondaryColorHex) {
            this.name = name;
            this.colorHex = colorHex;
            this.secondaryColorHex = secondaryColorHex;
        }

        /**
         * Gets the name of the color.
         *
         * @return The name of the color.
         */
        public @NotNull String getName() {
            return name;
        }

        /**
         * Gets the primary color hex code as a string.
         *
         * @return The hex code of the primary color.
         */
        public @NotNull String getColorHex() {
            return colorHex;
        }

        /**
         * Gets the secondary color hex code as a string.
         *
         * @return The hex code of the secondary color.
         */
        public @NotNull String getSecondaryColorHex() {
            return secondaryColorHex;
        }

        /**
         * Returns a {@link Color} enum instance based on the provided name.
         *
         * @param name The name of the color to find.
         * @return The Color enum instance if found, otherwise null.
         */
        public static @Nullable Color from(String name) {
            for (Color color : values()) {
                if (color.name.equalsIgnoreCase(name)) {
                    return color;
                }
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TTeam tTeam = (TTeam) o;
        return Objects.equals(members, tTeam.members) && Objects.equals(leader, tTeam.leader) && Objects.equals(tag, tTeam.tag) && color == tTeam.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(members, leader, tag, color);
    }

    @Override
    public String toString() {
        return "TTeam{" +
                "color=" + color +
                ", tag='" + tag + '\'' +
                ", leader=" + leader +
                ", members=" + members +
                '}';
    }

}
