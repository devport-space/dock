package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.menuutil.events.MenuCloseEvent;
import space.devport.utils.menuutil.events.MenuOpenEvent;

import java.util.HashMap;

public class Menu implements MenuListener {

    // System name of the menu
    @Getter
    public String name;

    // Current items in the Menu indexed by slot.
    @Getter
    public HashMap<Integer, MenuItem> items;

    // Menu builder to build the Menu by.
    @Getter
    @Setter
    public MenuBuilder menuBuilder;

    // For whom the inventory is open, null if closed.
    @Getter
    public Player player;

    // Inventory
    @Getter
    @Setter
    public Inventory inventory;

    @Getter
    private boolean open = false;

    public Menu(String name, MenuBuilder builder) {
        this.name = name;

        this.menuBuilder = builder;

        this.items = builder.getBuiltItems();
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
            items = menuBuilder.getBuiltItems();
        }

        // Throw event
        MenuOpenEvent openEvent = new MenuOpenEvent(player, this);
        DevportUtils.getInstance().getPlugin().getServer().getPluginManager().callEvent(openEvent);

        if (!openEvent.isCancelled()) {
            this.player = player;

            DevportUtils.getInstance().getMenuHandler().addMenu(this);

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
        items = menuBuilder.getBuiltItems();
        inventory.setContents(menuBuilder.getInventory().getContents());
    }

    // Close the menu
    public void close() {
        if (player == null)
            return;

        // Throw close event
        MenuCloseEvent closeEvent = new MenuCloseEvent(player, this);
        DevportUtils.getInstance().getPlugin().getServer().getPluginManager().callEvent(closeEvent);

        if (!closeEvent.isCancelled()) {

            player.closeInventory();

            DevportUtils.getInstance().getMenuHandler().removeMenu(this);

            open = false;

            onClose();

            player = null;
        }
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent, MenuItem clickedItem) {
    }

    @Override
    public void onClose() {
    }

    @Override
    public void onOpen() {
    }
}