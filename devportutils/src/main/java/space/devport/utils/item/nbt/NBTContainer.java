package space.devport.utils.item.nbt;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.version.api.ICompound;

public class NBTContainer implements Cloneable {

    @Getter
    @Setter
    private Object value;

    public NBTContainer(Object value) {
        this.value = value;
    }

    private NBTContainer(NBTContainer container) {
        this.value = container.getValue();
    }

    @Override
    public NBTContainer clone() {
        return new NBTContainer(this);
    }


    public void apply(ICompound compound, String key) {
        TypeUtil.setValue(compound, key, value);
    }
}
