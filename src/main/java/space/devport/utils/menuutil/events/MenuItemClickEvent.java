package space.devport.utils.menuutil.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.utils.menuutil.Menu;
import space.devport.utils.menuutil.MenuItem;

public class MenuItemClickEvent extends MenuEvent {

    // This event is called when an Item in a simple gui item is clicked

    // Clicked item
    @Getter
    private MenuItem item;

    // Event that fired this one
    @Getter
    private InventoryClickEvent inventoryClickEvent;

    public MenuItemClickEvent(InventoryClickEvent inventoryClickEvent, Menu menu, MenuItem item) {
        super((Player) inventoryClickEvent.getWhoClicked(), menu);
        this.inventoryClickEvent = inventoryClickEvent;
        this.item = item;
    }
}
