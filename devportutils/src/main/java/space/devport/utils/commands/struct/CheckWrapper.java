package space.devport.utils.commands.struct;

import lombok.Data;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@Data
public class CheckWrapper<T extends CommandSender> {

    private final PreconditionCheck<T> check;
    private final Consumer<CommandSender> errorCallback;
    private final Class<T> clazz;

    public boolean isAssignable(@NotNull Class<?> other) {
        return clazz.isAssignableFrom(other);
    }

    @Nullable
    public <X> T cast(X object) {
        return object != null && isAssignable(object.getClass()) ? clazz.cast(object) : null;
    }

    public boolean verify(CommandSender sender) {
        T object = cast(sender);
        if (object == null)
            return false;
        return check.verify(object);
    }
}
