package dev.erpix.thetowers.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for creating and manipulating text components.
 */
public final class Components {

    private Components() { }

    /**
     * The standard {@link MiniMessage} instance with all tags support.
     */
    private static final MiniMessage STANDARD = MiniMessage.builder().build();
    /**
     * The {@link MiniMessage} instance with color tags support.
     */
    private static final MiniMessage COLOR = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolvers(StandardTags.color())
                    .build())
            .build();
    /**
     * The {@link MiniMessage} instance with color and decoration tags support.
     */
    private static final MiniMessage COLOR_WITH_DECO = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolvers(StandardTags.color(), StandardTags.decorations())
                    .build())
            .build();
    /**
     * The {@link MiniMessage} instance with color, decoration, and gradient tags support.
     */
    private static final MiniMessage GRADIENT = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolvers(StandardTags.color(), StandardTags.decorations(), StandardTags.gradient())
                    .build())
            .build();

    /**
     * <p>Deserializes a standard text input into a {@link Component} with all tags support.</p>
     *
     * @param input The input text to be deserialized.
     * @return A {@link Component} of the input text.
     */
    public static @NotNull Component standard(@NotNull String input) {
        return STANDARD.deserialize(input);
    }

    /**
     * Deserializes a text input string into a {@link Component}, supporting default color tags.
     *
     * @param input The input text to be deserialized.
     * @return A {@link Component} of the input text.
     */
    public static @NotNull Component color(@NotNull String input) {
        return COLOR.deserialize(input);
    }

    /**
     * Deserializes a text input string into a {@link Component}, supporting default color and decoration tags.
     *
     * @param input The input text to be deserialized.
     * @return A {@link Component} of the input text.
     */
    public static @NotNull Component colorWithDeco(@NotNull String input) {
        return COLOR_WITH_DECO.deserialize(input);
    }

    /**
     * Deserializes a text input string into a {@link Component}, supporting color, decoration, and gradient tags.
     *
     * @param input The input text to be deserialized.
     * @return A {@link Component} of the input text.
     */
    public static @NotNull Component gradient(@NotNull String input) {
        return GRADIENT.deserialize(input);
    }

    /**
     * Serializes a {@link Component} back into a plain text string.
     *
     * @param input The {@link Component} to be serialized into a string.
     * @return The plain text.
     */
    public static @NotNull String serialize(@NotNull Component input) {
        return STANDARD.serialize(input);
    }

}
