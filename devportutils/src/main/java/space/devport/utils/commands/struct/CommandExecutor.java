package space.devport.utils.commands.struct;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface CommandExecutor {

    @NotNull
    CommandResult perform(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
}
