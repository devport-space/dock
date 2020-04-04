package space.devport.utils.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand extends AbstractCommand {

    public SubCommand(String name) {
        super(name);
    }

    @Override
    protected CommandResult perform(CommandSender sender, String... args) {
        return CommandResult.FAILURE;
    }

    // TODO: Hook to locale

    @Override
    public abstract String getUsage();

    @Override
    public abstract String getDescription();
}
