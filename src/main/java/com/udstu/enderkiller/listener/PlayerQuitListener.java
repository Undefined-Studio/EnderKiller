package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * Created by czp on 16-8-19.
 * Player exit listener
 */
public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerExit(PlayerQuitEvent playerQuitEvent) {
        Plugin thisPlugin;
        Player player;
        Room targetRoom;

        thisPlugin = R.getMainClass();
        player = playerQuitEvent.getPlayer();
        targetRoom = Util.searchPlayer(player.getName());
        if (targetRoom != null) {
            //由于玩家指针在本tick最后才会被spigot丢弃,因此在本tick未结束前,player.isOnline都是true.故此处必须将任务放到下一tick执行
            thisPlugin.getServer().getScheduler().runTask(thisPlugin, new Runnable() {
                @Override
                public void run() {
                    targetRoom.updateScoreBoard();
                }
            });
        }
    }
}
