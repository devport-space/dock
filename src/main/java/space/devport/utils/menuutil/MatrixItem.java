package space.devport.utils.menuutil;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MatrixItem {

    // Menu items.
    @Getter
    @Setter
    private List<MenuItem> menuItems = new ArrayList<>();

    // Character to fill in.
    @Getter
    private char character;

    // Actual fill index, used when building a menu.
    @Getter
    @Setter
    private int index = 0;

    public MatrixItem(char character) {
        this.character = character;
    }

    public MatrixItem addItem(MenuItem item) {
        menuItems.add(item);
        return this;
    }
}