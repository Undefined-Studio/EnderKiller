package com.udstu.enderkiller;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * Created by czp on 16-8-6.
 * Resource class
 */
public class R {
    private static JavaPlugin mainClass = null;
    private static YamlConfiguration mainConfig = null;
    private static YamlConfiguration langConfig = null;
    private static ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

    //获取对应语言中的对应字段
    public static String getLang(String key) {
        return langConfig.getString(key);
    }

    public static String getConfig(String key) {
        return mainConfig.getString(key);
    }

    public static JavaPlugin getMainClass() {
        return mainClass;
    }

    public static void setMainClass(JavaPlugin mainClass) {
        R.mainClass = mainClass;
    }

    public static YamlConfiguration getMainConfig() {
        return mainConfig;
    }

    public static void setMainConfig(YamlConfiguration mainConfig) {
        R.mainConfig = mainConfig;
    }

    public static YamlConfiguration getLangConfig() {
        return langConfig;
    }

    public static void setLangConfig(YamlConfiguration langConfig) {
        R.langConfig = langConfig;
    }

    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public static void setScoreboardManager(ScoreboardManager scoreboardManager) {
        R.scoreboardManager = scoreboardManager;
    }
}
