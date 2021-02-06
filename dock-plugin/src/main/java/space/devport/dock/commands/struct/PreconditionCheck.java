package space.devport.dock.commands.struct;

import org.bukkit.command.CommandSender;

public interface PreconditionCheck<T extends CommandSender> {
    boolean verify(T sender);
}
