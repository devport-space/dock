package space.devport.utils.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportPlugin;
import space.devport.utils.menu.events.ItemClick;
import space.devport.utils.menu.events.MenuCloseEvent;
import space.devport.utils.menu.events.MenuOpenEvent;
import space.devport.utils.menu.item.MenuItem;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Menu implements MenuListener {

    @Getter
    private final UUID uniqueID;

    // Name of the TYPE of the menu.
    @Getter
    public String name;

    @Getter
    @Setter
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

    public Menu(String name) {
        this(name, new MenuBuilder());
    }

    public Menu(String name, MenuBuilder builder) {
        this.uniqueID = UUID.randomUUID();
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
        Bukkit.getPluginManager().callEvent(openEvent);

        if (!openEvent.isCancelled()) {
            this.player = player;

            DevportPlugin.getInstance().getManager(MenuManager.class).addMenu(this);

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

        items = menuBuilder.construct().build().getItems();
        inventory.setContents(menuBuilder.getInventory().getContents());
    }

    // Close the menu
    public void close() {
        if (player == null || !open)
            return;

        // Throw close event
        MenuCloseEvent closeEvent = new MenuCloseEvent(player, this);
        Bukkit.getPluginManager().callEvent(closeEvent);

        if (!closeEvent.isCancelled()) {

            open = false;

            player.closeInventory();

            DevportPlugin.getInstance().getManager(MenuManager.class).removeMenu(this);

            onClose();

            player = null;
        }
    }

    public void runClick(InventoryClickEvent clickEvent, MenuItem clickedItem) {
        if (onClick(clickEvent, clickedItem) && clickedItem.getClickAction() != null)
            clickedItem.getClickAction().accept(new ItemClick((Player) clickEvent.getWhoClicked(), clickedItem, this));
    }

    /**
     * Return whether or not to run an action assigned to the item that was clicked.
     */
    @Override
    public boolean onClick(InventoryClickEvent clickEvent, MenuItem clickedItem) {
        if (clickedItem.getRewards() != null) clickedItem.getRewards().give((Player) clickEvent.getWhoClicked());
        return true;
    }

    @Override
    public void onClose() {
    }

    @Override
    public void onOpen() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return uniqueID.equals(menu.uniqueID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueID);
    }
}