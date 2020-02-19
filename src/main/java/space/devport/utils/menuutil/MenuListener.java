package space.devport.utils.menuutil;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface MenuListener {
    void onClick(InventoryClickEvent clickEvent, MenuItem clickedItem);

    void onClose();

    void onOpen();
}