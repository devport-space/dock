package space.devport.utils.menu.events;

import org.bukkit.entity.Player;
import space.devport.utils.menu.Menu;

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