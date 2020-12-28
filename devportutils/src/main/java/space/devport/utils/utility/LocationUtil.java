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

    public final String LOCATION_DELIMITER = ";";

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
     * @return Parsed location string
     */
    @Nullable
    @Contract("null,_,_ -> null;_,null,_ -> null")
    public String composeString(Location location, String delimiter, @Nullable ExceptionCallback callback) {

        if (delimiter == null) {
            if (callback != null)
                callback.call(CallbackContent.createNew(new IllegalArgumentException("Delimiter cannot be null."), "delimiter"));
            return null;
        }

        if (location == null) {
            if (callback != null)
                callback.call(CallbackContent.createNew(new IllegalArgumentException("Location cannot be null."), "location"));
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
     * @return parsed Location
     */
    @Nullable
    public Location parseLocation(@Nullable String str, @Nullable String delimiter, boolean useWorld, ExceptionCallback callback) {

        if (Strings.isNullOrEmpty(str)) {
            callback.call(CallbackContent.createNew(new IllegalArgumentException("Input string cannot be null."),
                    "input", str));
            return null;
        }

        if (Strings.isNullOrEmpty(delimiter)) {
            callback.call(CallbackContent.createNew(new IllegalArgumentException("Delimiter cannot be null."),
                    "delimiter", delimiter));
            return null;
        }

        String[] arr = str.split(delimiter);

        if (arr.length < 4) {
            callback.call(CallbackContent.createNew(new IllegalArgumentException("Not enough arguments."),
                    "input", str));
            return null;
        }

        try {
            return new Location(useWorld ? Bukkit.getWorld(arr[0]) : null,
                    Double.parseDouble(arr[1]),
                    Double.parseDouble(arr[2]),
                    Double.parseDouble(arr[3]));
        } catch (NumberFormatException | NullPointerException e) {
            callback.call(CallbackContent.createNew(e, "input", str));
        }

        return null;
    }
}