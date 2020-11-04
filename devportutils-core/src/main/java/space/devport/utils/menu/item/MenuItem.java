package space.devport.utils.menu.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.item.ItemBuilder;
import space.devport.utils.menu.events.ItemClick;
import space.devport.utils.struct.Rewards;


public class MenuItem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private int slot;

    @Getter
    @Setter
    private ItemBuilder itemBuilder;

    @Getter
    @Setter
    private Consumer<ItemClick> clickAction;

    @Getter
    @Setter
    private Rewards rewards = new Rewards();

    @Getter
    @Setter
    private boolean cancelClick = true;

    @Getter
    @Setter
    private boolean clickable = true;

    public MenuItem(@NotNull ItemBuilder itemBuilder, @NotNull String name, int slot) {
        this.name = name;

        this.slot = slot;
        this.itemBuilder = itemBuilder;
    }

    public MenuItem(@NotNull MenuItem item) {
        this.name = item.getName();
        this.slot = item.getSlot();
        this.clickable = item.isClickable();

        this.itemBuilder = new ItemBuilder(item.getItemBuilder());

        this.rewards = item.getRewards();
        this.cancelClick = item.isCancelClick();
        this.clickAction = item.getClickAction();
    }
}