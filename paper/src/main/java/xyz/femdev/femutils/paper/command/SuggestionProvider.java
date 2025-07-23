package xyz.femdev.femutils.paper.command;

import java.util.List;

/**
 * Provides tab completion suggestions for a command argument.
 */
@FunctionalInterface
public interface SuggestionProvider {

    /**
     * Returns an empty suggestion provider.
     */
    static SuggestionProvider empty() {
        return (c, t) -> List.of();
    }

    /**
     * Suggests completions for the given input token.
     *
     * @param ctx          the command context
     * @param currentToken the current input being typed
     * @return list of possible completions
     */
    List<String> suggest(CommandContext ctx, String currentToken);
}
