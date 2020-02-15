package space.devport.utils.simplegui.events;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimpleItemClickEvent extends InventoryClickEvent {

    // This event is called when an Item in a simple gui item is clicked

    // Name of the clicked gui
    @Getter
    private String guiName;

    // Name of the item clicked, taken from NBT
    @Getter
    private String itemName;

    public SimpleItemClickEvent(InventoryClickEvent e, String guiName, String itemName) {
        super(e.getView(), e.getSlotType(), e.getSlot(), e.getClick(), e.getAction());
        this.guiName = guiName;
        this.itemName = itemName;
    }
}
