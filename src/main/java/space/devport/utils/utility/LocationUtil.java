package space.devport.utils.utility;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportUtils;

/**
 * Static util class to assist location related operations.
 */
@UtilityClass
public class LocationUtil {

    /**
     * Parses a location to string using the default location delimiter.
     *
     * @param location Location to parse
     * @return parsed location String
     */
    @Nullable
    public String locationToString(@Nullable Location location) {
        return locationToString(location, Settings.LOCATION_DELIMITER.toString());
    }

    /**
     * Parses a location to string using given delimiter.
     *
     * @param location  Location to parse
     * @param delimiter String delimiter to use
     * @return parse location String
     */
    @Nullable
    public String locationToString(@Nullable Location location, @Nullable String delimiter) {
        if (location == null) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not parse location to string, location is null.");
            return null;
        }

        return location.getWorld().getName() + delimiter +
                location.getX() + delimiter +
                location.getY() + delimiter +
                location.getZ();
    }

    /**
     * Parses a location from string.
     *
     * @param locationString String to parse location from
     * @return parsed Location
     */
    @Nullable
    public Location locationFromString(@Nullable String locationString) {
        return locationFromString(locationString, Settings.LOCATION_DELIMITER.toString());
    }

    /**
     * Parses a location from String using given String delimiter.
     *
     * @param locationString String to parse location from
     * @param delimiter      String delimiter to use
     * @param useWorld       Optional boolean, whether to parse a world or not
     * @return parsed Location
     */
    @Nullable
    public Location locationFromString(@Nullable String locationString, @Nullable String delimiter, boolean... useWorld) {
        if (Strings.isNullOrEmpty(locationString) || Strings.isNullOrEmpty(delimiter))
            return null;

        boolean world = useWorld.length == 0 || useWorld[0];

        String[] arr = locationString.split(delimiter);

        if (arr.length < 4) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not parse a location from " + locationString + ", too few parameters.");
            return null;
        }

        try {
            if (world)
                return new Location(Bukkit.getWorld(arr[0]), Double.parseDouble(arr[1]), Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
            else
                return new Location(null, Double.parseDouble(arr[1]), Double.parseDouble(arr[2]), Double.parseDouble(arr[3]));
        } catch (NumberFormatException e) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not parse a location from " + locationString + ", parameter not a number.");
        } catch (NullPointerException e1) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not parse a location from " + locationString + ", parameter(s) missing.");
        }

        return null;
    }
}