package space.devport.utils.commands;

import org.bukkit.command.CommandSender;

public class SubCommand extends AbstractCommand {

    public SubCommand(String name) {
        super(name);
    }

    public SubCommand(String name, String usage, String description) {
        super(name, usage, description);
    }

    @Override
    protected CommandResult perform(CommandSender sender, String... args) {
        return CommandResult.SUCCESS;
    }
}
