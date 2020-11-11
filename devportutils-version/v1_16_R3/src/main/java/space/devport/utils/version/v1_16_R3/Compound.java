package space.devport.utils.version.v1_16_R3;

import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import space.devport.utils.version.api.ICompound;

import java.util.Set;

public class Compound implements ICompound {

    private final NBTTagCompound compound;

    private ItemStack itemStack;

    public Compound() {
        this.compound = new NBTTagCompound();
    }

    public Compound(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.compound = itemStack.getOrCreateTag();
    }

    @Override
    public byte getId(String key) {
        NBTBase base = compound.get(key);
        return base == null ? -1 : base.getTypeId();
    }

    @Override
    public ICompound remove(String key) {
        compound.remove(key);
        return this;
    }

    @Override
    public boolean has(String key) {
        return compound.hasKey(key);
    }

    @Override
    public Set<String> getKeys() {
        return compound.getKeys();
    }

    @Override
    public ICompound withString(String key, String value) {
        compound.setString(key, value);
        return this;
    }

    @Override
    public ICompound withBoolean(String key, boolean value) {
        compound.setBoolean(key, value);
        return this;
    }

    @Override
    public ICompound withInteger(String key, int value) {
        compound.setInt(key, value);
        return this;
    }

    @Override
    public ICompound withDouble(String key, double value) {
        compound.setDouble(key, value);
        return this;
    }

    @Override
    public ICompound withLong(String key, long value) {
        compound.setLong(key, value);
        return this;
    }

    @Override
    public ICompound withFloat(String key, float value) {
        compound.setFloat(key, value);
        return this;
    }

    @Override
    public ICompound withShort(String key, short value) {
        compound.setShort(key, value);
        return this;
    }

    @Override
    public String getString(String key) {
        return compound.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.getBoolean(key);
    }

    @Override
    public int getInteger(String key) {
        return compound.getInt(key);
    }

    @Override
    public double getDouble(String key) {
        return compound.getDouble(key);
    }

    @Override
    public long getLong(String key) {
        return compound.getLong(key);
    }

    @Override
    public float getFloat(String key) {
        return compound.getFloat(key);
    }

    @Override
    public short getShort(String key) {
        return compound.getShort(key);
    }

    @Override
    public org.bukkit.inventory.ItemStack apply(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack item = CraftItemStack.asNMSCopy(itemStack);
        item.setTag(compound);
        return CraftItemStack.asBukkitCopy(item);
    }

    @Override
    public org.bukkit.inventory.ItemStack finish() {

        if (itemStack == null)
            return null;

        itemStack.setTag(compound);
        return CraftItemStack.asBukkitCopy(itemStack);
    }
}
