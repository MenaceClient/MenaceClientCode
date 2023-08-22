package dev.menace.command.commands;

import dev.menace.Menace;
import dev.menace.command.Command;
import dev.menace.module.config.Config;
import dev.menace.utils.misc.ChatUtils;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("Save and load configs", "config", "cfg");
    }

    @Override
    public void call(String[] args) {
        if (args[0].equalsIgnoreCase("save")) {
            Menace.instance.configManager.saveConfig(args[1]);
            ChatUtils.message("Successfully saved " + args[1] + ".");
        } else if (args[0].equalsIgnoreCase("load")) {
            //Add this?
            //Menace.instance.moduleManager.saveModules(Menace.instance.configManager.getLoadedConfig().getName());

            if (args[1].isEmpty()) {
                Menace.instance.configManager.saveConfig(Menace.instance.configManager.getLoadedConfig());
                ChatUtils.message("Successfully saved " + Menace.instance.configManager.getLoadedConfig().getName() + ".");
                return;
            }

            if (Menace.instance.configManager.loadConfig(args[1])) {
                ChatUtils.message("Loaded config " + args[1]);
            } else {
                ChatUtils.message("Config " + args[1] + " not found.");
            }
        } else if (args[0].equalsIgnoreCase("none")) {
            Menace.instance.moduleManager.getModules().forEach(module -> {
                if (module.isToggled()) {
                    module.toggle();
                }

                module.setKeybindNoSave(0);

            });
        } else if (args[0].equalsIgnoreCase("list")) {
            StringBuilder builder = new StringBuilder();
            for (Config config : Menace.instance.configManager.getConfigs()) {
                builder.append(config.getName()).append(", ");
            }
            ChatUtils.message("Configs: " + builder);
        } else {
            ChatUtils.message("Invalid syntax. Usage: .config <save/load> <name> or .config list");
        }
    }
}
