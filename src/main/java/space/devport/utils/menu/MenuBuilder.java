package space.devport.utils.menu;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.text.CacheMessage;
import space.devport.utils.text.Placeholders;

import java.util.*;

public class MenuBuilder {

    // A simple GUI Builder

    // TODO Move Matrix & stable items to different builders

    // Global parse format for all the items & the title
    @Getter
    private Placeholders globalFormat = new Placeholders();

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

    // Title of the GUI
    // Is colored and parsed when built
    @Getter
    private CacheMessage title = new CacheMessage("My Simple GUI");

    // Slots
    @Getter
    private int slots = 9;

    // Slots to fill
    @Getter
    private List<Integer> fillerSlots = new ArrayList<>();

    // Fill all free slots on build?
    @Getter
    private boolean fillAll = false;

    // Filler item
    @Getter
    private ItemBuilder filler;

    // The inventory matrix
    @Getter
    private String[] buildMatrix = new String[]{};

    // Matrix of items
    @Getter
    private final HashMap<Character, MatrixItem> itemMatrix = new HashMap<>();

    // Default constructor
    public MenuBuilder() {
    }

    // Copy constructor
    public MenuBuilder(MenuBuilder builder) {
        this.slots = builder.getSlots();
        this.title = builder.getTitle();

        this.items = builder.getItems();

        this.fillAll = builder.isFillAll();
        this.filler = builder.getFiller();
        this.fillerSlots = builder.getFillerSlots();

        this.buildMatrix = builder.getBuildMatrix();

        this.clickDelay = builder.getClickDelay();

        for (MatrixItem matrixItem : builder.getItemMatrix().values())
            itemMatrix.put(matrixItem.getCharacter(), new MatrixItem(matrixItem));

        this.globalFormat = new Placeholders(builder.getGlobalFormat());
    }

    // Clear the builder
    public MenuBuilder clear() {
        if (inventory != null) {
            inventory.clear();
            inventory = null;
        }

        builtItems.clear();
        return this;
    }

    // Build the gui with items, placeholders etc.
    // Is called before opening
    // TODO Output inventory
    public MenuBuilder build() {

        String usedTitle = globalFormat.parse(title.color().toString());
        title.pull();

        // Check if the inventory title isn't too long.
        if (usedTitle.length() > 32) {
            // Cut it to 32
            usedTitle = usedTitle.substring(0, 31);

            // Send a message to console.
            DevportUtils.getInstance().getConsoleOutput().warn("Inventory title " + title + " is too long, cutting to 32.");
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

    // Return next free slot in the menu
    public int nextFree() {
        int n = 1;

        for (int slot : items.keySet()) {
            if (n != slot)
                return n;
            n++;
        }

        return -1;
    }

    // ----------------- Chain Setters -----------------

    // Set the title
    public MenuBuilder setTitle(String title) {
        this.title = new CacheMessage(title);
        return this;
    }

    public MenuBuilder setGlobalFormat(Placeholders format) {
        this.globalFormat = new Placeholders(format);
        return this;
    }

    public MenuBuilder setClickDelay(int clickDelay) {
        this.clickDelay = clickDelay;
        return this;
    }

    // Set slots
    public MenuBuilder setSlots(int slots) {
        this.slots = slots;
        return this;
    }

    public MenuBuilder setBuildMatrix(String[] buildMatrix) {
        this.buildMatrix = buildMatrix;
        return this;
    }

    // Fill all slots that are empty
    // Other items should be set before this action
    public MenuBuilder setFillAll(boolean fillAll) {
        this.fillAll = fillAll;
        return this;
    }

    // Set slots to fill
    public MenuBuilder setFillSlots(int[] slots) {
        Arrays.stream(slots).forEach(slot -> fillerSlots.add(slot));
        return this;
    }

    public MenuBuilder setFillSlots(List<Integer> slots) {
        this.fillerSlots = slots;
        return this;
    }

    // Set the filler ItemBuilder
    public MenuBuilder setFiller(ItemBuilder filler) {
        this.filler = filler;
        return this;
    }

    // Set new SimpleItem directly to it's slot
    // Every adding method is redirected here. Idk why, I just like it.
    public MenuBuilder setItem(MenuItem item) {
        item = new MenuItem(item);
        items.put(item.getSlot(), item);
        return this;
    }

    // Set new item to GUI slot, name is always lowercase.
    public MenuBuilder setItem(ItemBuilder itemBuilder, String name, int slot) {
        return setItem(new MenuItem(itemBuilder, name, slot));
    }

    // Add new item to the next free slot
    public MenuBuilder addItem(ItemBuilder item, String name) {
        int slot = nextFree();

        // Add only if there's a free slot
        if (slot != -1)
            setItem(item, name, slot);

        return this;
    }

    // Add item to the next free slot
    public MenuBuilder addItem(MenuItem item) {
        item = new MenuItem(item);
        item.setSlot(nextFree());
        setItem(item);
        return this;
    }

    // Add an item to a corresponding character from the matrix.
    public MenuBuilder addMatrixItem(char character, MenuItem item) {
        return addMatrixItem(new MatrixItem(character, item));
    }

    // Add a matrix item to the matrix, if it's already there, merge them
    public MenuBuilder addMatrixItem(MatrixItem matrixItem) {
        if (itemMatrix.containsKey(matrixItem.getCharacter())) {

            // Merge them
            MatrixItem newItem = new MatrixItem(matrixItem);
            matrixItem.getMenuItems().forEach(i -> newItem.addItem(new MenuItem(i)));

            setMatrixItem(newItem);
        } else setMatrixItem(matrixItem);
        return this;
    }

    // Set a Matrix item to the menu
    public MenuBuilder setMatrixItem(MatrixItem matrixItem) {
        itemMatrix.put(matrixItem.getCharacter(), matrixItem);
        return this;
    }

    // Remove a matrix item from the menu
    public MenuBuilder removeMatrixItem(char character) {
        itemMatrix.remove(character);
        return this;
    }

    // Get item from the matrix
    public MatrixItem getMatrixItem(char character) {
        return itemMatrix.getOrDefault(character, new MatrixItem(character));
    }

    // --------------- Getters --------------------

    // Get an item from the Menu by name
    public MenuItem getItem(String name) {
        Optional<MenuItem> opt = items.values().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();
        return opt.orElse(null);
    }

    public MenuItem getItem(int slot) {
        return items.getOrDefault(slot, null);
    }
}