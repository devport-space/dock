package space.devport.utils.commands.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.DevportPlugin;
import space.devport.utils.commands.CommandExecutor;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;

public class BuildableSubCommand extends SubCommand {

    private String defaultUsage;
    private String defaultDescription;
    private ArgumentRange argumentRange = new ArgumentRange(0);

    public BuildableSubCommand(DevportPlugin plugin, String name) {
        super(plugin, name);
        this.defaultUsage = String.format("/%%label%% %s", getName());
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return defaultUsage;
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return defaultDescription;
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return argumentRange;
    }

    public BuildableSubCommand withDefaultUsage(String defaultUsage) {
        this.defaultUsage = defaultUsage;
        return this;
    }

    public BuildableSubCommand withDefaultDescription(String defaultDescription) {
        this.defaultDescription = defaultDescription;
        return this;
    }

    @Override
    public @NotNull BuildableSubCommand withExecutor(CommandExecutor executor) {
        super.withExecutor(executor);
        return this;
    }

    @Override
    public @NotNull BuildableSubCommand withParent(@NotNull MainCommand parent) {
        super.withParent(parent);
        return this;
    }

    public BuildableSubCommand withRange(ArgumentRange range) {
        this.argumentRange = range;
        return this;
    }

    public BuildableSubCommand withRange(int min, int max) {
        return withRange(new ArgumentRange(min, max));
    }

    public BuildableSubCommand withRange(int wanted) {
        return withRange(new ArgumentRange(wanted));
    }
}
