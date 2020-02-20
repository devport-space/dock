package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.itemutil.ItemBuilder;

public class MenuItem {

    // Holds information about an Item in a Simple GUI

    // Name of the item
    @Getter
    @NotNull
    private final String name;

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

    // Default constructor
    public MenuItem(@NotNull ItemBuilder itemBuilder, @NotNull String name, int slot) {
        this.name = name;

        this.slot = slot;
        this.itemBuilder = itemBuilder;
    }

    public MenuItem(@NotNull MenuItem item) {
        this.name = item.getName();
        this.slot = item.getSlot();

        this.itemBuilder = new ItemBuilder(item.getItemBuilder());

        this.cancelClick = item.isCancelClick();
    }
}