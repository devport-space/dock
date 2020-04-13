package space.devport.utils.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import space.devport.utils.text.Message;
import space.devport.utils.item.Amount;

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
     * MESSAGES
     */

    MESSAGE_BUILDER(new Message()),

    /**
     * REGION
     */

    REGION_IGNORE_HEIGHT(false),

    /**
     * ITEM BUILDER
     */

    ITEM_TYPE("STONE"),
    ITEM_NAME("&cCould not load item"),
    ITEM_LINE("&cReason: &7{message}"),

    /**
     * STRING UTIL
     */

    LIST_DELIMITER("\n"),

    /**
     * LOCATION UTIL
     */

    LOCATION_DELIMITER(";"),

    /**
     * AMOUNT
     */

    AMOUNT(new Amount(1));

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