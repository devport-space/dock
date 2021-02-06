package space.devport.dock.menu.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import space.devport.dock.menu.Menu;
import space.devport.dock.menu.item.MenuItem;

@RequiredArgsConstructor
public class ItemClick {

    @Getter
    private final Player player;

    @Getter
    private final MenuItem clickedItem;

    @Getter
    private final Menu menu;
}