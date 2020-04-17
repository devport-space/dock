package space.devport.utils.commands.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.DevportPlugin;
import space.devport.utils.text.message.Message;

/**
 * Various results of the command system.
 * Makes it easier and way faster to create commands.
 */
public enum CommandResult {

    NOT_ENOUGH_ARGS("Commands.Not-Enough-Args"),
    TOO_MANY_ARGS("Commands.Too-Many-Args"),
    NO_CONSOLE("Commands.Only-Players"),
    NO_PLAYER("Commands.Only-Console"),
    NO_PERMISSION("Commands.No-Permission"),
    NOT_OPERATOR("Commands.Not-Operator"),
    FAILURE(),
    SUCCESS();

    @Getter
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
