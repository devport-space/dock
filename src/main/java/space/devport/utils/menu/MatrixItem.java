package space.devport.utils.menu;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Keeps an ordered list of items under a character.
 */
public class MatrixItem {

    @Getter
    private final char character;

    @Getter
    @Setter
    private List<MenuItem> menuItems = new LinkedList<>();

    @Getter
    @Setter
    private MenuItem filler;

    @Getter
    @Setter
    private int index = 0;

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