package space.devport.utils.menu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import space.devport.utils.DevportUtils;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.menu.item.MatrixItem;
import space.devport.utils.menu.item.MenuItem;
import space.devport.utils.text.Placeholders;
import space.devport.utils.text.message.CachedMessage;

import java.util.HashMap;
import java.util.Optional;

@NoArgsConstructor
public class MenuBuilder {

    @Getter
    private String name;

    @Getter
    private CachedMessage title = new CachedMessage("My Simple GUI");

    @Getter
    private int slots = 9;

    @Getter
    private Placeholders placeholders = new Placeholders();

    @Getter
    private Inventory inventory;

    @Getter
    private HashMap<Integer, MenuItem> items = new HashMap<>();

    @Getter
    private int clickDelay = 10;

    @Getter
    private ItemBuilder filler;

    @Getter
    private String[] buildMatrix = new String[]{};

    @Getter
    private final HashMap<Character, MatrixItem> itemMatrix = new HashMap<>();

    public MenuBuilder(String name, MenuBuilder builder) {
        this.name = name;
        this.slots = builder.getSlots();
        this.title = builder.getTitle();
        this.items = builder.getItems();
        this.filler = builder.getFiller();
        this.buildMatrix = builder.getBuildMatrix();
        this.clickDelay = builder.getClickDelay();

        for (MatrixItem matrixItem : builder.getItemMatrix().values())
            itemMatrix.put(matrixItem.getCharacter(), new MatrixItem(matrixItem));

        this.placeholders = new Placeholders(builder.getPlaceholders());
    }

    public MenuBuilder clear() {
        if (inventory != null) {
            inventory.clear();
            inventory = null;
        }

        items.clear();
        return this;
    }

    public Menu build() {

        // Title

        String title = placeholders.parse(this.title.color().toString());
        this.title.pull();

        if (title.length() > 32) {
            title = title.substring(0, 31);
            DevportUtils.getInstance().getConsoleOutput().warn("Inventory title " + this.title + " is too long, cutting to 32.");
        }

        inventory = Bukkit.createInventory(null, slots, title);

        // Build scheme

        if (buildMatrix.length == 0) {
            DevportUtils.getInstance().getConsoleOutput().err("Could not create menu " + name + ", there's not matrix.");
            return null;
        }

        char[] matrix = String.join("", buildMatrix).toCharArray();

        HashMap<Integer, MenuItem> inventoryItems = new HashMap<>(items);

        // Fill items

        for (int slot = 0; slot < slots; slot++) {

            char matrixKey = matrix[slot];

            MenuItem item = itemMatrix.get(matrixKey).getNext();

            if (item == null) {
                DevportUtils.getInstance().getConsoleOutput().debug("Item for character " + matrixKey + " is null, skipping it.");
                continue;
            }

            this.items.put(slot, item);

            if (inventoryItems.containsKey(slot)) {
                inventory.setItem(slot,
                        inventoryItems.get(slot)
                                .getItemBuilder()
                                .parseWith(placeholders)
                                .build());
            } else {
                if (filler != null) {
                    inventory.setItem(slot,
                            filler
                                    .parseWith(placeholders)
                                    .build());
                }
            }
        }

        for (MatrixItem matrixItem : itemMatrix.values()) matrixItem.setIndex(0);
        this.items = new HashMap<>(inventoryItems);

        return new Menu(name, this);
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
        this.title = new CachedMessage(title);
        return this;
    }

    public MenuBuilder setPlaceholders(Placeholders format) {
        this.placeholders = new Placeholders(format);
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

    // Set the filler ItemBuilder
    public MenuBuilder setFiller(ItemBuilder filler) {
        this.filler = new ItemBuilder(filler);
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
            MatrixItem newItem = new MatrixItem(itemMatrix.get(matrixItem.getCharacter()));
            matrixItem.getMenuItems().forEach(i -> newItem.addItem(new MenuItem(i)));

            setMatrixItem(newItem);
        } else setMatrixItem(matrixItem);
        return this;
    }

    // Set a Matrix item to the menu
    public MenuBuilder setMatrixItem(MatrixItem matrixItem) {
        itemMatrix.put(matrixItem.getCharacter(), new MatrixItem(matrixItem));
        return this;
    }

    // Remove a matrix item from the menu
    public MenuBuilder removeMatrixItem(char character) {
        itemMatrix.remove(character);
        return this;
    }

    // Get item from the matrix
    public MatrixItem getMatrixItem(char character) {
        return itemMatrix.getOrDefault(character, null);
    }

    // Get an item from the Menu by name
    public MenuItem getItem(String name) {
        Optional<MenuItem> opt = items.values().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();
        return opt.orElse(null);
    }

    public MenuItem getItem(int slot) {
        return items.getOrDefault(slot, null);
    }
}