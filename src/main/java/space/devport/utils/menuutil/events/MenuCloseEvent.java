package space.devport.utils.menuutil.events;

import org.bukkit.entity.Player;
import space.devport.utils.menuutil.Menu;

public class MenuCloseEvent extends MenuEvent {

    public MenuCloseEvent(Player who, Menu menu) {
        super(who, menu);
    }
}
