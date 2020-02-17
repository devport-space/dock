package space.devport.utils.simplegui.events;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.utils.simplegui.SimpleGUI;
import space.devport.utils.simplegui.SimpleItem;

public class SimpleItemClickEvent extends InventoryClickEvent {

    // This event is called when an Item in a simple gui item is clicked

    // Clicked GUI
    @Getter
    private SimpleGUI gui;

    // Clicked item
    @Getter
    private SimpleItem item;

    public SimpleItemClickEvent(InventoryClickEvent e, SimpleGUI gui, SimpleItem item) {
        super(e.getView(), e.getSlotType(), e.getSlot(), e.getClick(), e.getAction());
        this.gui = gui;
        this.item = item;
    }
}
