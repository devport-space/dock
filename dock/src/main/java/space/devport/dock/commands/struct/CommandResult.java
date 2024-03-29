package space.devport.dock.commands.struct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.dock.DockedPlugin;
import space.devport.dock.UsageFlag;
import space.devport.dock.api.IDockedPlugin;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.text.message.Message;

/**
 * Various results of a command execution.
 * Makes it easier and way faster to code command executions.
 */
@NoArgsConstructor
public enum CommandResult {

    /**
     * Fired after an ArgumentRange check.
     */
    NOT_ENOUGH_ARGS("Commands.Not-Enough-Args", "%prefix%&cNot enough arguments.", "%prefix%&cUsage: &7%usage%"),
    TOO_MANY_ARGS("Commands.Too-Many-Args", "%prefix%&cToo many arguments.", "%prefix%&cUsage: &7%usage%"),

    /**
     * Preconditions
     */
    NO_CONSOLE("Commands.Only-Players", "%prefix%&cOnly players can do this."),
    NO_PLAYER("Commands.Only-Console", "%prefix%&cOnly the console can do this."),
    NO_PERMISSION("Commands.No-Permission", "%prefix%&cYou don't have permission to do this."),
    NOT_OPERATOR("Commands.Not-Operator", "%prefix%&cOnly operators are allowed to do this."),

    /**
     * Generic, no message.
     */
    FAILURE(),
    SUCCESS();

    @Getter
    @Setter
    private String path;

    @Setter
    private Message message;

    @Getter
    private boolean defaultMessage = false;

    CommandResult(String path, String... defaultMessage) {
        this.message = new Message(defaultMessage);
        this.path = path;
    }

    public void sendMessage(CommandSender sender, DockedPlugin plugin) {
        getMessage(plugin).send(sender);
    }

    public Message getMessage(IDockedPlugin plugin) {
        if (defaultMessage && message == null && plugin.use(UsageFlag.LANGUAGE)) {
            this.message = plugin.getManager(LanguageManager.class).get(path);
            this.defaultMessage = false;
        }
        return Message.of(message);
    }
}