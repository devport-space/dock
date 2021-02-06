package space.devport.dock.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.DockedPlugin;
import space.devport.dock.UsageFlag;
import space.devport.dock.commands.struct.*;
import space.devport.dock.text.message.Message;

import java.util.function.Consumer;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    private MainCommand parent;

    public SubCommand(DockedPlugin plugin, String name) {
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
        getPreconditions().permissions(craftPermission());
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

    @Override
    public @NotNull SubCommand withRange(ArgumentRange range) {
        super.withRange(range);
        return this;
    }

    @Override
    public @NotNull SubCommand withRange(int min, int max) {
        super.withRange(min, max);
        return this;
    }

    @Override
    public @NotNull SubCommand withRange(int wanted) {
        super.withRange(wanted);
        return this;
    }

    @Override
    public @NotNull SubCommand modifyPreconditions(Consumer<Preconditions> modifier) {
        super.modifyPreconditions(modifier);
        return this;
    }
}