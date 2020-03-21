package space.devport.utils.menu;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface MenuListener {
    void onClick(InventoryClickEvent clickEvent, MenuItem clickedItem);

    void onClose();

    void onOpen();
}