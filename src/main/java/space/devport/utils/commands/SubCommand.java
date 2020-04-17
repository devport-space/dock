package space.devport.utils.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    @Setter
    private String parent;

    public SubCommand(String name) {
        super(name);
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        return CommandResult.FAILURE;
    }

    @Override
    public abstract String getDefaultUsage();

    @Override
    public abstract String getDefaultDescription();

    @Override
    public abstract ArgumentRange getRange();
}
