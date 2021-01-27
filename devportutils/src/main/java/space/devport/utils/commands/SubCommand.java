package space.devport.utils.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.message.Message;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    private MainCommand parent;

    public SubCommand(DevportPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
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

    public void addLanguage() {
        if (plugin.use(UsageFlag.LANGUAGE)) {
            if (getDefaultUsage() != null)
                language.addDefault("Commands.Help." + getParent().getName() + "." + getName() + ".Usage", getDefaultUsage());
            if (getDefaultDescription() != null)
                language.addDefault("Commands.Help." + getParent().getName() + "." + getName() + ".Description", getDefaultDescription());
        }
    }

    @NotNull
    public SubCommand withParent(@NotNull MainCommand parent) {
        this.parent = parent;
        this.preconditions.permissions(craftPermission());
        return this;
    }

    @Override
    public @NotNull SubCommand withExecutor(@Nullable CommandExecutor executor) {
        super.withExecutor(executor);
        return this;
    }

    @Override
    public @NotNull SubCommand withCompletionProvider(@Nullable CompletionProvider completionProvider) {
        super.withCompletionProvider(completionProvider);
        return this;
    }
}