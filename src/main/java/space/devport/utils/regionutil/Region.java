package space.devport.utils.regionutil;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import space.devport.utils.DevportUtils;

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

    // Set region to yaml
    public void set(FileConfiguration yaml, String path) {
        ConfigurationSection section = yaml.createSection(path);

        section.set("min", LocationUtil.locationToString(min));
        section.set("max", LocationUtil.locationToString(max));
    }

    // Load region from yaml
    public static Region loadRegion(FileConfiguration yaml, String path, String[] paths) {
        Location min = LocationUtil.locationFromString(yaml.getString(path + "." + paths[0]));
        Location max = LocationUtil.locationFromString(yaml.getString(path + "." + paths[1]));

        if (min == null) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a region at path " + path + ", minimum location didn't load.");
            return null;
        }

        if (max == null) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a region at path " + path + ", maximum location didn't load.");
            return null;
        }

        return new Region(min, max, false);
    }

    public static Region loadRegion(FileConfiguration yaml, String path) {
        String[] paths = new String[]{"min", "max"};
        return loadRegion(yaml, path, paths);
    }
}