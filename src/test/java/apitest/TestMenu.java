package apitest;

import org.bukkit.event.inventory.InventoryClickEvent;
import space.devport.utils.menuutil.Menu;
import space.devport.utils.menuutil.MenuBuilder;
import space.devport.utils.menuutil.MenuItem;

public class TestMenu extends Menu {

    public TestMenu(MenuBuilder builder) {
        super(builder);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent, Menu menu, MenuItem clickedItem) {

    }
}