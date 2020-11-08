package space.devport.utils.item.nbt;

import lombok.Getter;
import space.devport.utils.version.api.ICompound;

public class NBTContainer {

    @Getter
    private final Object value;

    public NBTContainer(Object value) {
        this.value = value;
    }

    public void apply(ICompound compound, String key) {
        TypeUtil.setValue(compound, key, value);
    }
}
