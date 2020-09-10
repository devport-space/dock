package space.devport.utils.commands.struct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.text.message.Message;

/**
 * Various results of a command execution.
 * Makes it easier and way faster to create command execution.
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

    public void sendMessage(CommandSender sender) {
        getMessage().send(sender);
    }

    public Message getMessage() {
        if (defaultMessage && message == null && DevportPlugin.getInstance().use(UsageFlag.LANGUAGE)) {
            message = DevportPlugin.getInstance().getManager(LanguageManager.class).get(path);
            defaultMessage = false;
        }
        return new Message(message);
    }
}