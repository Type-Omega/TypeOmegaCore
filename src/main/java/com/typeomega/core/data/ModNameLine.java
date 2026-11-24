package com.typeomega.core.data;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Keeps the blue mod name line of a tooltip once. The multiblock tooltip ends with the mod name (GTIA-D-410) and EMI
 * appends the same name to every item tooltip, so the name showed up twice.
 */
public final class ModNameLine {

    private ModNameLine() {}

    /** Removes every line that only repeats {@code modName}, except the last one. */
    public static void keepOnce(List<Component> lines, String modName) {
        int last = -1;
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (!modName.equals(lines.get(i).getString())) continue;
            if (last < 0) {
                last = i;
            } else {
                lines.remove(i);
            }
        }
    }
}
