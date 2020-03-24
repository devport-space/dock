package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatrixItem {

    // Character to fill in.
    @Getter
    private final char character;

    // Menu items.
    @Getter
    @Setter
    private List<MenuItem> menuItems = new ArrayList<>();

    // Actual fill index, used when building a menu.
    @Getter
    @Setter
    private int index = 0;

    // Item to fill when we're out of menu items and get a request.
    // If null, nothing will fill in.
    @Getter
    @Setter
    private MenuItem filler;

    // Repeat the items and return to first when overflow occurs?
    @Getter
    @Setter
    private boolean repeat = false;

    public MatrixItem(char character) {
        this.character = character;
    }

    public MatrixItem(char character, MenuItem item) {
        this.character = character;

        menuItems.add(item);
    }

    public MatrixItem(MatrixItem item) {
        this.character = item.getCharacter();

        if (item.getFiller() != null)
            this.filler = new MenuItem(item.getFiller());

        this.repeat = item.isRepeat();

        for (MenuItem menuItem : item.getMenuItems())
            menuItems.add(new MenuItem(menuItem));
    }

    public void addItem(MenuItem item) {
        menuItems.add(new MenuItem(item));
    }

    public MenuItem getItem(String name) {
        Optional<MenuItem> opt = menuItems.stream().filter(i -> i.getName().equals(name)).findFirst();
        return opt.orElse(null);
    }

    public void removeItem(String name) {
        MenuItem item = getItem(name);

        if (item != null)
            menuItems.remove(item);
    }

    public void clear() {
        menuItems.clear();
    }

    public MenuItem getNext() {

        // Up the index if there are more items stored.
        if (!menuItems.isEmpty()) {

            // Overflow
            if (index >= menuItems.size()) {
                if (repeat)
                    index = 0;
                else
                    return filler;
            }

            MenuItem item = menuItems.get(index);

            index++;

            return item;
        } else return null;
    }

    public boolean isEmpty() {
        return menuItems.isEmpty();
    }
}