package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.itemutil.ItemBuilder;
import space.devport.utils.messageutil.MessageBuilder;
import space.devport.utils.messageutil.ParseFormat;

import java.util.*;

public class MenuBuilder {

    // A simple GUI handler

    // System name for the gui, can be used for loading/saving.
    @Getter
    @Setter
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

    // Items that should be filled in.
    @Getter
    private HashMap<Integer, MenuItem> items = new HashMap<>();

    // Items that are built in the menu.
    @Getter
    private HashMap<Integer, MenuItem> builtItems = new HashMap<>();

    // Spam prevention click delay in ticks
    @Getter
    private int clickDelay = 10;

    public MenuBuilder setClickDelay(int clickDelay) {
        this.clickDelay = clickDelay;
        return this;
    }

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
    private MessageBuilder title = new MessageBuilder("My Simple GUI");

    // Set the title
    public MenuBuilder setTitle(String title) {
        this.title = new MessageBuilder(title);
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
    private HashMap<Character, MatrixItem> itemMatrix = new HashMap<>();

    // Add an item to a corresponding character from the matrix.
    // AKA "Welcome to the matrix, Neo."
    public MenuBuilder addMatrixItem(char character, MenuItem item) {

        MatrixItem matrixItem = itemMatrix.getOrDefault(character, new MatrixItem(character));
        matrixItem.addItem(item);

        itemMatrix.put(character, matrixItem);
        return this;
    }

    public MatrixItem getMatrixItem(char character) {
        return itemMatrix.getOrDefault(character, new MatrixItem(character));
    }

    // Get matrix items assigned to a character
    public MatrixItem getMenuItems(char character) {
        return itemMatrix.getOrDefault(character, null);
    }

    // Default constructor
    public MenuBuilder(String name) {
        this.name = name;
    }

    // Copy constructor
    public MenuBuilder(MenuBuilder builder) {
        this.name = builder.getName();

        this.slots = builder.getSlots();
        this.title = builder.getTitle();

        this.items = builder.getItems();

        this.fillAll = builder.isFillAll();
        this.filler = builder.getFiller();
        this.fillerSlots = builder.getFillerSlots();

        this.buildMatrix = builder.getBuildMatrix();

        for (MatrixItem matrixItem : builder.getItemMatrix().values())
            itemMatrix.put(matrixItem.getCharacter(), new MatrixItem(matrixItem));

        this.globalFormat = new ParseFormat(builder.getGlobalFormat());
    }

    public MenuBuilder clear() {
        inventory.clear();
        inventory = null;
        return this;
    }

    // Build the gui with items, placeholders etc.
    // Is called before opening
    public MenuBuilder build() {

        String usedTitle = globalFormat.parse(title.color().toString());
        title.pull();

        // Check if the inventory title isn't too long.
        if (usedTitle.length() > 32) {
            // Cut it to 32
            usedTitle = usedTitle.substring(0, 31);

            // Send a message to console.
            DevportUtils.inst.getConsoleOutput().warn("Inventory title length for " + name + " is too long, cutting to 32.");
        }

        // Create the inventory
        inventory = Bukkit.createInventory(null, slots, usedTitle);

        // buildMatrix will be empty if we're not supposed to use it.

        char[] matrix = new char[]{};

        if (buildMatrix.length != 0)
            matrix = String.join("", buildMatrix).toCharArray();

        HashMap<Integer, MenuItem> inventoryItems = new HashMap<>(items);

        // Fill the items.
        for (int slot = 0; slot < slots; slot++) {

            // Add matrix items to items, don't overwrite
            if (buildMatrix.length != 0) {
                char matrixKey = matrix[slot];

                if (itemMatrix.containsKey(matrixKey) && !inventoryItems.containsKey(slot)) {
                    MenuItem item = itemMatrix.get(matrixKey).getNext();

                    if (item != null)
                        inventoryItems.put(slot, item);
                }
            }

            // Set the item if present
            if (inventoryItems.containsKey(slot)) {
                inventory.setItem(slot,
                        inventoryItems.get(slot)
                                .getItemBuilder()
                                .parseWith(globalFormat)
                                .build());
            } else
                // Fill if we should
                if ((fillerSlots.contains(slot) || fillAll) && filler != null) {
                    inventory.setItem(slot,
                            filler
                                    .parseWith(globalFormat)
                                    .build());
                }
        }

        // Reset matrix indexes
        for (MatrixItem matrixItem : itemMatrix.values())
            matrixItem.setIndex(0);

        // Copy to builtItems
        this.builtItems = new HashMap<>(inventoryItems);

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