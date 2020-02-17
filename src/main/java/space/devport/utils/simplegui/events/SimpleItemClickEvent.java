package space.devport.utils.simplegui.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import space.devport.utils.simplegui.SimpleGUI;
import space.devport.utils.simplegui.SimpleItem;

public class SimpleItemClickEvent extends PlayerEvent {

    // This event is called when an Item in a simple gui item is clicked

    // Clicked GUI
    @Getter
    private SimpleGUI gui;

    // Clicked item
    @Getter
    private SimpleItem item;

    // Event that fired this one
    @Getter
    private InventoryClickEvent inventoryClickEvent;

    public SimpleItemClickEvent(InventoryClickEvent inventoryClickEvent, SimpleGUI gui, SimpleItem item) {
        super((Player) inventoryClickEvent.getWhoClicked());
        this.inventoryClickEvent = inventoryClickEvent;
        this.gui = gui;
        this.item = item;
    }

    public static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
