package space.devport.utils.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportPlugin;
import space.devport.utils.DevportUtils;
import space.devport.utils.menu.events.MenuCloseEvent;
import space.devport.utils.menu.events.MenuOpenEvent;

import java.util.HashMap;

public class Menu implements MenuListener {

    @Getter
    public String name;

    @Getter
    public HashMap<Integer, MenuItem> items;

    @Getter
    @Setter
    public MenuBuilder menuBuilder;

    @Getter
    public Player player;

    @Getter
    @Setter
    public Inventory inventory;

    @Getter
    private boolean open = false;

    public Menu(String name, MenuBuilder builder) {
        this.name = name;

        this.menuBuilder = new MenuBuilder(name, builder);

        this.items = builder.getItems();
        this.inventory = builder.getInventory();
    }

    // Open the gui for a player
    public void open(Player player, boolean... rebuild) {

        // Remove the inventory if we should rebuild when opening.
        if (rebuild.length > 0)
            if (rebuild[0])
                menuBuilder.clear();

        // Build the inventory if it's not there yet.
        if (inventory == null) {
            menuBuilder.build();
            inventory = menuBuilder.getInventory();
            items = menuBuilder.getItems();
        }

        // Throw event
        MenuOpenEvent openEvent = new MenuOpenEvent(player, this);
        DevportUtils.getInstance().getPlugin().getServer().getPluginManager().callEvent(openEvent);

        if (!openEvent.isCancelled()) {
            this.player = player;

            DevportPlugin.getInstance().getMenuHandler().addMenu(this);

            player.openInventory(inventory);

            open = true;

            onOpen();
        }
    }

    // Reload inventory
    public void reload(boolean... reopen) {
        if (reopen.length > 0)
            if (reopen[0]) {
                Player p = player;
                close();
                inventory = menuBuilder.clear().build().getInventory();
                open(p);
                return;
            }

        menuBuilder.clear().build();
        items = menuBuilder.getItems();
        inventory.setContents(menuBuilder.getInventory().getContents());
    }

    // Close the menu
    public void close() {
        if (player == null || !open)
            return;

        // Throw close event
        MenuCloseEvent closeEvent = new MenuCloseEvent(player, this);
        DevportUtils.getInstance().getPlugin().getServer().getPluginManager().callEvent(closeEvent);

        if (!closeEvent.isCancelled()) {

            open = false;

            player.closeInventory();

            DevportPlugin.getInstance().getMenuHandler().removeMenu(this);

            onClose();

            player = null;
        }
    }

    public void runClick(InventoryClickEvent clickEvent, MenuItem clickedItem) {
        if (onClick(clickEvent, clickedItem)) {
            clickedItem.callClick();
        }
    }

    @Override
    public boolean onClick(InventoryClickEvent clickEvent, MenuItem clickedItem) {
        return true;
    }

    @Override
    public void onClose() {
    }

    @Override
    public void onOpen() {
    }
}