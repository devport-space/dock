package space.devport.utils.configutil;

import lombok.Setter;

// Sub paths to different parts of an Object
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
    ITEM_FLAGS("flags"),
    ITEM_NBT("nbt"),
    ITEM_GLOW("glow");

    @Setter
    private String subPath;

    SubPath(String subPath) {
        this.subPath = subPath;
    }

    @Override
    public String toString() {
        return subPath;
    }
}