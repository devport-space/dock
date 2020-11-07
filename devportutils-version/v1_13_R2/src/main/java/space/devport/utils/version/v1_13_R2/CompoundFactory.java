package space.devport.utils.version.v1_13_R2;

import net.minecraft.server.v1_13_R2.ItemStack;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import space.devport.utils.version.api.ICompound;
import space.devport.utils.version.api.ICompoundFactory;

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
