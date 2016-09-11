package com.udstu.enderkiller;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.command.CommandEk;
import com.udstu.enderkiller.listener.PlayerDeathListener;
import com.udstu.enderkiller.listener.PlayerJoinListener;
import com.udstu.enderkiller.listener.PlayerPortalListener;
import com.udstu.enderkiller.listener.PlayerQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by yst on 2016/7/31.
 * EnderKiller main class
 */
public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("ek").setExecutor(new CommandEk());
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerPortalListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);

        //传入主类(指本类)引用至Config,并执行一次配置文件载入
        Config.setMainClass(this);
        Config.load();
    }

    @Override
    public void onDisable() {
        for (Room room : Lobby.getRoomList()) {
            for (GameCharacter gameCharacter : room.getGameCharacters()) {
                gameCharacter.onDeath();
            }
        }
    }
}
