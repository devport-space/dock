package space.devport.utils.version.api;

import org.bukkit.inventory.ItemStack;

public interface ICompoundFactory {

    ICompound of(ItemStack itemStack);
}
