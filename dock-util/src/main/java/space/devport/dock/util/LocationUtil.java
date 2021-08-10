package space.devport.dock.util;

import org.jetbrains.annotations.NotNull;
import space.devport.dock.common.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.common.Result;

/**
 * Static util class to assist location related operations.
 */
@UtilityClass
public class LocationUtil {

    public static final String LOCATION_DELIMITER = ";";

    @NotNull
    public Result<String> composeString(Location location) {
        return composeString(location, LOCATION_DELIMITER);
    }

    /**
     * Parses a location to string using given delimiter.
     *
     * @param location  Location to parse
     * @param delimiter String delimiter to use
     * @return Parsed location string
     */
    @NotNull
    public Result<String> composeString(Location location, String delimiter) {

        if (delimiter == null)
            return Result.ofException(new IllegalArgumentException("Delimiter cannot be null."));

        if (location == null)
            return Result.ofException(new IllegalArgumentException("Location cannot be null."));

        boolean world = location.getWorld() != null;

        return Result.of(StringUtil.join(delimiter,
                world ? location.getWorld().getName() : "",
                location.getX(),
                location.getY(),
                location.getZ()));
    }

    @NotNull
    public Result<Location> parseLocation(String str) {
        return parseLocation(str, LOCATION_DELIMITER, true);
    }

    @NotNull
    public Result<Location> parseLocation(String str, String delimiter) {
        return parseLocation(str, delimiter, true);
    }

    @NotNull
    public Result<Location> parseLocation(String str, boolean useWorld) {
        return parseLocation(str, LOCATION_DELIMITER, useWorld);
    }

    /**
     * Parses a location from String using given String delimiter.
     *
     * @param str       String to parse location from
     * @param delimiter String delimiter to use
     * @param useWorld  Optional boolean, whether to parse a world or not
     * @return parsed Location
     */
    @NotNull
    public Result<Location> parseLocation(@Nullable String str, @Nullable String delimiter, boolean useWorld) {

        if (Strings.isNullOrEmpty(str))
            return Result.ofException(new IllegalArgumentException(String.format("Input string cannot be null or empty. Got: '%s'", str)));

        if (Strings.isNullOrEmpty(delimiter))
            return Result.ofException(new IllegalArgumentException(String.format("Delimiter cannot be null or empty. Got: '%s'", delimiter)));

        String[] arr = str.split(delimiter);

        if (arr.length < 4)
            return Result.ofException(new IllegalArgumentException(String.format("Not enough arguments. Required length: %d, Provided: %d", 4, arr.length)));

        return Result.supply(
                () -> new Location(useWorld ? Bukkit.getWorld(arr[0]) : null,
                        Double.parseDouble(arr[1]),
                        Double.parseDouble(arr[2]),
                        Double.parseDouble(arr[3]))
        );
    }
}