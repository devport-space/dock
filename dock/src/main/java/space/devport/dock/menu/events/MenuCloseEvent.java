package space.devport.dock.menu.events;

import org.bukkit.entity.Player;
import space.devport.dock.menu.Menu;

/**
 * Event thrown when a Devport-handled menu is closed.
 *
 * @author Devport Team
 */
public class MenuCloseEvent extends MenuEvent {
    public MenuCloseEvent(Player who, Menu menu) {
        super(who, menu);
    }
}