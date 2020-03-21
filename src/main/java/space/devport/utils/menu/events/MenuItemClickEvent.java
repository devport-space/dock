package space.devport.utils.menu.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.utils.menu.Menu;
import space.devport.utils.menu.MenuItem;

/**
 * Event called when an Item in the Menu is clicked.
 *
 * @author Devport Team
 */
public class MenuItemClickEvent extends MenuEvent {

    // Clicked item
    @Getter
    private final MenuItem clickItem;

    // Event that fired this one
    @Getter
    private final InventoryClickEvent inventoryClickEvent;

    public MenuItemClickEvent(InventoryClickEvent inventoryClickEvent, Menu menu, MenuItem clickedItem) {
        super((Player) inventoryClickEvent.getWhoClicked(), menu);

        this.inventoryClickEvent = inventoryClickEvent;
        this.clickItem = clickedItem;
    }
}