package space.devport.utils.simplegui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.messageutil.ParseFormat;

import java.util.HashMap;

public class SimpleGUI implements Listener {

    // A simple GUI handler

    // System name for the gui.
    @Getter
    @Setter
    private String name;

    // Global parse format for all the items & the title
    @Getter
    @Setter
    private ParseFormat globalFormat;

    // Items that the GUI contains, indexed by slot they occupy.
    private HashMap<Integer, ItemBuilder> items = new HashMap<>();

    // Title of the GUI
    // Is colored and parsed when built
    @Getter
    @Setter
    private String title = "My Simple GUI";

    // Slots
    @Getter
    @Setter
    private int slots = 9;

    // Inventory for the GUI
    @Getter
    private Inventory inventory;

    public SimpleGUI(String name) {
        this.name = name;
    }

    // Open the gui for a player
    public void open(Player player) {
        build();
        player.openInventory(inventory);
    }

    // Build the gui with items, placeholders etc.
    // Is called before opening
    public void build() {}

    // Add item to GUI
    public SimpleGUI addItem(ItemBuilder item, int slot) {
        items.put(slot, item);
        return this;
    }

    // Click listener, throws SimpleItemClickEvent
    @EventHandler
    public void onClick(InventoryClickEvent e) {

    }
}