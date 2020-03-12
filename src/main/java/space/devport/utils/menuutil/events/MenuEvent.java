package space.devport.utils.menuutil.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import space.devport.utils.menuutil.Menu;

/**
 * Base for all Menu events.
 *
 * @author Devport Team
 */
public class MenuEvent extends PlayerEvent implements Cancellable {

    // Interacted Menu
    @Getter
    private final Menu menu;

    // Cancelled?
    @Getter
    @Setter
    private boolean cancelled = false;

    public MenuEvent(Player who, Menu menu) {
        super(who);
        this.menu = menu;
    }

    public static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}