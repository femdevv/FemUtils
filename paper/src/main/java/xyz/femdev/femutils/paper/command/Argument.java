package xyz.femdev.femutils.paper.command;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a typed command argument with parsing and validation.
 *
 * @param <T> the parsed value type
 */
public final class Argument<T> {

    private final String name;
    private final ArgParser<T> parser;
    private final boolean optional;
    private final Predicate<T> validator;

    private Argument(String name,
                     ArgParser<T> parser,
                     boolean optional,
                     Predicate<T> validator) {
        this.name = name;
        this.parser = parser;
        this.optional = optional;
        this.validator = validator;
    }

    /**
     * Starts building a new argument.
     */
    public static <T> Builder<T> of(String name, ArgParser<T> parser) {
        return new Builder<>(name, parser);
    }

    public String name() {
        return name;
    }

    public ArgParser<T> parser() {
        return parser;
    }

    public boolean optional() {
        return optional;
    }

    public Predicate<T> validator() {
        return validator;
    }

    /**
     * Builder for {@link Argument}.
     */
    public static final class Builder<T> {
        private final String name;
        private final ArgParser<T> parser;
        private boolean optional;
        private Predicate<T> validator = t -> true;

        private Builder(String name, ArgParser<T> parser) {
            this.name = Objects.requireNonNull(name, "name");
            this.parser = Objects.requireNonNull(parser, "parser");
        }

        /**
         * Marks the argument as optional.
         */
        public Builder<T> optional() {
            this.optional = true;
            return this;
        }

        /**
         * Adds a validator that must return true for valid values.
         */
        public Builder<T> validate(Predicate<? super T> p) {
            Objects.requireNonNull(p, "validator");
            Predicate<T> prev = this.validator;
            this.validator = t -> prev.test(t) && p.test(t);
            return this;
        }

        /**
         * Builds the argument.
         */
        public Argument<T> build() {
            return new Argument<>(name, parser, optional, validator);
        }
    }
}
