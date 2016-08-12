package com.udstu.enderkiller;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by czp on 16-8-5.
 * Config Class.
 */
public class Config {
    private static JavaPlugin mainClass = null;
    private static YamlConfiguration mainConfig = null;
    private static String mainConfigPath = "config.yml";
    private static YamlConfiguration langConfig = null;
    private static String langConfigPath = "lang/"; //统一使用unix路径,在windows系统运行时,java8将自动转换路径
    private static String templateResourcePath = "/template/";    //指在jar中的路径

    //传入R中
    private static void setR() {
        R.setMainClass(mainClass);
        R.setMainConfig(mainConfig);
        R.setLangConfig(langConfig);
    }

    //传入基于本插件目录的相对路径
    private static boolean createFileFromJar(String path) {
        File file = new File(mainClass.getDataFolder(), path);
        File dir = file.getParentFile();
        byte[] buffer;
        InputStream inputStream = null;

        //目录不存在时创建目录
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                mainClass.getLogger().warning("Cannot create directory " + dir);
            }
        }
        //读取jar包(Config.java所在的jar包)中的资源文件并写入对应路径的外部文件
        try {
            inputStream = Config.class.getResourceAsStream(templateResourcePath + path);
            buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            new FileOutputStream(file).write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            mainClass.getLogger().warning("Cannot create file " + file.getAbsolutePath());
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean load() {
        return reload();
    }

    //重载配置文件
    public static boolean reload() {
        File mainConfigFile = new File(mainClass.getDataFolder(), mainConfigPath);
        File langConfigFIle;
        String langConfigPath;

        //若配置文件不存在则创建
        if (!mainConfigFile.exists()) {
            mainClass.getLogger().info("Create config.yml from template");
            if (!createFileFromJar(mainConfigPath)) {
                return false;
            }
        }
        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);

        //TODO 其实应该每次重载均检测每一个语言文件,没有则创建.这里是当前语言没有则仅创建当前语言对应的文件.
        //语言文件不存在则创建
        langConfigPath = Config.langConfigPath + mainConfig.getString("lang") + ".yml";
        langConfigFIle = new File(mainClass.getDataFolder(), langConfigPath);
        if (!langConfigFIle.exists()) {
            mainClass.getLogger().info("Create language file from template");
            if (!createFileFromJar(langConfigPath)) {
                return false;
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(langConfigFIle);

        setR();

        return true;
    }

    public static void setMainClass(JavaPlugin mainClass) {
        Config.mainClass = mainClass;
    }
}
