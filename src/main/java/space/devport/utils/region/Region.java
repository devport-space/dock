package space.devport.utils.region;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.utility.Default;

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
     * @param min Minimal region location
     * @param max Maximal region location
     */
    public Region(@NotNull Location min, @NotNull Location max) {
        World world = min.getWorld();

        if (min.getWorld() != max.getWorld()) throw new IllegalArgumentException("Worlds are not the same");

        this.min = new Location(world, Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()));
        this.max = new Location(world, Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()));

        this.ignoreHeight = (boolean) Default.REGION_IGNORE_HEIGHT.getValue();
    }

    /**
     * A constructor with a minimal, maximal location and ignore height parameter.
     *
     * @param min          Minimal region location
     * @param max          Maximal region location
     * @param ignoreHeight boolean, whether or not should the region ignore Y axis
     */
    public Region(@NotNull Location min, @NotNull Location max, boolean ignoreHeight) {
        this(min, max);
        this.ignoreHeight = ignoreHeight;
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