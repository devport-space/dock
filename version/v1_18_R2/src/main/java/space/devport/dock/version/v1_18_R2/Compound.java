package space.devport.dock.version.v1_18_R2;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import space.devport.dock.version.api.ICompound;

import java.util.Set;

public class Compound implements ICompound {

    private final NBTTagCompound compound;

    private ItemStack itemStack;

    public Compound() {
        this.compound = new NBTTagCompound();
    }

    public Compound(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.compound = itemStack.u(); // #getOrCreateTag()
    }

    @Override
    public byte getId(String key) {
        NBTBase base = compound.c(key); // #getTag(String)
        return base == null ? -1 : base.a(); // #getTypeId()
    }

    @Override
    public ICompound remove(String key) {
        compound.r(key); // #remove(key)
        return this;
    }

    @Override
    public boolean has(String key) {
        return compound.e(key); // #hasKey(key);
    }

    @Override
    public Set<String> getKeys() {
        return compound.d(); // #getKeys();
    }

    @Override
    public ICompound withString(String key, String value) {
        compound.a(key, value); // #setString(key, value);
        return this;
    }

    @Override
    public ICompound withBoolean(String key, boolean value) {
        compound.a(key, value); // #setBoolean(key, value);
        return this;
    }

    @Override
    public ICompound withInteger(String key, int value) {
        compound.a(key, value); // #setInt(key, value);
        return this;
    }

    @Override
    public ICompound withDouble(String key, double value) {
        compound.a(key, value); // #setDouble(key, value);
        return this;
    }

    @Override
    public ICompound withLong(String key, long value) {
        compound.a(key, value); // #setLong(key, value);
        return this;
    }

    @Override
    public ICompound withFloat(String key, float value) {
        compound.a(key, value); // #setFloat(key, value);
        return this;
    }

    @Override
    public ICompound withShort(String key, short value) {
        compound.a(key, value); // #setShort(key, value);
        return this;
    }

    @Override
    public String getString(String key) {
        return compound.l(key); // #getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return compound.q(key); // #getBoolean(key);
    }

    @Override
    public int getInteger(String key) {
        return compound.h(key); // #getInt(key);
    }

    @Override
    public double getDouble(String key) {
        return compound.k(key); // #getDouble(key);
    }

    @Override
    public long getLong(String key) {
        return compound.i(key); // #getLong(key);
    }

    @Override
    public float getFloat(String key) {
        return compound.j(key); // #getFloat(key);
    }

    @Override
    public short getShort(String key) {
        return compound.g(key); // #getShort(key);
    }

    @Override
    public ICompound withByte(String key, byte value) {
        compound.a(key, value); // #setByte(key, value);
        return this;
    }

    @Override
    public ICompound withByteArray(String key, byte[] value) {
        compound.a(key, value); // #setByteArray(key, value);
        return this;
    }

    @Override
    public ICompound withIntArray(String key, int[] value) {
        compound.a(key, value); // #setIntArray(key, value);
        return this;
    }

    @Override
    public byte getByte(String key) {
        return compound.f(key); // #getByte(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return compound.m(key); // #getByteArray(key);
    }

    @Override
    public int[] getIntArray(String key) {
        return compound.n(key); // #getIntArray(key);
    }

    @Override
    public org.bukkit.inventory.ItemStack apply(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack item = CraftItemStack.asNMSCopy(itemStack);
        item.c(compound); // #setTag(compound);
        return CraftItemStack.asBukkitCopy(item);
    }

    @Override
    public org.bukkit.inventory.ItemStack finish() {

        if (itemStack == null)
            return null;

        itemStack.c(compound);
        return CraftItemStack.asBukkitCopy(itemStack);
    }
}
