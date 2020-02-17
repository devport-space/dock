package space.devport.utils.simplegui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.messageutil.ParseFormat;
import space.devport.utils.messageutil.StringUtil;

import java.util.Arrays;
import java.util.HashMap;

public class SimpleGUI implements Listener {

    // A simple GUI handler
    // TODO Add filler item
    // TODO Add gui loading/saving from/to yaml
    // TODO Add chaining where missing
    // TODO Change first item slot to 1
    // TODO Make it possible to set an item to more slots
    // TODO Add matrix inventory support

    // System name for the gui, can be used for loading/saving.
    @Getter
    @Setter
    private String name;

    // Global parse format for all the items & the title
    @Getter
    @Setter
    private ParseFormat globalFormat = new ParseFormat();

    // Items that the GUI contains, indexed by slot they occupy.
    // Is indexed from 1 instead of 0
    @Getter
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

    // For whom the inventory is open, null if closed.
    @Getter
    @Setter
    private Player player;

    public SimpleGUI(String name) {
        this.name = name;

        DevportUtils.inst.getGuiHandler().addGUI(this);
    }

    // Open the gui for a player
    public void open(Player player) {
        build();
        player.openInventory(inventory);
        this.player = player;
    }

    // Build the gui with items, placeholders etc.
    // Is called before opening
    public void build() {

        String usedTitle = title;

        // Check if the inventory title isn't too long.
        if (title.length() > 32) {
            // Cut it to 32
            usedTitle = usedTitle.substring(0, 31);

            // Send a message to console.
            DevportUtils.inst.getConsoleOutput().warn("Inventory title length for " + name + " is too long, cutting to 32.");
        }

        // Create the inventory
        inventory = Bukkit.createInventory(null, slots, StringUtil.color(globalFormat.parse(usedTitle)));

        for (SimpleItem item : items.values()) {

            // Build the item and add to GUI
            inventory.setItem(item.getSlot(),
                    item.getItemBuilder()
                            .parseFormat(globalFormat)
                            .build());
        }
    }

    // Reload inventory contents without reopening
    // TODO
    public void reload() {

    }

    public void close() {
        player.closeInventory();
        player = null;
    }

    // Set item array to gui
    public SimpleGUI setItems(SimpleItem[] items) {
        Arrays.asList(items).forEach(this::setItem);
        return this;
    }

    // Set new SimpleItem directly to it's slot
    // Every adding method is redirected here. Idk why, I just like it.
    public SimpleGUI setItem(SimpleItem item) {
        items.put(item.getSlot(), item);
        return this;
    }

    // Set new item to GUI slot, name is always lowercase.
    public SimpleGUI setItem(ItemBuilder itemBuilder, String name, int slot) {
        setItem(new SimpleItem(itemBuilder, name, slot));
        return this;
    }

    // Add new item to the next free slot
    public SimpleGUI addItem(ItemBuilder item, String name) {
        int slot = nextFree();

        // Add only if there's a free slot
        if (slot != -1)
            setItem(item, name, slot);

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

    // Method to override when using this system
    public void onClick(InventoryClickEvent clickEvent, SimpleGUI gui, SimpleItem item) {
    }
}