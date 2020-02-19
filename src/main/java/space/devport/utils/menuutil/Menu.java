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

    @Getter
    public String name;

    // For whom the inventory is open, null if closed.
    @Getter
    public Player player;

    @Getter
    public HashMap<Integer, MenuItem> items;

    @Getter
    public Inventory inventory;

    @Getter
    @Setter
    public MenuBuilder menuBuilder;

    @Getter
    @Setter
    private boolean open;

    public Menu(MenuBuilder builder) {
        this(builder.getName(), builder.getInventory(), builder.getItems());

        this.menuBuilder = builder;
    }

    public Menu(String name, Inventory inventory, HashMap<Integer, MenuItem> items) {
        this.name = name;
        this.inventory = inventory;
        this.items = items;
    }

    // Open the gui for a player
    public void open(Player player) {

        if (inventory == null) {
            DevportUtils.inst.getConsoleOutput().err("Inventory is not built.");
            return;
        }

        // Throw event
        MenuOpenEvent openEvent = new MenuOpenEvent(player, this);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(openEvent);

        if (!openEvent.isCancelled()) {
            this.player = player;

            DevportUtils.inst.getMenuHandler().addMenu(this);

            open = true;
            player.openInventory(inventory);

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

        inventory.setContents(menuBuilder.clear().build().getInventory().getContents());
    }

    // Close the menu
    public void close() {
        if (player == null)
            return;

        // Throw close event
        MenuCloseEvent closeEvent = new MenuCloseEvent(player, this);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(closeEvent);

        if (!closeEvent.isCancelled()) {

            open = false;
            player.closeInventory();

            DevportUtils.inst.getMenuHandler().removeMenu(this);

            player = null;

            onClose();
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