package space.devport.utils.configutil;

import lombok.Setter;

// Some default values for object loading
public enum DefaultValue {

    /**
     * MENU
     */
    MENU_TITLE("My Simple GUI"),

    /**
     * ITEM BUILDER
     */
    ITEM_TYPE("STONE"),
    ITEM_NAME("&cCould not load item"),
    ITEM_LINE("&cReason: &7{message}");

    @Setter
    private String defaultValue;

    DefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return defaultValue;
    }
}