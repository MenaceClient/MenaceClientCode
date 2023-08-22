package dev.menace.module.config;

import com.google.gson.JsonObject;
import dev.menace.Menace;
import dev.menace.module.settings.*;
import dev.menace.utils.file.FileManager;
import net.minecraft.util.MathHelper;

import java.io.File;

public class Config {

    private final String name;
    private final File file;

    public Config(File file) {
        this.name = file.getName().replace(".json", "");
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public void save() {
        JsonObject configFile = new JsonObject();
        Menace.instance.moduleManager.getModules().forEach(module -> {
            JsonObject modSave = new JsonObject();

            modSave.addProperty("Toggled", module.isToggled());

            if (!module.getSettings().isEmpty()) {
                JsonObject settingSave = new JsonObject();
                module.getSettings().forEach(setting -> {

                    if (setting instanceof BooleanSetting) {
                        settingSave.addProperty(setting.getName(), ((BooleanSetting)setting).getValue());
                    } else if (setting instanceof NumberSetting) {
                        settingSave.addProperty(setting.getName(), ((NumberSetting)setting).getValue());
                    } else if (setting instanceof ListSetting) {
                        settingSave.addProperty(setting.getName(), ((ListSetting)setting).getValue());
                    } else if (setting instanceof DividerSetting) {
                        JsonObject dividerSave = new JsonObject();
                        for (Setting s : ((DividerSetting)setting).getSettings()) {
                            if (s instanceof BooleanSetting) {
                                dividerSave.addProperty(s.getName(), ((BooleanSetting)s).getValue());
                            } else if (s instanceof NumberSetting) {
                                dividerSave.addProperty(s.getName(), ((NumberSetting)s).getValue());
                            } else if (s instanceof ListSetting) {
                                dividerSave.addProperty(s.getName(), ((ListSetting)s).getValue());
                            }
                        }
                        settingSave.add(setting.getName(), dividerSave);
                    }

                });
                modSave.add("Settings", settingSave);
            }

            configFile.add(module.getName(), modSave);
        });
        FileManager.writeJsonToFile(file, configFile);
    }

    public void load() {
        JsonObject configFile = FileManager.readJsonFromFile(file);
        Menace.instance.moduleManager.getModules().stream().filter(mod -> configFile.has(mod.getName())).forEach(module -> {
            JsonObject modSave = configFile.get(module.getName()).getAsJsonObject();
            boolean toggled = modSave.get("Toggled").getAsBoolean();
            if ((toggled && !module.isToggled()) || (!toggled && module.isToggled())) {
                module.toggle();
            }
            if (!module.getSettings().isEmpty() && modSave.has("Settings")) {
                JsonObject settingSave = modSave.get("Settings").getAsJsonObject();
                module.getSettings().forEach(setting -> {
                    if (settingSave.has(setting.getName())) {
                        if (setting instanceof BooleanSetting) {
                            ((BooleanSetting)setting).setValue(settingSave.get(setting.getName()).getAsBoolean());
                        } else if (setting instanceof NumberSetting) {
                            ((NumberSetting)setting).setValue(MathHelper.clamp_double(settingSave.get(setting.getName()).getAsDouble(), ((NumberSetting)setting).getMin(), ((NumberSetting)setting).getMax()));
                        } else if (setting instanceof ListSetting) {
                            boolean found = false;
                            for (String s : ((ListSetting)setting).getOptions()) {
                                if (s.equalsIgnoreCase(settingSave.get(setting.getName()).getAsString()) && !found) {
                                    ((ListSetting)setting).setValue(s);
                                    found = true;
                                }
                            }

                            if (!found) {
                                ((ListSetting)setting).setValue(((ListSetting)setting).getValue());
                            }

                        } else if (setting instanceof DividerSetting) {
                            if (!settingSave.get(setting.getName()).isJsonObject()) return;
                            JsonObject dividerSave = settingSave.get(setting.getName()).getAsJsonObject();
                            for (Setting s : ((DividerSetting)setting).getSettings()) {
                                if (dividerSave.has(s.getName())) {
                                    if (s instanceof BooleanSetting) {
                                        ((BooleanSetting)s).setValue(dividerSave.get(s.getName()).getAsBoolean());
                                    } else if (s instanceof NumberSetting) {
                                        ((NumberSetting)s).setValue(dividerSave.get(s.getName()).getAsDouble());
                                    } else if (s instanceof ListSetting) {
                                        boolean found2 = false;
                                        for (String s2 : ((ListSetting)s).getOptions()) {
                                            if (s2.equalsIgnoreCase(dividerSave.get(s.getName()).getAsString()) && !found2) {
                                                ((ListSetting)s).setValue(s2);
                                                found2 = true;
                                            }
                                        }

                                        if (!found2) {
                                            ((ListSetting)s).setValue(((ListSetting)s).getValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }

}
