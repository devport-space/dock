package space.devport.utils.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.utils.menu.item.MenuItem;

public interface MenuListener {
    boolean onClick(InventoryClickEvent clickEvent, MenuItem clickedItem);

    void onClose();

    void onOpen();
}