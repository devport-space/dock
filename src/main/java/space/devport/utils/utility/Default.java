package space.devport.utils.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import space.devport.utils.text.message.Message;

/**
 * Enum that holds default values for custom object loading and manipulation.
 *
 * @author Devport Team
 */
@AllArgsConstructor
public enum Default {

    /**
     * MENU
     */

    MENU_TITLE(new Message("My Cute Menu")),
    MENU_SLOTS(9),
    MENU_FILL_ALL(false),

    /**
     * MENU ITEMS
     */

    MENU_ITEM_MATRIX_CHAR(' '),

    /**
     * REGION
     */

    REGION_IGNORE_HEIGHT(false),

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