package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    private MainCommand parent;

    public SubCommand(String name) {
        super(name);
    }

    public void addLanguage() {
        if (DevportPlugin.getInstance().use(UsageFlag.LANGUAGE)) {
            if (getDefaultUsage() != null)
                language.addDefault("Commands.Help." + getParent().getName() + "." + getName() + ".Usage", getDefaultUsage());
            if (getDefaultDescription() != null)
                language.addDefault("Commands.Help." + getParent().getName() + "." + getName() + ".Description", getDefaultDescription());
        }
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

    public void setParent(MainCommand parent) {
        this.parent = parent;
        this.preconditions.setPermissions(Collections.singletonList(craftPermission()));
    }

    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}