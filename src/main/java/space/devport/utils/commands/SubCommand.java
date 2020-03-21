package space.devport.utils.commands;

import org.bukkit.command.CommandSender;

public class SubCommand extends AbstractCommand {

    @Override
    protected CommandResult perform(CommandSender sender, String... args) {
        return CommandResult.SUCCESS;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }
}
