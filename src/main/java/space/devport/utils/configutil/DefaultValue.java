package space.devport.utils.configutil;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.messageutil.MessageBuilder;

public enum DefaultValue {

    /**
     * MENU
     */
    MENU_TITLE("My Simple GUI"),
    MENU_SLOTS(9, Integer.class),

    /**
     * MESSAGES
     */
    MESSAGE_BUILDER(new MessageBuilder(), MessageBuilder.class),

    /**
     * ITEM BUILDER
     */
    ITEM_TYPE("STONE"),
    ITEM_NAME("&cCould not load item"),
    ITEM_LINE("&cReason: &7{message}");

    @Getter
    @Setter
    private Class<?> type = String.class;

    @Setter
    private Object value;

    DefaultValue(Object defaultValue, Class<?>... type) {
        this.value = defaultValue;

        if (type.length > 0)
            this.type = type[0];
    }

    @Override
    public String toString() {
        return value.toString();
    }
}