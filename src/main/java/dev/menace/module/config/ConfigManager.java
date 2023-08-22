package dev.menace.module.config;

import dev.menace.utils.file.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigManager {

    private Config loadedConfig;
    private ArrayList<Config> configs = new ArrayList<>();

    public ConfigManager() {
        scanConfigs();
        loadConfig("default");
        if (loadedConfig == null) {
            loadedConfig = new Config(new File(FileManager.getConfigFolder(), "default.json"));
            loadedConfig.save();
        }
    }

    public Config getLoadedConfig() {
        return loadedConfig;
    }

    public ArrayList<Config> getConfigs() {
        return configs;
    }

    public void scanConfigs() {
        configs.clear();
        for (File file : Objects.requireNonNull(FileManager.getConfigFolder().listFiles())) {
            if (file.getName().endsWith(".json") && !file.getName().equalsIgnoreCase("Binds.json")) {
                configs.add(new Config(file));
            }
        }
    }

    public void saveConfig(String name) {
        scanConfigs();
        for (Config config : configs) {
            if (config.getName().equalsIgnoreCase(name)) {
                config.save();
                return;
            }
        }

        // If the config doesn't exist, create it
        Config config = new Config(new File(FileManager.getConfigFolder(), name + ".json"));
        config.save();
        scanConfigs();
    }

    public void saveConfig(Config config) {
        scanConfigs();
        config.save();
        scanConfigs();
    }

    public void loadConfig(Config config) {
        scanConfigs();
        this.loadedConfig = config;
        config.load();
    }

    public boolean loadConfig(String name) {
        scanConfigs();
        for (Config config : configs) {
            if (config.getName().equalsIgnoreCase(name)) {
                this.loadedConfig = config;
                config.load();
                return true;
            }
        }
        return false;
    }
}
