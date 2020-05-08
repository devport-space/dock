package space.devport.utils.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Enum that holds settings for object-to-string parsing.
 *
 * @author Devport Team
 */
@AllArgsConstructor
public enum Settings {

    /**
     * STRING UTIL
     */

    LIST_DELIMITER("\n"),

    /**
     * LOCATION UTIL
     */

    LOCATION_DELIMITER(";");

    @Getter
    @Setter
    private Object value;

    /**
     * Get the value.
     *
     * @return Value object
     */
    public Object get() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}