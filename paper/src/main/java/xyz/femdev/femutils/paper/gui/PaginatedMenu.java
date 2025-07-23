package xyz.femdev.femutils.paper.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

/**
 * A generic paginated GUI menu that supports navigation between multiple pages of items.
 *
 * @param <T> the type of data being rendered
 */
public class PaginatedMenu<T> extends Menu {

    private final List<T> data;
    private final int[] gridSlots;
    private final Function<T, Button> mapper;
    private int page = 0;

    private Button nextBtn;
    private Button prevBtn;
    private int nextSlot = -1, prevSlot = -1;

    public PaginatedMenu(Component title, int rows, List<T> data, int[] gridSlots, Function<T, Button> mapper) {
        super(title, rows);
        this.data = data;
        this.gridSlots = gridSlots;
        this.mapper = mapper;
    }

    /**
     * Sets navigation buttons and their slots.
     */
    public PaginatedMenu<T> navigation(int prevSlot, Button prev, int nextSlot, Button next) {
        this.prevSlot = prevSlot;
        this.prevBtn = prev;
        this.nextSlot = nextSlot;
        this.nextBtn = next;
        return this;
    }

    @Override
    protected void build(Player player) {
        drawPage();
    }

    private void drawPage() {
        clearButtons();
        int start = page * gridSlots.length;
        int end = Math.min(start + gridSlots.length, data.size());
        for (int i = start, gi = 0; i < end; i++, gi++) {
            setButton(gridSlots[gi], mapper.apply(data.get(i)));
        }

        if (prevBtn != null) {
            setButton(prevSlot, Button.of(prevBtn.item(), ctx -> {
                if (page > 0) {
                    page--;
                    drawPage();
                    redraw();
                }
            }));
        }

        if (nextBtn != null) {
            setButton(nextSlot, Button.of(nextBtn.item(), ctx -> {
                if ((page + 1) * gridSlots.length < data.size()) {
                    page++;
                    drawPage();
                    redraw();
                }
            }));
        }
    }
}
