package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.itemutil.ItemBuilder;

public class MenuItem {

    // Holds information about an Item in a Simple GUI

    // Name of the item, will be used for loading/saving later.
    @Getter
    private String name;

    // Just to have the info here
    @Getter
    @Setter
    private int slot;

    // Item Builder for the item
    @Getter
    @Setter
    private ItemBuilder itemBuilder;

    // Cancel the event?
    @Getter
    @Setter
    private boolean cancelClick = true;

    public MenuItem(ItemBuilder itemBuilder, String name, int slot) {
        this.name = name;
        this.slot = slot;
        this.itemBuilder = itemBuilder;
    }
}