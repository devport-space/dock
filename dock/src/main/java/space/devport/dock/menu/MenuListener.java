package space.devport.dock.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.dock.menu.item.MenuItem;

public interface MenuListener {
    boolean onClick(InventoryClickEvent clickEvent, MenuItem clickedItem);

    void onClose();

    void onOpen();
}