package space.devport.utils.regionutil;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Custom region handling.
 *
 * @author Devport Team
 */
public class Region {

    // Min location of the region
    @Getter
    @Setter
    private Location min;

    // Max location of the region
    @Getter
    @Setter
    private Location max;

    // Should we ignore Y coordinates?
    @Getter
    @Setter
    private boolean ignoreHeight;

    /**
     * Default constructor with a minimal and maximal location.
     *
     * @param min          Minimal region location
     * @param max          Maximal region location
     * @param ignoreHeight Optional boolean, whether or not should the region ignore Y axis
     */
    public Region(Location min, Location max, boolean... ignoreHeight) {
        this.min = min;
        this.max = max;

        this.ignoreHeight = ignoreHeight.length != 0 && ignoreHeight[0];
    }

    /**
     * Returns whether or not does the region contain a location.
     *
     * @param location Location to try
     * @return boolean whether or not does this region contain given location
     */
    public boolean contains(@NotNull Location location) {

        // Check world
        if (max.getWorld() != null)
            if (!max.getWorld().equals(location.getWorld()))
                return false;

        // Check coordinates
        if (ignoreHeight)
            return location.getX() <= max.getX() && location.getX() >= min.getX()
                    && location.getZ() <= max.getZ() && location.getZ() >= min.getZ();
        else
            return location.getX() <= max.getX() && location.getX() >= min.getX()
                    && location.getZ() <= max.getZ() && location.getZ() >= min.getZ()
                    && location.getY() <= max.getY() && location.getY() >= min.getY();
    }
}