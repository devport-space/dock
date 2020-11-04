package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.message.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    private MainCommand parent;

    public SubCommand(String name) {
        super(name);
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

    @Override
    public @NotNull Message getUsage() {
        if (plugin.use(UsageFlag.LANGUAGE))
            return this.getParent() != null ? language.get("Commands.Help." + this.getParent().getName() + "." + getName() + ".Usage") : new Message();
        return new Message(getDefaultUsage());
    }

    @NotNull
    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public void addLanguage() {
        if (plugin.use(UsageFlag.LANGUAGE)) {
            if (getDefaultUsage() != null)
                language.addDefault("Commands.Help." + getParent().getName() + "." + getName() + ".Usage", getDefaultUsage());
            if (getDefaultDescription() != null)
                language.addDefault("Commands.Help." + getParent().getName() + "." + getName() + ".Description", getDefaultDescription());
        }
    }

    public SubCommand withParent(MainCommand parent) {
        this.parent = parent;
        this.preconditions.permissions(craftPermission());
        return this;
    }
}