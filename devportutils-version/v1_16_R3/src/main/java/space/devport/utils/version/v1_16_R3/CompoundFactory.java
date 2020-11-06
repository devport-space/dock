package space.devport.utils.version.v1_16_R3;

import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import space.devport.utils.version.api.ICompound;
import space.devport.utils.version.api.ICompoundFactory;

public class CompoundFactory implements ICompoundFactory {

    @Override
    public ICompound of(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
        return new Compound(craftItemStack);
    }
}
