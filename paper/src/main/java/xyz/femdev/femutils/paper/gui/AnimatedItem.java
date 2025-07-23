package xyz.femdev.femutils.paper.gui;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents an item that cycles through multiple frames for animation in a GUI.
 */
public final class AnimatedItem {
    private final List<ItemStack> frames;
    private final int ticksPerFrame;
    private int tick;
    private int index;

    public AnimatedItem(List<ItemStack> frames, int ticksPerFrame) {
        if (frames == null || frames.isEmpty()) throw new IllegalArgumentException("frames empty");
        if (ticksPerFrame < 1) throw new IllegalArgumentException("ticksPerFrame < 1");
        this.frames = List.copyOf(frames);
        this.ticksPerFrame = ticksPerFrame;
    }

    /**
     * Advances the animation by one tick.
     *
     * @return true if the frame changed, false otherwise
     */
    public boolean advance() {
        tick++;
        if (tick >= ticksPerFrame) {
            tick = 0;
            index = (index + 1) % frames.size();
            return true;
        }
        return false;
    }

    /**
     * Returns the current frame's item.
     */
    public ItemStack current() {
        return frames.get(index).clone();
    }
}
