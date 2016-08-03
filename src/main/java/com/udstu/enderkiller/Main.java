package com.udstu.enderkiller;

import com.udstu.enderkiller.command.CommandEK;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by yst on 2016/7/31.
 * Spigot main class
 */
public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        this.getCommand("ek").setExecutor(new CommandEK());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
