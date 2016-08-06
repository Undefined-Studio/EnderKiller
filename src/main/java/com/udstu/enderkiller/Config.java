package com.udstu.enderkiller;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by czp on 16-8-5.
 * Config Class.
 */
public class Config {
    private static JavaPlugin mainClass = null;
    private static YamlConfiguration mainConfig = null;
    private static String mainConfigPath = "config.yml";
    private static YamlConfiguration langConfig = null;
    private static String langConfigPath = "lang/";

    private static void setR() {
        R.setMainClass(mainClass);
        R.setMainConfig(mainConfig);
        R.setLangConfig(langConfig);
    }

    public static void load() {
        reload();
    }

    public static void reload() {
        String langConfigPath;

        mainConfig = YamlConfiguration.loadConfiguration(new File(mainClass.getDataFolder(), mainConfigPath));
        langConfigPath = Config.langConfigPath + mainConfig.getString("lang") + ".yml";
        System.out.println(mainClass.getDataFolder() + langConfigPath);
        langConfig = YamlConfiguration.loadConfiguration(new File(mainClass.getDataFolder(), langConfigPath));

        setR();
    }

    public static void setMainClass(JavaPlugin mainClass) {
        Config.mainClass = mainClass;
    }
}
