package space.devport.dock.version.v1_12_R1;

import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
