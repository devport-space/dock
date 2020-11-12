package space.devport.utils.menu;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import space.devport.utils.DevportManager;
import space.devport.utils.DevportPlugin;
import space.devport.utils.menu.item.MenuItem;

import java.util.HashMap;

public class MenuManager extends DevportManager implements Listener {

    @Getter
    private final HashMap<String, Menu> registeredMenus = new HashMap<>();

    public MenuManager(DevportPlugin plugin) {
        super(plugin);
    }

    @Override
    public void afterEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null || e.getCursor() == null || e.getClickedInventory() == null) return;

        // Get which gui the player clicked
        Inventory inventory = e.getClickedInventory();
        Player player = (Player) e.getWhoClicked();

        Menu menu = null;

        for (Menu menuLoop : registeredMenus.values())
            if (inventory.equals(menuLoop.getInventory()) && menuLoop.getPlayer().equals(player))
                menu = menuLoop;

        if (menu == null || !menu.isOpen())
            return;

        if (!menu.getItems().containsKey(e.getSlot())) {
            e.setCancelled(true);
            return;
        }

        // Get clicked SimpleItem
        MenuItem clickedItem = menu.getItems().get(e.getSlot());

        // Cancel event if we should.
        if (clickedItem.isCancelClick()) {
            e.setCancelled(true);

            // Spam prevention
            if (menu.getMenuBuilder().getClickDelay() >= 0) {

                if (!clickedItem.isClickable())
                    return;

                clickedItem.setClickable(false);

                final MenuItem finalItem = clickedItem;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        finalItem.setClickable(true);
                    }
                }.runTaskLaterAsynchronously(plugin, menu.getMenuBuilder().getClickDelay());
            }
        }

        menu.runClick(e, clickedItem);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Menu menu = null;

        for (Menu menuLoop : registeredMenus.values())
            if (menuLoop.getInventory().equals(e.getInventory()))
                menu = menuLoop;

        if (menu == null || menu.getPlayer() == null || !menu.isOpen()) return;

        menu.close();
    }

    public void addMenu(Menu menu) {
        registeredMenus.put(menu.getName(), menu);
    }

    public void removeMenu(Menu menu) {
        registeredMenus.remove(menu.getName());
    }

    public void closeAll() {
        registeredMenus.values().forEach(Menu::close);
        registeredMenus.clear();
    }
}