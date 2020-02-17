package space.devport.utils.menuutil.events;

import org.bukkit.entity.Player;
import space.devport.utils.menuutil.Menu;

public class MenuOpenEvent extends MenuEvent {

    public MenuOpenEvent(Player who, Menu menu) {
        super(who, menu);
    }
}
