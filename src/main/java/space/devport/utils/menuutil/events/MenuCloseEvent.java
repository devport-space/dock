package space.devport.utils.menuutil.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import space.devport.utils.menuutil.Menu;

public class MenuCloseEvent extends MenuEvent implements Cancellable {

    @Getter
    @Setter
    private boolean cancelled;

    public MenuCloseEvent(Player who, Menu menu) {
        super(who, menu);
    }
}
