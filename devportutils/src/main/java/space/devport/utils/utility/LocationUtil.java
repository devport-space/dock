package space.devport.utils.utility;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.callbacks.CallbackContent;
import space.devport.utils.callbacks.ExceptionCallback;
import space.devport.utils.text.StringUtil;

/**
 * Static util class to assist location related operations.
 */
@UtilityClass
public class LocationUtil {

    public static final String LOCATION_DELIMITER = ";";

    /**
     * Parses a location to string using the default location delimiter.
     *
     * @param location Location to parse
     * @return parsed location String
     */
    @Nullable
    @Contract("null -> null")
    public String composeString(Location location) {
        return composeString(location, LOCATION_DELIMITER, null);
    }

    @Contract("null,_ -> null")
    public String composeString(Location location, @Nullable ExceptionCallback callback) {
        return composeString(location, LOCATION_DELIMITER, callback);
    }

    @Contract("null,_ -> null;_,null -> null")
    public String composeString(Location location, String delimiter) {
        return composeString(location, delimiter, null);
    }

    /**
     * Parses a location to string using given delimiter.
     *
     * @param location  Location to parse
     * @param delimiter String delimiter to use
     * @param callback  {@link ExceptionCallback} to call on failure.
     * @return Parsed location string
     */
    @Nullable
    @Contract("null,_,_ -> null;_,null,_ -> null")
    public String composeString(Location location, String delimiter, @Nullable ExceptionCallback callback) {

        if (delimiter == null) {
            CallbackContent.createNew(new IllegalArgumentException("Delimiter cannot be null."), "delimiter")
                    .callOrThrow(callback);
            return null;
        }

        if (location == null) {
            CallbackContent.createNew(new IllegalArgumentException("Location cannot be null."), "location")
                    .callOrThrow(callback);
            return null;
        }

        boolean world = location.getWorld() != null;

        return StringUtil.join(delimiter,
                world ? location.getWorld().getName() : "",
                location.getX(),
                location.getY(),
                location.getZ());
    }

    @Contract("null -> null")
    public Location parseLocation(String str) {
        return parseLocation(str, LOCATION_DELIMITER, true, null);
    }

    @Contract("null,_ -> null")
    public Location parseLocation(String str, boolean useWorld) {
        return parseLocation(str, LOCATION_DELIMITER, useWorld, null);
    }

    @Contract("null,_ -> null")
    public Location parseLocation(String str, @Nullable ExceptionCallback callback) {
        return parseLocation(str, LOCATION_DELIMITER, true, callback);
    }

    @Contract("null,_,_ -> null")
    public Location parseLocation(String str, boolean useWorld, @Nullable ExceptionCallback callback) {
        return parseLocation(str, LOCATION_DELIMITER, useWorld, callback);
    }

    @Contract("null,_,_ -> null")
    public Location parseLocation(String str, String delimiter, @Nullable ExceptionCallback callback) {
        return parseLocation(str, delimiter, true, callback);
    }

    @Contract("null,_,_ -> null")
    public Location parseLocation(String str, String delimiter, boolean useWorld) {
        return parseLocation(str, delimiter, useWorld, null);
    }

    /**
     * Parses a location from String using given String delimiter.
     *
     * @param str       String to parse location from
     * @param delimiter String delimiter to use
     * @param useWorld  Optional boolean, whether to parse a world or not
     * @param callback  {@link ExceptionCallback} to call on failure.
     * @return parsed Location
     */
    @Nullable
    public Location parseLocation(@Nullable String str, @Nullable String delimiter, boolean useWorld, ExceptionCallback callback) {

        if (Strings.isNullOrEmpty(str)) {
            CallbackContent.createNew(new IllegalArgumentException("Input string cannot be null."), "input", str)
                    .callOrThrow(callback);
            return null;
        }

        if (Strings.isNullOrEmpty(delimiter)) {
            CallbackContent.createNew(new IllegalArgumentException("Delimiter cannot be null."), "delimiter", delimiter)
                    .callOrThrow(callback);
            return null;
        }

        String[] arr = str.split(delimiter);

        if (arr.length < 4) {
            CallbackContent.createNew(new IllegalArgumentException("Not enough arguments."),
                    "input", str)
                    .callOrThrow(callback);
            return null;
        }

        try {
            return new Location(useWorld ? Bukkit.getWorld(arr[0]) : null,
                    Double.parseDouble(arr[1]),
                    Double.parseDouble(arr[2]),
                    Double.parseDouble(arr[3]));
        } catch (NumberFormatException | NullPointerException e) {
            CallbackContent.createNew(e, "input", str).callOrThrow(callback);
        }

        return null;
    }
}