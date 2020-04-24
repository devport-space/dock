package space.devport.utils.commands;

import jdk.internal.joptsimple.internal.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SubCommand extends AbstractCommand {

    @Getter
    @Setter
    private String parent;

    public SubCommand(String name) {
        super(name);
    }

    public void addLanguage() {
        language.addDefault("Commands.Help." + getParent() + "." + getName() + ".Usage", getDefaultUsage());
        language.addDefault("Commands.Help." + getParent() + "." + getName() + ".Description", getDefaultDescription());
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

    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    protected List<String> filterSuggestions(List<String> input, String arg) {
        if (Strings.isNullOrEmpty(arg)) return input;
        return input.stream().filter(o -> o.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }
}