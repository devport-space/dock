package space.devport.dock.menu.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.item.ItemPrefab;
import space.devport.dock.item.impl.PrefabFactory;
import space.devport.dock.menu.events.ItemClick;
import space.devport.dock.struct.Rewards;


public class MenuItem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int slot;

    @Getter
    @Setter
    private ItemPrefab prefab;

    @Getter
    @Setter
    private Consumer<ItemClick> clickAction;

    @Getter
    @Setter
    private Rewards rewards;

    @Getter
    @Setter
    private boolean cancelClick = true;

    @Getter
    @Setter
    private boolean clickable = true;

    public MenuItem(IDockedPlugin plugin, @NotNull ItemPrefab prefab, @NotNull String name, int slot) {
        this.name = name;

        this.slot = slot;
        this.prefab = prefab;
        this.rewards = new Rewards(plugin);
    }

    public MenuItem(@NotNull MenuItem menuItem) {
        this.name = menuItem.getName();
        this.slot = menuItem.getSlot();
        this.clickable = menuItem.isClickable();

        this.prefab = PrefabFactory.of(menuItem.getPrefab());

        this.rewards = menuItem.getRewards();
        this.cancelClick = menuItem.isCancelClick();
        this.clickAction = menuItem.getClickAction();
    }
}