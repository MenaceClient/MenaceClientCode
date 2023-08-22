package dev.menace.command;

import net.minecraft.client.Minecraft;

public abstract class Command {

    String[] aliases;
    String description;
    protected Minecraft mc = Minecraft.getMinecraft();

    public Command(String description, String... aliases) {
        this.aliases = aliases;
        this.description = description;

        CommandManager.cmds.add(this);
    }

    public abstract void call(String[] args);

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

}
