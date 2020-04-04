package space.devport.utils.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.Message;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand {

    // TODO: Hook all of the options to a commands.yml file for maximum customisation.

    @Getter
    private final String name;

    @Getter
    protected Preconditions preconditions = new Preconditions();

    protected String[] aliases = new String[]{};

    @Getter
    private String usage;

    @Getter
    private String description;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public AbstractCommand(String name, String usage, String description) {
        this.name = name;
        this.usage = usage;
        this.description = description;
    }

    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    // This is called from outside and sends the message automatically once it gets a response.
    public void runCommand(CommandSender sender, String... args) {
        if (!preconditions.check(sender)) return;

        perform(sender, args).getMessage().replace("%usage%", getUsage()).send(sender);
    }

    // This should be overriden by commands and performs the wanted action itself.
    protected abstract CommandResult perform(CommandSender sender, String... args);

    // TODO: Hook messages to locale
    public enum CommandResult {

        NOT_ENOUGH_ARGS("&cNot enough arguments!", "%usage%"),
        TOO_MANY_ARGS("&cToo many arguments!", "%usage%"),
        NO_CONSOLE("&cOnly for players!"),
        NO_PLAYER("&cOnly for console!"),
        FAILURE(new Message()),
        SUCCESS(new Message());

        @Setter
        private Message message;

        CommandResult(Message message) {
            this.message = message;
        }

        CommandResult(String... message) {
            this.message = new Message(message);
        }

        public void sendMessage(CommandSender sender) {
            message.send(sender);
        }

        public Message getMessage() {
            return new Message(message);
        }
    }
}