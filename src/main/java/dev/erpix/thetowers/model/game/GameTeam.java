package dev.erpix.thetowers.model.game;

import dev.erpix.thetowers.TheTowers;
import dev.erpix.thetowers.util.DisguiseHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Represents a team in the game.
 */
public class GameTeam {

    public static final int TEAM_MAX_NAME_LENGTH = 5;
    public static final int TEAM_MIN_NAME_LENGTH = 2;

    private final Map<String, GamePlayer> members = new LinkedHashMap<>();
    @NonNull @Getter
    private String name;
    @NonNull @Getter @Setter
    private GamePlayer leader;
    @NonNull @Getter @Setter
    private Color color;
    @Getter @Setter
    private int heartHealth;
    @Getter @Setter
    private int souls;

    /**
     * Creates a new team.
     *
     * @param leader the leader of the team, must not be null.
     * @param name the name of the team, must be valid.
     * @param color the color of the team, must not be null.
     * @throws IllegalArgumentException if the name is invalid.
     * @see #isValidName(String)
     */
    public GameTeam(@NotNull GamePlayer leader, @NotNull String name, @NotNull Color color) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid team name: " + name);
        }
        this.leader = leader;
        this.name = name;
        this.color = color;
        this.heartHealth = 100; // can be adjusted later.
        addMember(leader);
    }

    /**
     * Validates if the provided team name is valid.
     *
     * @param name the name to validate.
     * @return true if the name is valid, false otherwise.
     */
    public static boolean isValidName(@NotNull String name) {
        return name.length() >= TEAM_MIN_NAME_LENGTH && name.length() <= TEAM_MAX_NAME_LENGTH
                && name.chars().allMatch(Character::isLetterOrDigit);
    }

    /**
     * Sets the name of the team.
     *
     * @param name the new name for the team.
     * @throws IllegalArgumentException if the name is invalid.
     * @see #isValidName(String)
     */
    public void setName(@NonNull String name) {
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid team name: " + name);
        }
        TheTowers.getInstance().getGameManager().updateTeamName(this, name);
        this.name = name;
        for (GamePlayer member : members.values()) {
            member.getBukkitPlayer().ifPresent(DisguiseHandler::refresh);
        }
    }

    /**
     * Retrieves a member of the team by their name.
     *
     * @param name the name of the player to retrieve.
     * @return the {@link GamePlayer} if found, otherwise null.
     */
    public @Nullable GamePlayer getMember(@NotNull String name) {
        return members.get(name);
    }

    /**
     * Retrieves all members of the team.
     *
     * @return an unmodifiable collection of all team members.
     */
    public @NotNull @Unmodifiable Collection<GamePlayer> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    /**
     * Adds a player to the team and updates their status to a team member.
     *
     * @param player the player to add to the team.
     */
    public void addMember(@NotNull GamePlayer player) {
        members.put(player.getName(), player);
        player.setTeam(this);
        player.getBukkitPlayer().ifPresent(DisguiseHandler::refresh);

        TheTowers theTowers = TheTowers.getInstance();
        theTowers.getGameManager().removeSpectator(player);
        theTowers.getTabManager().updateLayout(player.getName());
    }

    /**
     * Checks if the team has a specific player as a member.
     *
     * @param player the player to check.
     * @return true if the player is a member of the team, false otherwise.
     */
    public boolean hasMember(@NotNull GamePlayer player) {
        return members.containsKey(player.getName());
    }

    /**
     * Removes a player from the team and updates their status to spectator.
     *
     * @param player the player to remove from the team.
     */
    public void removeMember(@NotNull GamePlayer player) {
        members.remove(player.getName());
        player.setTeam(null);
        player.getBukkitPlayer().ifPresent(DisguiseHandler::refresh);

        TheTowers theTowers = TheTowers.getInstance();
        theTowers.getGameManager().addSpectator(player);
        theTowers.getTabManager().updateLayout(player.getName());
    }

    /**
     * Retrieves the display name of the team, formatted with color and tag.
     *
     * @return the formatted display name of the team.
     */
    public @NotNull String getDisplayName() {
        return String.format("<color:#%s>[%s]</color>", color.getColorHex(), name);
    }

    /**
     * Damages the heart by 1.
     *
     * @return the remaining heart health after damage.
     */
    public int damageHeart() {
        return damageHeart(1);
    }

    /**
     * Damages the heart by a specified amount.
     *
     * @param amount the amount of damage to apply to the heart.
     * @return the remaining heart health after damage.
     */
    public int damageHeart(int amount) {
        if (heartHealth > 0) {
            heartHealth -= amount;
        }
        return heartHealth;
    }

    /**
     * Checks if the team is still alive based on heart health and member status.
     *
     * @return true if the team is alive, false otherwise.
     */
    public boolean isAlive() {
        return heartHealth > 0 && members.values().stream()
                .noneMatch(player -> !player.isAlive() || !player.isOnline());
    }

    /**
     * Adds souls to the team's total.
     *
     * @param value the amount of souls to add.
     */
    public void addSouls(@Range(from = 0, to = Integer.MAX_VALUE) int value) {
        souls += value;
    }

    /**
     * Removes souls from the team's total, ensuring it does not go below zero.
     *
     * @param value the amount of souls to remove.
     */
    public void removeSouls(@Range(from = 0, to = Integer.MAX_VALUE) int value) {
        souls = Math.max(0, souls - value);
    }

    /**
     * Represents the color of a team.
     */
    @Getter
    public enum Color {
        RED("red", "CC3933", "FE6C67"),
        BLUE("blue", "0094FF", "66BFFF"),
        GREEN("green", "4FCC33", "95E085"),
        YELLOW("yellow", "D5CC59", "E0D985"),
        ORANGE("orange", "E56F19", "F0A875"),
        PURPLE("purple", "C126D9", "E093EC");

        @NotNull private final String name;
        @NotNull private final String colorHex;
        @NotNull private final String secondaryColorHex;

        Color(@NotNull String name, @NotNull String colorHex, @NotNull String secondaryColorHex) {
            this.name = name;
            this.colorHex = colorHex;
            this.secondaryColorHex = secondaryColorHex;
        }

        /**
         * Returns a {@link Color} enum instance based on the provided name.
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
        GameTeam gameTeam = (GameTeam) o;
        return Objects.equals(members, gameTeam.members) && Objects.equals(leader, gameTeam.leader) && Objects.equals(name, gameTeam.name) && color == gameTeam.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(members, leader, name, color);
    }

    @Override
    public String toString() {
        return "TTeam{" +
                "color=" + color +
                ", tag='" + name + '\'' +
                ", leader=" + leader +
                ", members=" + members +
                '}';
    }

}
