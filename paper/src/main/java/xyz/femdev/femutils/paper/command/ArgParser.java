package xyz.femdev.femutils.paper.command;

import xyz.femdev.femutils.java.core.Result;

/**
 * Parses a raw string argument into a typed value for a command.
 *
 * @param <T> the target type
 */
@FunctionalInterface
public interface ArgParser<T> {

    /**
     * Parses input into a typed value.
     *
     * @param input the raw string
     * @param ctx   the command context
     * @return a {@link Result} containing the parsed value or an error
     */
    Result<T, String> parse(String input, CommandContext ctx);
}
