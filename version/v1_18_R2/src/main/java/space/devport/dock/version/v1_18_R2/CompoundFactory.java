package space.devport.dock.version.v1_18_R2;

import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import space.devport.dock.version.api.ICompound;
import space.devport.dock.version.api.ICompoundFactory;

public class CompoundFactory implements ICompoundFactory {

    @Override
    public ICompound create() {
        return new Compound();
    }

    @Override
    public ICompound of(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
        return new Compound(craftItemStack);
    }
}
