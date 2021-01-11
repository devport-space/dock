package space.devport.utils.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.CommandResult;

public interface CommandExecutor {

    @NotNull
    CommandResult perform(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
}
