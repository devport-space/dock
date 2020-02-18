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

    public Menu(MenuBuilder builder) {
        this(builder.getName(), builder.getInventory(), builder.getItems());

        this.menuBuilder = builder;
    }

    public Menu(String name, Inventory inventory, HashMap<Integer, MenuItem> items) {
        this.name = name;
        this.inventory = inventory;
        this.items = items;

        DevportUtils.inst.getMenuHandler().addMenu(this);
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
    // TODO Fix.. doesn't replace with new GlobalFormat, idk why.
    // TODO Create an original X a created thing, like in a MessageBuilder to allow replacing placeholders over again. That would fix the problem.
    public void reload() {
        menuBuilder.clear().build();
        inventory.setContents(menuBuilder.getInventory().getContents());
    }

    // Close the menu
    public void close() {
        if (player == null)
            return;

        MenuCloseEvent closeEvent = new MenuCloseEvent(player, this);
        DevportUtils.inst.getPlugin().getServer().getPluginManager().callEvent(closeEvent);

        if (!closeEvent.isCancelled()) {
            DevportUtils.inst.getMenuHandler().removeMenu(this);
            player.closeInventory();
            player = null;
        }
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent, MenuItem clickedItem) {
    }
}