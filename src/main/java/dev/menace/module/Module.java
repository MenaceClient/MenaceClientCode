package dev.menace.module;

import com.google.gson.JsonObject;
import dev.menace.Menace;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.Setting;
import dev.menace.utils.file.FileManager;
import dev.menace.utils.misc.ChatUtils;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Module {

    public final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final String description;
    private final Category category;
    private int keybind;
    private boolean toggled;
    private BooleanSetting visible;
    private String displayName = null;
    ArrayList<Setting> settings = new ArrayList<>();

    public Module(String name, String description, Category category, int keybind) {
        this(name, description, category);
        if (this.keybind == 0) {
            this.keybind = keybind;
        }
    }

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.toggled = false;

        if (!(new File(FileManager.getConfigFolder(), "Binds.json").exists())) {
            this.keybind = 0;
        } else {
            JsonObject bindFile = FileManager.readJsonFromFile(new File(FileManager.getConfigFolder(), "Binds.json"));
            assert bindFile != null;
            if (bindFile.has(name)) {
                this.keybind = bindFile.get(name).getAsInt();
            } else {
                this.keybind = 0;
            }
        }

        ModuleManager.modules.add(this);
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
        visible = new BooleanSetting("Visible", true, true);
        this.settings.add(visible);
    }

    public void onEnable() {
        Menace.instance.eventManager.subscribe(this);
    }

    public void onDisable() {
        Menace.instance.eventManager.unsubscribe(this);
    }

    public void onGuiUpdate() { }

    public void toggle() {
        this.setToggled(!this.toggled);
    }

    public void setToggled(boolean toggled) {
        if (mc.theWorld == null && this.getClass().isAnnotationPresent(DontSaveState.class)) {
            this.toggled = false;
            return;
        }

        if (this.toggled == toggled) return;

        this.toggled = toggled;

        if (this.toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public int getKeybind() {
        return this.keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
        Menace.instance.moduleManager.saveKeys();
    }
    public void setKeybindNoSave(int keybind) {
        this.keybind = keybind;
    }
    public String getDisplayName() {
        return displayName != null ? getName() + " §7- " + displayName : getName();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    public boolean isVisible() {
        return this.visible.getValue();
    }

    public void setVisible(boolean visible) {
        this.visible.setValue(visible);
    }
}