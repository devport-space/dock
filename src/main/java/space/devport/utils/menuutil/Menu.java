package space.devport.utils.menuutil;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.menuutil.events.MenuCloseEvent;
import space.devport.utils.menuutil.events.MenuOpenEvent;

import java.util.HashMap;

public class Menu implements MenuListener {

    @Getter
    private String name;

    // For whom the inventory is open, null if closed.
    @Getter
    private Player player;

    @Getter
    private HashMap<Integer, MenuItem> items;

    @Getter
    private Inventory inventory;

    public Menu(MenuBuilder builder) {
        this(builder.getName(), builder.getInventory(), builder.getItems());
    }

    public Menu(String name, Inventory inventory, HashMap<Integer, MenuItem> items) {
        this.name = name;
        this.inventory = inventory;
        this.items = items;

        DevportUtils.inst.getMenuHandler().addGUI(this);
    }

    // Open the gui for a player
    public void open(Player player) {

        if (inventory == null) {
            DevportUtils.inst.getConsoleOutput().err("Inventory is not built.");
            return;
        }

        this.player = player;
        player.openInventory(inventory);

        MenuOpenEvent openEvent = new MenuOpenEvent(player, this);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(openEvent);
    }

    // Reload inventory contents without reopening
    public void reload(MenuBuilder menuBuilder) {
        inventory.setContents(menuBuilder.build().getInventory().getContents());
    }

    // Close the menu
    public void close() {
        player.closeInventory();

        MenuCloseEvent closeEvent = new MenuCloseEvent(player, this);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(closeEvent);

        player = null;
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent, Menu menu, MenuItem clickedItem) {
    }
}