package dev.menace.module;

import com.google.gson.JsonObject;
import dev.menace.module.modules.combat.*;
import dev.menace.module.modules.misc.*;
import dev.menace.module.modules.movement.*;
import dev.menace.module.modules.player.*;
import dev.menace.module.modules.render.*;
import dev.menace.module.modules.world.*;
import dev.menace.utils.file.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ModuleManager {

    protected static ArrayList<Module> modules = new ArrayList<>();

    //COMBAT
    BacktrackModule backtrackModule = new BacktrackModule();
    CriticalsModule criticalsModule = new CriticalsModule();
    public KillauraModule killauraModule = new KillauraModule();
    public VelocityModule velocityModule = new VelocityModule();

    //MOVEMENT
    public SprintModule sprintModule = new SprintModule();
    StepModule stepModule = new StepModule();

    //PLAYER
    AntiVoidModule antiVoidModule = new AntiVoidModule();
    BlinkModule blinkModule = new BlinkModule();
    InvManagerModule invManagerModule = new InvManagerModule();
    NoSlowModule noSlowModule = new NoSlowModule();
    public SafeWalkModule safeWalkModule = new SafeWalkModule();
    public ScaffoldModule scaffoldModule = new ScaffoldModule();

    //RENDER
    ClickGuiModule clickGuiModule = new ClickGuiModule();
    ESPModule espModule = new ESPModule();
    FullbrightModule fullbrightModule = new FullbrightModule();
    HUDEditorModule hudEditorModule = new HUDEditorModule();
    public ItemPhysicsModule itemPhysicsModule = new ItemPhysicsModule();
    TimeChangerModule timeChangerModule = new TimeChangerModule();

    //WORLD
    public ChestStealerModule chestStealerModule = new ChestStealerModule();
    FastPlaceModule fastPlaceModule = new FastPlaceModule();

    //MISC
    AutoLoginModule autoLoginModule = new AutoLoginModule();
    AutoPlayModule autoPlayModule = new AutoPlayModule();
    DevModule devModule = new DevModule();
    SpooferModule spooferModule = new SpooferModule();


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
