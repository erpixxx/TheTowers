package dev.erpix.thetowers.command;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Collection;

/**
 * Utility class for creating suggestion providers.
 */
@SuppressWarnings("UnstableApiUsage")
public final class SuggestionProviders {

    /**
     * Creates a suggestion provider from a collection of strings.
     *
     * @param suggestions the collection of suggestions to provide.
     * @return an instance of {@link SuggestionProvider}.
     */
    public static SuggestionProvider<CommandSourceStack> fromCollection(Collection<String> suggestions) {
        return (ctx, builder) -> {
            String[] parts = ctx.getInput().split(" ", -1);
            String lastPart = parts[parts.length - 1];

            for (String suggestion : suggestions) {
                // Check if the suggestion starts with the last part of the input
                // If the last part is empty, suggest all options
                if (!lastPart.isEmpty() && !suggestion.startsWith(lastPart)) {
                    continue;
                }
                builder.suggest(suggestion);
            }

            return builder.buildFuture();
        };
    }

}
