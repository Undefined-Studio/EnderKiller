package com.udstu.enderkiller;

import com.udstu.enderkiller.command.CommandEk;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by yst on 2016/7/31.
 * EnderKiller main class
 */
public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("ek").setExecutor(new CommandEk());

        Config.setMainClass(this);
        Config.load();
    }

    @Override
    public void onDisable() {

    }
}
