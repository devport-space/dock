package space.devport.utils.menuutil;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import space.devport.utils.DevportUtils;
import space.devport.utils.menuutil.events.MenuItemClickEvent;

import java.util.HashMap;

public class MenuHandler implements Listener {

    // API reference
    private final DevportUtils devportUtils;

    // Holding currently created GUIs
    @Getter
    private final HashMap<String, Menu> menuCache = new HashMap<>();

    /**
     * Default constructor, requires DevportUtils instanced with a JavaPlugin reference.
     * Registers it's Bukkit Event Listener.
     */
    public MenuHandler() {
        this.devportUtils = DevportUtils.getInstance();
        devportUtils.getPlugin().getServer().getPluginManager().registerEvents(this, devportUtils.getPlugin());
    }

    // Add a gui to the cache
    public void addMenu(Menu menu) {
        menuCache.put(menu.getName(), menu);
    }

    public void removeMenu(Menu menu) {
        menuCache.remove(menu.getName());
    }

    // Close all the menus
    public void closeAll() {
        menuCache.values().forEach(Menu::close);
        menuCache.clear();
    }

    // Click listener, throws SimpleItemClickEvent
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCursor() == null || e.getClickedInventory() == null) return;

        // Get which gui the player clicked
        Inventory inventory = e.getClickedInventory();
        Player player = (Player) e.getWhoClicked();

        Menu menu = null;

        for (Menu menuLoop : menuCache.values())
            if (inventory.equals(menuLoop.getInventory()) && menuLoop.getPlayer().equals(player))
                menu = menuLoop;

        if (menu == null || !menu.getItems().containsKey(e.getSlot()) || !menu.isOpen()) return;

        // Get clicked SimpleItem
        MenuItem clickedItem = menu.getItems().get(e.getSlot());

        // Throw new event
        MenuItemClickEvent clickEvent = new MenuItemClickEvent(e, menu, clickedItem);
        devportUtils.getPlugin().getServer().getPluginManager().callEvent(clickEvent);

        // Return if the event was cancelled
        if (clickEvent.isCancelled())
            return;

        // Update the item from the event
        clickedItem = clickEvent.getClickItem();

        // Cancel event if we should.
        if (clickedItem.isCancelClick()) {
            e.setCancelled(true);

            // Spam prevention
            if (menu.getMenuBuilder().getClickDelay() != 0) {

                if (!clickedItem.isClickable())
                    return;

                clickedItem.setClickable(false);

                final MenuItem finalItem = clickedItem;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        finalItem.setClickable(true);
                    }
                }.runTaskLaterAsynchronously(devportUtils.getPlugin(), menu.getMenuBuilder().getClickDelay());
            }
        }

        // Call method
        menu.onClick(e, clickedItem);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Menu menu = null;

        for (Menu menuLoop : menuCache.values())
            if (menuLoop.getInventory().equals(e.getInventory()))
                menu = menuLoop;

        if (menu == null)
            return;

        // Player is null when the menu is not open
        if (menu.getPlayer() == null)
            return;

        menu.close();
    }
}