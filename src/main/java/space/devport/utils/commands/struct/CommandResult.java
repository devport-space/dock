package space.devport.utils.commands.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;
import space.devport.utils.text.message.Message;

/**
 * Various results of a command execution.
 * Makes it easier and way faster to create command execution.
 */
public enum CommandResult {

    /**
     * Fired after an ArgumentRange check.
     */
    NOT_ENOUGH_ARGS("Commands.Not-Enough-Args"),
    TOO_MANY_ARGS("Commands.Too-Many-Args"),

    /**
     * Preconditions
     */
    NO_CONSOLE("Commands.Only-Players"),
    NO_PLAYER("Commands.Only-Console"),
    NO_PERMISSION("Commands.No-Permission"),
    NOT_OPERATOR("Commands.Not-Operator"),

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

    CommandResult(String... path) {
        if (path.length > 0) this.path = path[0];
    }

    public void sendMessage(CommandSender sender) {
        getMessage().send(sender);
    }

    public Message getMessage() {
        if (message == null) message = DevportPlugin.getInstance().getLanguageManager().get(path);
        return new Message(message);
    }
}