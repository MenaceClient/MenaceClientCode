package dev.menace.module;

import com.google.gson.JsonObject;
import dev.menace.module.modules.render.ClickGuiModule;
import dev.menace.module.modules.render.HUDEditorModule;
import dev.menace.module.modules.world.FastPlaceModule;
import dev.menace.utils.file.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ModuleManager {

    protected static ArrayList<Module> modules = new ArrayList<>();

    //COMBAT

    //MOVEMENT

    //PLAYER

    //RENDER
    ClickGuiModule clickGuiModule = new ClickGuiModule();
    HUDEditorModule hudEditorModule = new HUDEditorModule();

    //WORLD
    FastPlaceModule fastPlaceModule = new FastPlaceModule();

    //MISC


    public ArrayList<Module> getModules() {
        return modules;
    }

    public void removeModule(Module module) {
        modules.remove(module);
    }

    public ArrayList<Module> getActiveModules() {
        ArrayList<Module> activeModules = new ArrayList<>();

        for (Module m : modules) {
            if (m.isToggled()) activeModules.add(m);
        }

        return activeModules;
    }

    public Module getModuleByName(String name) {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ArrayList<Module> getModulesByCategory(Category c) {
        return modules.stream().filter(module -> module.getCategory() == c).collect(Collectors.toCollection(ArrayList::new));
    }

    public void saveKeys() {
        JsonObject bindFile = new JsonObject();
        this.modules.forEach(module -> bindFile.addProperty(module.getName(), module.getKeybind()));
        FileManager.writeJsonToFile(new File(FileManager.getConfigFolder(), "Binds.json"), bindFile);
    }

}