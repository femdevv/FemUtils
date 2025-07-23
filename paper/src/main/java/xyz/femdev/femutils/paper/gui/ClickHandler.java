package xyz.femdev.femutils.paper.gui;

/**
 * Functional interface for handling GUI clicks.
 */
@FunctionalInterface
public interface ClickHandler {
    void handle(ClickContext ctx);
}
