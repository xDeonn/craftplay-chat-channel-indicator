// File: src/main/java/com/craftplaychatindicator/PlotUtils.java

package com.craftplaychatindicator;

public class PlotUtils {
    /**
     * Determines if the given coordinates are within a plot or on a border.
     * Each plot is 200x200 with a 4-block border around each plot
     * (resulting in 8 blocks between adjacent plots).
     *
     * @param x The x-coordinate
     * @param z The z-coordinate
     * @return The plot coordinates as a string (e.g. "1;1") if within a plot,
     *         or "0;0" if on a border
     */
    public static String getPlotCoordinates(int x, int z) {
        // Size constants
        final int PLOT_SIZE = 200;
        final int BORDER_SIZE = 4;
        final int CELL_SIZE = PLOT_SIZE + (2 * BORDER_SIZE);

        if (x < 0) {
            x--; //
        }
        if (z < 0) {
            z--;
        }
        // Step 1: Determine if we're in a border
        // Calculate modulo position in the repeating pattern
        int modX = Math.floorMod(x, CELL_SIZE);
        int modZ = Math.floorMod(z, CELL_SIZE);

        boolean inBorderX = modX < BORDER_SIZE || modX >= (BORDER_SIZE + PLOT_SIZE);
        boolean inBorderZ = modZ < BORDER_SIZE || modZ >= (BORDER_SIZE + PLOT_SIZE);

        if (inBorderX || inBorderZ) {
            return "Road"; // In a border
        }

        // Step 2: Calculate plot coordinates
        int plotX, plotZ;

        // For positive coordinates
        if (x >= 0) {
            plotX = (x / CELL_SIZE) + 1; // +1 because plots start at 1
        } else {
            // For negative coordinates, we need to adjust to make plots start at 0, -1, -2, etc.
            plotX = (int) Math.ceil((double) x / CELL_SIZE);
        }

        if (z >= 0) {
            plotZ = (z / CELL_SIZE) + 1; // +1 because plots start at 1
        } else {
            // For negative coordinates, we need to adjust to make plots start at 0, -1, -2, etc.
            plotZ = (int) Math.ceil((double) z / CELL_SIZE);
        }

        return plotX + ";" + plotZ;
    }
}