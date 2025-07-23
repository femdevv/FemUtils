package xyz.femdev.femutils.paper.command;

/**
 * Exception types used during command parsing and execution.
 */
public final class Exceptions {
    public static class NoPermission extends RuntimeException {
        public NoPermission(String perm) {
            super("Missing permission: " + perm);
        }
    }

    public static class WrongSender extends RuntimeException {
        public WrongSender(String msg) {
            super(msg);
        }
    }

    public static class ParseError extends RuntimeException {
        public ParseError(String msg) {
            super(msg);
        }
    }
}
