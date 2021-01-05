package space.devport.utils.commands;

import org.bukkit.command.CommandSender;
import space.devport.utils.commands.struct.CommandResult;

public interface CommandExecutor {

    CommandResult execute(CommandSender sender, String label, String[] args);
}
