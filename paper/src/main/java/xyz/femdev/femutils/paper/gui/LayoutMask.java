package xyz.femdev.femutils.paper.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * A character-based layout mask for defining slot groups in GUI rows.
 * Each row must be exactly 9 characters (like an inventory row).
 */
public final class LayoutMask {

    private final List<char[]> rows;

    private LayoutMask(List<char[]> rows) {
        this.rows = rows;
    }

    /**
     * Creates a layout mask from row strings.
     *
     * @param lines must be 9 characters each
     */
    public static LayoutMask of(String... lines) {
        List<char[]> list = new ArrayList<>();
        int width = 9;
        for (String line : lines) {
            if (line.length() != width)
                throw new IllegalArgumentException("Each layout line must be width 9");
            list.add(line.toCharArray());
        }
        return new LayoutMask(list);
    }

    /**
     * Returns the slot indices matching the given character.
     */
    public int[] slots(char c) {
        List<Integer> out = new ArrayList<>();
        for (int r = 0; r < rows.size(); r++) {
            char[] arr = rows.get(r);
            for (int col = 0; col < arr.length; col++) {
                if (arr[col] == c) out.add(r * 9 + col);
            }
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }
}
