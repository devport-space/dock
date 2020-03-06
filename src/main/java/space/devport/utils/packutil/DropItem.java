package space.devport.utils.packutil;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.itemutil.ItemBuilder;

public class DropItem {

    @Getter
    @Setter
    private ItemBuilder itemBuilder;

    @Getter
    @Setter
    private Amount amount = new Amount(1);

    public DropItem(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    public DropItem(ItemBuilder itemBuilder, Amount amount) {
        this.itemBuilder = itemBuilder;
        this.amount = amount;
    }
}
