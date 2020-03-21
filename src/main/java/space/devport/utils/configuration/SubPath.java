package space.devport.utils.configuration;

import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * Holds Configuration sub-paths to different paths to custom objects.
 *
 * @author Devport Team
 * */
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
    MENU_FILL_ALL("fill-all"),
    MENU_FILL_SLOTS("fill-slots"),
    MENU_FILL_SLOTS_DELIMITER(";"),
    MENU_MATRIX("matrix"),
    MENU_ITEMS("items"),
    MENU_FILLER("filler"),
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
    ITEM_GLOW("glow"),

    /**
     * CONDITIONED REWARD PACK
     * */

    CONDITION_PACK("condition"),
    REWARD_PACK("reward")
    ;

    @Setter
    private String subPath;

    @Override
    public String toString() {
        return subPath;
    }
}