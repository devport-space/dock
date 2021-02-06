package space.devport.dock.menu.events;

import org.bukkit.entity.Player;
import space.devport.dock.menu.Menu;

/**
 * Event fired when a Devport-handled Menu is opened.
 *
 * @author Devport Team
 */
public class MenuOpenEvent extends MenuEvent {
    public MenuOpenEvent(Player who, Menu menu) {
        super(who, menu);
    }
}