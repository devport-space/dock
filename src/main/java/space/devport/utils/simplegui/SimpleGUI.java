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

import java.util.Arrays;
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
    // Is indexed from 1 instead of 0
    private HashMap<Integer, SimpleItem> items = new HashMap<>();

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
    public void build() {
    }

    // Reload inventory contents without reopening
    public void reload() {
    }

    // Set item array to gui
    public SimpleGUI setItems(SimpleItem[] items) {
        Arrays.asList(items).forEach(i -> this.items.put(i.getSlot(), i));
        return this;
    }

    // Set new SimpleItem directly to it's slot
    public SimpleGUI setItem(SimpleItem item) {
        items.put(item.getSlot(), item);
        return this;
    }

    // Set new item to GUI slot, name is always lowercase.
    public SimpleGUI setItem(ItemBuilder itemBuilder, String name, int slot) {
        items.put(slot, new SimpleItem(itemBuilder, name, slot));
        return this;
    }

    // Add new item to the next free slot
    public SimpleGUI addItem(ItemBuilder item, String name) {
        int slot = nextFree();

        // Add only if there's a free slot
        if (slot != -1)
            items.put(slot, new SimpleItem(item, name, slot));

        return this;
    }

    // Return next free slot
    public int nextFree() {
        int n = 1;

        for (int slot : items.keySet()) {
            if (n != slot)
                return n;
            n++;
        }

        return -1;
    }

    // Click listener, throws SimpleItemClickEvent
    @EventHandler
    public void onClick(InventoryClickEvent e) {

    }
}