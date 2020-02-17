package space.devport.utils.simplegui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import space.devport.utils.itemutil.ItemBuilder;

public class SimpleItem {

    // Holds information about an Item in a Simple GUI

    // Name of the item, will be used for loading/saving later.
    @Getter
    private String name;

    // Just to have the info here
    @Getter
    private int slot;

    // Item Builder for the item
    @Getter
    @Setter
    private ItemBuilder itemBuilder;

    // ItemStack currently built and in the GUI
    @Getter
    @Setter
    private ItemStack item;

    // Cancel the event?
    @Getter
    @Setter
    private boolean cancelClick = true;

    public SimpleItem(ItemBuilder itemBuilder, String name, int slot) {
        this.name = name;
        this.slot = slot;
        this.itemBuilder = itemBuilder;
    }
}