package dev.menace.command;

import dev.menace.Menace;
import dev.menace.command.commands.BindCommand;
import dev.menace.command.commands.BindsCommand;
import dev.menace.command.commands.ConfigCommand;
import dev.menace.command.commands.HelpCommand;
import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventSendChatMessage;
import dev.menace.utils.misc.ChatUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {

    public static ArrayList<Command> cmds = new ArrayList<>();
    public String prefix = ".";
    public String ircPrefix = "#";

    //COMMANDS
    BindCommand bindCommand = new BindCommand();
    BindsCommand bindsCommand = new BindsCommand();
    ConfigCommand configCommand = new ConfigCommand();
    HelpCommand helpCommand = new HelpCommand();

    public void init() {
        Menace.instance.eventManager.subscribe(this);
    }

    public void end() {
        Menace.instance.eventManager.unsubscribe(this);
    }

    @EventLink
    public Listener<EventSendChatMessage> onSendChatMessage = event -> {
        if (event.getMessage().startsWith(this.prefix)) {
            event.cancel();
            parse(event.getMessage().replaceFirst(this.prefix, ""));
        } else if (event.getMessage().startsWith(this.ircPrefix)) {
            event.cancel();
            //Menace.instance.irc.sendMessage(event.getMessage().replaceFirst(this.ircPrefix, ""));
        }
    };

    private void parse(String command) {
        String cmdName = command.split(" ")[0];
        final String[] args = Arrays.copyOfRange(command.split(" "), 1, command.split(" ").length);
        //Find the command with an alias that matches the command name
        Command cmd = cmds.stream().filter(c -> Arrays.stream(c.getAliases()).anyMatch(a -> a.equalsIgnoreCase(cmdName))).findFirst().orElse(null);
        if (cmd != null) {
            cmd.call(args);
        } else {
            ChatUtils.message("Unknown command. try .help");
        }
    }

    public ArrayList<Command> getCommands() {
        return cmds;
    }
}
