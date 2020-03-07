package space.devport.utils.regionutil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import space.devport.utils.DevportUtils;

public class LocationUtil {

    // Parse a location to string, uses ; as a delimiter.
    public static String locationToString(Location location) {
        return locationToString(location, ";");
    }

    // Parse a location to string, uses given delimiter
    public static String locationToString(Location location, String delimiter) {

        if (location == null) {
            DevportUtils.inst.getConsoleOutput().err("Could not parse location to string, location is null.");
            return null;
        }

        return location.getWorld().getName() + delimiter + location.getX() + delimiter + location.getY() + delimiter + location.getZ();
    }

    // Parse a location from string, uses ; as a delimiter
    public static Location locationFromString(String dataString) {
        return locationFromString(dataString, ";");
    }

    // Parse a location from string, uses given delimiter
    public static Location locationFromString(String dataString, String delimiter) {
        String[] arr = dataString.split(delimiter);

        if (arr.length < 4) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a location from " + dataString + ", too few parameters.");
            return null;
        }

        try {
            return new Location(Bukkit.getWorld(arr[0]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
        } catch (NumberFormatException e) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a location from " + dataString + ", parameter not a number.");
        } catch (NullPointerException e1) {
            DevportUtils.inst.getConsoleOutput().err("Could not load a location from " + dataString + ", parameter(s) missing.");
        }

        return null;
    }
}