package space.devport.utils.menuutil;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.menuutil.events.MenuItemClickEvent;

import java.util.HashMap;

public class MenuHandler implements Listener {

    // Holding currently created GUIs
    @Getter
    private HashMap<String, Menu> guiCache = new HashMap<>();

    // Add a gui to the cache
    public void addGUI(Menu menu) {
        guiCache.put(menu.getName(), menu);
    }

    // Click listener, throws SimpleItemClickEvent
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCursor() == null || e.getClickedInventory() == null) return;

        // Get which gui the player clicked
        Inventory inventory = e.getClickedInventory();

        Menu menu = null;

        for (Menu menuLoop : guiCache.values())
            if (inventory.equals(menuLoop.getInventory()))
                menu = menuLoop;

        if (menu == null)
            return;

        if (!menu.getItems().containsKey(e.getSlot()))
            return;

        // Get clicked SimpleItem
        MenuItem clickedItem = menu.getItems().get(e.getSlot());

        // Cancel event if we should.
        if (clickedItem.isCancelClick())
            e.setCancelled(true);

        // Throw new event
        MenuItemClickEvent clickEvent = new MenuItemClickEvent(e, menu, clickedItem);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(clickEvent);

        // Call method
        menu.onClick(e, menu, clickedItem);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Menu menu = null;

        for (Menu menuLoop : guiCache.values())
            if (menuLoop.getInventory().equals(e.getInventory()))
                menu = menuLoop;

        if (menu == null)
            return;

        menu.close();
    }
}
