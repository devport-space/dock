package space.devport.utils.regions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class Region {

    // Min location of the region
    @Getter
    private Location min;

    // Max location of the region
    @Getter
    private Location max;

    // Should we ignore Y coordinates?
    @Getter
    @Setter
    private boolean ignoreHeight = false;

    // Default two location region constructor
    public Region(Location min, Location max) {
        this.min = min;
        this.max = max;
    }

    // Constructor with ignoreHeight parameter
    public Region(Location min, Location max, boolean ignoreHeight) {
        this.min = min;
        this.max = max;

        this.ignoreHeight = ignoreHeight;
    }

    // Does this region contain given location?
    public boolean contains(Location location) {
        if (ignoreHeight)
            return location.getX() <= max.getX() && location.getX() >= min.getX()
                    && location.getZ() <= max.getZ() && location.getZ() >= min.getZ();
        else
            return location.getX() <= max.getX() && location.getX() >= min.getX()
                    && location.getZ() <= max.getZ() && location.getZ() >= min.getZ()
                    && location.getY() <= max.getY() && location.getY() >= min.getY();
    }
}