package space.devport.utils.configuration;

import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * Holds Configuration sub-paths to different paths to custom objects.
 *
 * @author Devport Team
 */
@AllArgsConstructor
public enum SubPath {

    /**
     * REGIONS
     */

    REGION_MIN("min"),
    REGION_MAX("max"),

    /**
     * MENU
     */

    MENU_TITLE("title"),
    MENU_SLOTS("slots"),
    MENU_MATRIX("matrix"),
    MENU_ITEMS("items"),
    MENU_MATRIX_CHAR("matrix-char"),

    /**
     * MENU ITEM
     */

    MENU_ITEM_CANCEL_CLICK("cancel-click"),
    MENU_ITEM_SLOT("slot"),

    /**
     * ITEM BUILDER
     */

    ITEM_TYPE("type"),
    ITEM_DATA("data"),
    ITEM_NAME("name"),
    ITEM_AMOUNT("amount"),
    ITEM_LORE("lore"),
    ITEM_ENCHANTS("enchants"),
    ITEM_ENCHANT_DELIMITER(";"),
    ITEM_FLAGS("flags"),
    ITEM_NBT("nbt"),
    ITEM_NBT_DELIMITER(";"),
    ITEM_SKULL_DATA("head"),
    ITEM_GLOW("glow");

    @Setter
    private String subPath;

    @Override
    public String toString() {
        return subPath;
    }
}