package space.devport.utils.item.nbt;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.version.api.ICompound;

import java.util.Objects;

public class NBTContainer implements Cloneable {

    @Getter
    @Setter
    private Object value;

    public NBTContainer(Object value) {
        this.value = value;
    }

    private NBTContainer(@NotNull NBTContainer container) {
        Objects.requireNonNull(container);
        this.value = container.getValue();
    }

    public void apply(ICompound compound, String key) {
        TypeUtil.setValue(compound, key, value);
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public NBTContainer clone() {
        return new NBTContainer(this);
    }
}
