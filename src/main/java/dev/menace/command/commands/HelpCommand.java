package dev.menace.command.commands;

import dev.menace.Menace;
import dev.menace.command.Command;
import dev.menace.utils.misc.ChatUtils;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("Shows this message.", "Help", "?");
    }

    @Override
    public void call(String[] args) {
        ChatUtils.message("===Help===");
        for (Command cmd : Menace.instance.commandManager.getCommands()) {
            ChatUtils.noPrefix("§5" + cmd.getAliases()[0] + " - §r" + cmd.getDescription());
        }
        ChatUtils.message("===Help===");
    }
}
