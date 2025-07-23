package xyz.femdev.femutils.paper.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builder-style utility for creating menus.
 */
public final class MenuBuilder {

    private final Map<Integer, Button> buttons = new HashMap<>();
    private Component title = Component.text("Menu");
    private int rows = 3;
    private Consumer<Player> onClose = p -> {
    };
    private BiConsumer<Player, Menu> dynamicBuilder = (p, m) -> {
    };

    private MenuBuilder() {
    }

    public static MenuBuilder gui() {
        return new MenuBuilder();
    }

    public MenuBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public MenuBuilder rows(int rows) {
        this.rows = rows;
        return this;
    }

    public MenuBuilder button(int slot, Button button) {
        buttons.put(slot, button);
        return this;
    }

    public MenuBuilder animatedButton(int slot, AnimatedItem anim, ClickHandler handler) {
        onBuild((player, m) -> {
            m.animate(slot, anim);
            if (handler != null) m.setButton(slot, Button.of(anim.current(), handler));
        });
        return this;
    }

    public MenuBuilder onBuild(BiConsumer<Player, Menu> bi) {
        dynamicBuilder = dynamicBuilder.andThen(bi);
        return this;
    }

    public MenuBuilder onClose(Consumer<Player> c) {
        onClose = onClose.andThen(c);
        return this;
    }

    /**
     * Builds the final Menu instance.
     */
    public Menu build() {
        return new Menu(title, rows) {
            @Override
            protected void build(Player player) {
                this.buttons.putAll(MenuBuilder.this.buttons);
                dynamicBuilder.accept(player, this);
            }

            @Override
            protected void onClose(Player player) {
                onClose.accept(player);
            }
        };
    }
}
