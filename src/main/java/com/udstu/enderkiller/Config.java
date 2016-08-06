package com.udstu.enderkiller;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

/**
 * Created by czp on 16-8-5.
 * Config Class.
 */
public class Config {
    private static JavaPlugin mainClass = null;
    private static YamlConfiguration mainConfig = null;
    private static String mainConfigPath = "config.yml";
    private static YamlConfiguration langConfig = null;
    private static String langConfigPath = "lang" + File.separator;
    private static String templateResourcePath="/template/";    //指在jar中的路径

    private static void setR() {
        R.setMainClass(mainClass);
        R.setMainConfig(mainConfig);
        R.setLangConfig(langConfig);
    }

    //传入基于本插件目录的相对路径
    private static boolean createFileFromJar(String path) {
        File file = new File(mainClass.getDataFolder(), path);
        File dir=file.getParentFile();
        byte[] buffer;
        InputStream inputStream=null;

        //目录不存在时创建目录
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Cannot create directory " + dir);
            }
        }
        //读取jar包(Config.java所在的jar包)中的资源文件并写入外部文件
        try {
            inputStream=Config.class.getResourceAsStream(templateResourcePath+path);
            buffer=new byte[inputStream.available()];
            inputStream.read(buffer);
            new FileOutputStream(file).write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot create file " + file.getAbsolutePath());
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean load() {
        return reload();
    }

    public static boolean reload() {
        File mainConfigFile = new File(mainClass.getDataFolder(), mainConfigPath);
        File langConfigFIle;
        String langConfigPath;

        //若配置文件不存在则创建
        if (!mainConfigFile.exists()) {
            System.out.println("Create config.yml from template");
            if (!createFileFromJar(mainConfigPath)){
                return false;
            }
        }
        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile);

        //语言文件不存在则创建
        langConfigPath = Config.langConfigPath + mainConfig.getString("lang") + ".yml";
        langConfigFIle=new File(mainClass.getDataFolder(), langConfigPath);
        if (!langConfigFIle.exists()){
            System.out.println("Create language file from template");
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
