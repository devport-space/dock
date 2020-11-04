package space.devport.utils.menu.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import space.devport.utils.menu.Menu;
import space.devport.utils.menu.item.MenuItem;

@RequiredArgsConstructor
public class ItemClick {

    @Getter
    private final Player player;

    @Getter
    private final MenuItem clickedItem;

    @Getter
    private final Menu menu;
}