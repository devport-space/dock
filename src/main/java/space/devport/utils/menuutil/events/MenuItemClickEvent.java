package space.devport.utils.menuutil.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.utils.menuutil.Menu;
import space.devport.utils.menuutil.MenuItem;

public class MenuItemClickEvent extends MenuEvent implements Cancellable {

    // This event is called when an Item in a simple gui item is clicked

    // Clicked item
    @Getter
    private MenuItem clickItem;

    // Event that fired this one
    @Getter
    private InventoryClickEvent inventoryClickEvent;

    @Getter
    @Setter
    private boolean cancelled = false;

    public MenuItemClickEvent(InventoryClickEvent inventoryClickEvent, Menu menu, MenuItem clickedItem) {
        super((Player) inventoryClickEvent.getWhoClicked(), menu);
        this.inventoryClickEvent = inventoryClickEvent;
        this.clickItem = clickedItem;
    }
}
