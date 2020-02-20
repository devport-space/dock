package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    public MatrixItem(char character) {
        this.character = character;
    }

    public MatrixItem(char character, MenuItem item) {
        this.character = character;
        menuItems.add(item);
    }

    public MatrixItem addItem(MenuItem item) {
        menuItems.add(item);
        return this;
    }

    public MenuItem getNext() {
        // Up the index if there are more items.if (menuItems.size() > 1)index++;return menuItems.get(index);}
        if (menuItems.size() > 1)
            index++;

        return menuItems.get(index);
    }

    public boolean isEmpty() {
        return menuItems.isEmpty();
    }
}