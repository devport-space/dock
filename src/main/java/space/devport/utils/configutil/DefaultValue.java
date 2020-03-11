package space.devport.utils.configutil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import space.devport.utils.messageutil.MessageBuilder;

/**
 * Enum that holds default values for custom object loading.
 *
 * @author Devport Team
 */
@AllArgsConstructor
public enum DefaultValue {

    /**
     * MENU
     */

    MENU_TITLE(new MessageBuilder("My Cute Menu")),
    MENU_SLOTS(9),
    MENU_FILL_ALL(false),

    /**
     * MENU ITEMS
     */

    MENU_ITEM_MATRIX_CHAR(' '),

    /**
     * MESSAGES
     */

    MESSAGE_BUILDER(new MessageBuilder()),

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

    LIST_DELIMITER("\n");

    @Getter
    @Setter
    private Object value;
}