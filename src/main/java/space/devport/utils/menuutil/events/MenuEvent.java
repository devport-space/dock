package space.devport.utils.menuutil.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import space.devport.utils.menuutil.Menu;

public class MenuEvent extends PlayerEvent {

    // Interacted Menu
    @Getter
    private Menu menu;

    public MenuEvent(Player who, Menu menu) {
        super(who);
        this.menu = menu;
    }

    public static HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
