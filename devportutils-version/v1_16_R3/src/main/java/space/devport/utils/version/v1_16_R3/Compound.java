package space.devport.utils.version.v1_16_R3;

import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import space.devport.utils.version.api.ICompound;

public class Compound implements ICompound {

    private final ItemStack itemStack;

    private final NBTTagCompound compound;

    public Compound(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.compound = itemStack.getOrCreateTag();
    }

    @Override
    public boolean has(String key) {
        return compound.hasKey(key);
    }

    @Override
    public ICompound withString(String key, String value) {
        compound.setString(key, value);
        return this;
    }

    @Override
    public ICompound withDouble(String key, double value) {
        compound.setDouble(key, value);
        return this;
    }

    @Override
    public ICompound withInteger(String key, int value) {
        compound.setInt(key, value);
        return this;
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public double getDouble(String key) {
        return compound.getDouble(key);
    }

    @Override
    public int getInteger(String key) {
        return compound.getInt(key);
    }

    @Override
    public org.bukkit.inventory.ItemStack finish() {
        itemStack.setTag(compound);
        return CraftItemStack.asBukkitCopy(itemStack);
    }
}
