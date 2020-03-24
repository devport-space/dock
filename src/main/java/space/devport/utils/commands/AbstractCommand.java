package space.devport.utils.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.Message;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand {

    @Getter
    protected Preconditions preconditions = new Preconditions();

    protected String[] aliases = new String[]{};

    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    public void runCommand(CommandSender sender, String... args) {
        if (!preconditions.check(sender)) return;

        perform(sender, args).getMessage().replace("%usage%", getUsage()).send(sender);
    }

    protected abstract CommandResult perform(CommandSender sender, String... args);

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

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getUsage();
}