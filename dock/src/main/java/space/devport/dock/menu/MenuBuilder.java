package space.devport.dock.menu;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.item.ItemPrefab;
import space.devport.dock.menu.item.MatrixItem;
import space.devport.dock.menu.item.MenuItem;
import space.devport.dock.text.placeholders.Placeholders;
import space.devport.dock.text.message.CachedMessage;
import space.devport.dock.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log
public class MenuBuilder implements Cloneable {

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
    private String[] buildMatrix = new String[]{};

    @Getter
    private boolean constructed = false;

    @Getter
    private final HashMap<Character, MatrixItem> itemMatrix = new HashMap<>();

    @Getter
    private final IDockedPlugin plugin;

    public MenuBuilder(String name, MenuBuilder builder) {
        this(builder);
        this.name = name;
    }

    public MenuBuilder(IDockedPlugin plugin) {
        this.plugin = plugin;
    }

    private MenuBuilder(MenuBuilder builder) {
        this.slots = builder.getSlots();
        this.title = builder.getTitle().clone();

        this.buildMatrix = builder.getBuildMatrix().clone();
        this.clickDelay = builder.getClickDelay();

        for (MatrixItem matrixItem : builder.getItemMatrix().values())
            itemMatrix.put(matrixItem.getCharacter(), new MatrixItem(matrixItem));

        this.placeholders = builder.getPlaceholders().clone();
        this.plugin = builder.getPlugin();
    }

    public MenuBuilder clear() {
        if (inventory != null) {
            inventory.clear();
            inventory = null;
        }

        items.clear();
        return this;
    }

    /*
     * Place items by matrix into the items list.
     */
    public MenuBuilder construct() {

        // Slots

        int required = buildMatrix.length * 9;
        if (required > this.slots) this.slots = required;

        // Title

        String title = StringUtil.color(placeholders.parse(this.title.parse().toString()));
        this.title.pull();

        if (title.length() > 32) {
            title = title.substring(0, 31);
            log.warning("Inventory title " + this.title + " is too long, cutting to 32.");
        }

        inventory = Bukkit.createInventory(null, slots, title);

        // Build scheme

        if (buildMatrix.length == 0) {
            log.severe("Could not construct menu " + name + ", there's no matrix.");
            return null;
        }

        char[] matrix = String.join("", buildMatrix).toCharArray();

        HashMap<Integer, MenuItem> inventoryItems = new HashMap<>();

        // Fill items

        for (int slot = 0; slot < slots; slot++) {

            if (matrix.length <= slot) break;

            char matrixKey = matrix[slot];

            if (!itemMatrix.containsKey(matrixKey)) continue;

            MenuItem item = itemMatrix.get(matrixKey).getNext();

            if (item == null) continue;

            inventoryItems.put(slot, item);
        }

        for (MatrixItem matrixItem : itemMatrix.values()) matrixItem.setIndex(0);

        this.items = inventoryItems;

        this.constructed = true;
        return this;
    }

    public Menu build() {

        // Menu has to be at least once constructed
        if (!constructed)
            construct();

        // Fill items

        for (Map.Entry<Integer, MenuItem> item : this.items.entrySet()) {
            ItemPrefab prefab = item.getValue().getPrefab();
            prefab.getPlaceholders().copy(placeholders);
            inventory.setItem(item.getKey(), prefab.build());
        }

        Menu menu = new Menu(name, this);
        menu.setInventory(inventory);
        menu.setItems(this.items);
        return menu;
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

    public MenuBuilder title(String title) {
        this.title = new CachedMessage(title);
        return this;
    }

    public MenuBuilder slots(int slots) {
        this.slots = slots;
        return this;
    }

    public MenuBuilder placeholders(Placeholders placeholders) {
        this.placeholders = Placeholders.of(placeholders);
        return this;
    }

    public MenuBuilder clickDelay(int clickDelay) {
        this.clickDelay = clickDelay;
        return this;
    }

    public MenuBuilder buildMatrix(String[] buildMatrix) {
        this.buildMatrix = buildMatrix;
        return this;
    }

    public MenuBuilder setItem(MenuItem item) {
        item = new MenuItem(item);
        items.put(item.getSlot(), item);
        return this;
    }

    public MenuBuilder setItem(ItemPrefab prefab, String name, int slot) {
        return setItem(new MenuItem(plugin, prefab, name, slot));
    }

    public MenuBuilder addItem(ItemPrefab prefab, String name) {
        MenuItem menuItem = new MenuItem(plugin, prefab, name, nextFree());
        return setItem(menuItem);
    }

    // Add item to the next free slot
    public MenuBuilder addItem(MenuItem item) {
        item = new MenuItem(item);
        item.setSlot(nextFree());
        return setItem(item);
    }

    public MenuBuilder addMatrixItem(char character, MenuItem item) {
        return addMatrixItem(new MatrixItem(character, item));
    }

    public MenuBuilder addMatrixItem(MatrixItem matrixItem) {
        if (itemMatrix.containsKey(matrixItem.getCharacter())) {

            MatrixItem newItem = new MatrixItem(itemMatrix.get(matrixItem.getCharacter()));
            matrixItem.getMenuItems().forEach(i -> newItem.addItem(new MenuItem(i)));

            setMatrixItem(newItem);
        } else setMatrixItem(matrixItem);
        return this;
    }

    public MenuBuilder setMatrixItem(MatrixItem matrixItem) {
        itemMatrix.put(matrixItem.getCharacter(), new MatrixItem(matrixItem));
        return this;
    }

    public MenuBuilder removeMatrixItem(char character) {
        itemMatrix.remove(character);
        return this;
    }

    public MatrixItem getMatrixItem(char character) {
        return itemMatrix.getOrDefault(character, null);
    }

    public MenuItem getItem(String name) {
        Optional<MenuItem> opt = items.values().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findAny();
        return opt.orElse(null);
    }

    public MenuItem getItem(int slot) {
        return items.getOrDefault(slot, null);
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MenuBuilder clone() {
        return new MenuBuilder(this);
    }
}