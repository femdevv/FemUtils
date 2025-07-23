package xyz.femdev.femutils.paper.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import xyz.femdev.femutils.java.core.Result;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a command node with arguments, children, execution logic, and tab-completion.
 */
public final class CommandNode {
    private final String name;
    private final String permission;
    private final List<Argument<?>> arguments;
    private final Consumer<CommandContext> executor;
    private final SuggestionProvider suggestions;
    private final List<CommandNode> children;
    private final boolean playerOnly;
    private final Component description;

    private CommandNode(Builder b) {
        this.name = b.name;
        this.permission = b.permission;
        this.arguments = List.copyOf(b.arguments);
        this.executor = b.executor;
        this.suggestions = b.suggestions;
        this.children = List.copyOf(b.children);
        this.playerOnly = b.playerOnly;
        this.description = b.description;
    }

    /**
     * Creates a new literal root command builder.
     */
    public static Builder literal(String name) {
        return new Builder(name);
    }

    public String name() {
        return name;
    }

    public List<CommandNode> children() {
        return children;
    }

    public List<Argument<?>> arguments() {
        return arguments;
    }

    public String permission() {
        return permission;
    }

    public boolean playerOnly() {
        return playerOnly;
    }

    public Optional<Component> description() {
        return Optional.ofNullable(description);
    }

    /**
     * Executes the command based on the sender and tokenized input.
     */
    public void execute(CommandSender sender, List<String> tokens) {
        if (permission != null && !sender.hasPermission(permission))
            throw new Exceptions.NoPermission(permission);
        if (playerOnly && !(sender instanceof org.bukkit.entity.Player))
            throw new Exceptions.WrongSender("Only players may run this command.");

        if (!tokens.isEmpty()) {
            String next = tokens.get(0);
            for (CommandNode child : children) {
                if (child.name.equalsIgnoreCase(next)) {
                    child.execute(sender, tokens.subList(1, tokens.size()));
                    return;
                }
            }
        }

        CommandContext ctx = new CommandContext(sender);
        int requiredCount = (int) arguments.stream().filter(a -> !a.optional()).count();
        if (tokens.size() < requiredCount) throw new Exceptions.ParseError("Not enough arguments.");

        Iterator<String> it = tokens.iterator();
        for (Argument<?> arg : arguments) {
            if (!it.hasNext()) {
                if (arg.optional()) break;
                throw new Exceptions.ParseError("Missing argument: " + arg.name());
            }
            String raw = it.next();
            parseStore((Argument<Object>) arg, raw, ctx);
        }

        if (executor == null) throw new Exceptions.ParseError("Nothing to execute.");
        executor.accept(ctx);
    }

    /**
     * Provides tab completions based on the current input.
     */
    public List<String> tabComplete(CommandSender sender, List<String> tokens) {
        if (permission != null && !sender.hasPermission(permission)) return List.of();

        if (tokens.isEmpty()) {
            return children.stream().map(c -> c.name).toList();
        }

        String current = tokens.get(0);
        if (tokens.size() == 1) {
            List<String> out = new ArrayList<>();
            for (CommandNode c : children)
                if (c.name.startsWith(current)) out.add(c.name);
            if (children.isEmpty() && !arguments.isEmpty()) {
                return suggestions.suggest(new CommandContext(sender), current);
            }
            return out;
        }

        for (CommandNode c : children) {
            if (c.name.equalsIgnoreCase(current)) {
                return c.tabComplete(sender, tokens.subList(1, tokens.size()));
            }
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static <T> void parseStore(Argument<T> arg, String raw, CommandContext ctx) {
        var res = arg.parser().parse(raw, ctx);
        if (res.isErr())
            throw new Exceptions.ParseError(((Result.Err<T, String>) res).error());
        T value = ((Result.Ok<T, String>) res).value();
        if (!arg.validator().test(value))
            throw new Exceptions.ParseError("Validation failed for arg: " + arg.name());
        ctx.put(arg.name(), value);
    }

    /**
     * Builder for {@link CommandNode}.
     */
    public static final class Builder {
        private final String name;
        private final List<Argument<?>> arguments = new ArrayList<>();
        private final List<CommandNode> children = new ArrayList<>();
        private String permission;
        private boolean playerOnly;
        private Consumer<CommandContext> executor;
        private SuggestionProvider suggestions = SuggestionProvider.empty();
        private Component description;

        private Builder(String name) {
            this.name = Objects.requireNonNull(name);
        }

        public Builder permission(String perm) {
            this.permission = perm;
            return this;
        }

        public Builder playerOnly() {
            this.playerOnly = true;
            return this;
        }

        public Builder arg(Argument<?> arg) {
            this.arguments.add(arg);
            return this;
        }

        public Builder exec(Consumer<CommandContext> exec) {
            this.executor = exec;
            return this;
        }

        public Builder suggest(SuggestionProvider s) {
            this.suggestions = s;
            return this;
        }

        public Builder desc(String miniMsg) {
            this.description = MiniMessage.miniMessage().deserialize(miniMsg);
            return this;
        }

        public Builder child(CommandNode node) {
            this.children.add(node);
            return this;
        }

        public CommandNode build() {
            return new CommandNode(this);
        }
    }
}
