package dev.menace.command.commands;

import dev.menace.Menace;
import dev.menace.command.Command;
import dev.menace.module.Module;
import dev.menace.utils.misc.ChatUtils;
import org.lwjgl.input.Keyboard;

public class BindsCommand extends Command {
    public BindsCommand() {
        super("Lists the binds of a module.", "Binds");
    }

    @Override
    public void call(String[] args) {
        StringBuilder message = new StringBuilder();
        int i = 0;
        for (Module m : Menace.instance.moduleManager.getModules()) {
            i++;
            if (message.toString().isEmpty()) {
                message = new StringBuilder("§4Binds: \n§r");
            } else if (i == Menace.instance.moduleManager.getModules().size()) {
                message.append("§7").append(m.getName()).append(": §d").append(Keyboard.getKeyName(m.getKeybind()).toLowerCase()).append("§r");
            } else {
                message.append("§7").append(m.getName()).append(": §d").append(Keyboard.getKeyName(m.getKeybind()).toLowerCase()).append("§r\n");
            }
        }

        ChatUtils.message(message.toString());
    }
}
