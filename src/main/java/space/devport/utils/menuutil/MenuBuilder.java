package space.devport.utils.menuutil;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.messageutil.ParseFormat;
import space.devport.utils.messageutil.StringUtil;

import java.util.*;

public class MenuBuilder {

    // A simple GUI handler

    // System name for the gui, can be used for loading/saving.
    @Getter
    private String name;

    // Global parse format for all the items & the title
    @Getter
    private ParseFormat globalFormat = new ParseFormat();

    public MenuBuilder setGlobalFormat(ParseFormat format) {
        this.globalFormat = format;
        return this;
    }

    // Inventory for the GUI
    @Getter
    private Inventory inventory;

    // Items that the GUI contains, indexed by slot they occupy.
    @Getter
    private HashMap<Integer, MenuItem> items = new HashMap<>();

    // Get an item from the Menu by name
    public MenuItem getItem(String name) {
        Optional<MenuItem> opt = items.values().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();
        return opt.orElse(null);
    }

    public MenuItem getItem(int slot) {
        return items.getOrDefault(slot, null);
    }

    // Title of the GUI
    // Is colored and parsed when built
    @Getter
    private String title = "My Simple GUI";

    // Set the title
    public MenuBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    // Slots
    @Getter
    private int slots = 9;

    // Set slots
    public MenuBuilder setSlots(int slots) {
        this.slots = slots;
        return this;
    }

    // Slots to fill
    @Getter
    private List<Integer> fillerSlots = new ArrayList<>();

    // Set slots to fill
    public MenuBuilder setFillSlots(int[] slots) {
        Arrays.stream(slots).forEach(slot -> fillerSlots.add(slot));
        return this;
    }

    public MenuBuilder setFillSlots(List<Integer> slots) {
        this.fillerSlots = slots;
        return this;
    }

    // Fill all free slots on build?
    @Getter
    private boolean fillAll = false;

    // Fill all slots that are empty
    // Other items should be set before this action
    public MenuBuilder setFillAll(boolean fillAll) {
        this.fillAll = fillAll;
        return this;
    }

    // Filler item
    @Getter
    private ItemBuilder filler;

    // Set the filler ItemBuilder
    public MenuBuilder setFiller(ItemBuilder filler) {
        this.filler = filler;
        return this;
    }

    // The inventory matrix
    @Getter
    private String[] buildMatrix = new String[]{};

    public MenuBuilder setBuildMatrix(String[] buildMatrix) {
        this.buildMatrix = buildMatrix;
        return this;
    }

    // Matrix of items
    @Getter
    private HashMap<Character, MenuItem> itemMatrix = new HashMap<>();

    // Set an item to a corresponding character from the matrix.
    // AKA "Welcome to the matrix, Neo."
    public MenuBuilder setMatrixItem(char character, MenuItem item) {
        itemMatrix.put(character, item);
        return this;
    }

    // Default constructor
    public MenuBuilder(String name) {
        this.name = name;
    }

    // Basically copy another MenuBuilder.
    public MenuBuilder(MenuBuilder builder) {
        this.name = builder.getName();
        this.items = builder.getItems();
        this.fillAll = builder.isFillAll();
        this.buildMatrix = builder.getBuildMatrix();
        this.filler = builder.getFiller();
        this.fillerSlots = builder.getFillerSlots();
        this.itemMatrix = builder.getItemMatrix();
        this.slots = builder.getSlots();
        this.title = builder.getTitle();
        this.globalFormat = builder.getGlobalFormat();
        this.inventory = builder.getInventory();
    }

    public MenuBuilder clear() {
        inventory.clear();
        inventory = null;
        return this;
    }

    // Build the gui with items, placeholders etc.
    // Is called before opening
    public MenuBuilder build() {

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

        // buildMatrix will be empty if we're not supposed to use it.

        char[] matrix = new char[]{};

        if (buildMatrix.length != 0)
            matrix = String.join("", buildMatrix).toCharArray();

        // Fill the items.
        for (int slot = 0; slot < slots; slot++) {

            // Add matrix items to items, don't overwrite
            if (buildMatrix.length != 0) {
                char matrixKey = matrix[slot];

                if (itemMatrix.containsKey(matrixKey) && !items.containsKey(slots))
                    items.put(slot, itemMatrix.get(matrixKey));
            }

            // Set the item if present
            if (items.containsKey(slot)) {
                inventory.setItem(slot,
                        items.get(slot).getItemBuilder()
                                .parseWith(globalFormat)
                                .build());
            }

            // Fill if we should
            if ((fillerSlots.contains(slot) || fillAll) && !items.containsKey(slot) && filler != null) {
                inventory.setItem(slot,
                        filler
                                .parseWith(globalFormat)
                                .build());
            }
        }

        return this;
    }

    // Set item array to gui
    public MenuBuilder setItems(MenuItem[] items) {
        Arrays.asList(items).forEach(this::setItem);
        return this;
    }

    // Set new SimpleItem directly to it's slot
    // Every adding method is redirected here. Idk why, I just like it.
    public MenuBuilder setItem(MenuItem item) {
        items.put(item.getSlot(), item);
        return this;
    }

    // Set Item to multiple slots
    public MenuBuilder setItem(MenuItem item, int[] slots) {
        for (int slot : slots) {
            item.setSlot(slot);
            setItem(item);
        }

        return this;
    }

    // Set new item to GUI slot, name is always lowercase.
    public MenuBuilder setItem(ItemBuilder itemBuilder, String name, int slot) {
        setItem(new MenuItem(itemBuilder, name, slot));
        return this;
    }

    // Add new item to the next free slot
    public MenuBuilder addItem(ItemBuilder item, String name) {
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
}