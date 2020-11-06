package space.devport.utils.version.api;

import org.bukkit.inventory.ItemStack;

public interface ICompoundFactory {

    ICompound create();

    ICompound of(ItemStack itemStack);
}
