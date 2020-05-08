package space.devport.utils.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    @Setter
    private String parent;

    public SubCommand(String name) {
        super(name);
    }

    public void addLanguage() {
        if (getDefaultUsage() != null)
            language.addDefault("Commands.Help." + getParent() + "." + getName() + ".Usage", getDefaultUsage());
        if (getDefaultDescription() != null)
            language.addDefault("Commands.Help." + getParent() + "." + getName() + ".Description", getDefaultDescription());
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        return CommandResult.FAILURE;
    }

    @Override
    public abstract @Nullable String getDefaultUsage();

    @Override
    public abstract @Nullable String getDefaultDescription();

    @Override
    public abstract @Nullable ArgumentRange getRange();

    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}