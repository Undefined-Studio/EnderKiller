package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.extend.GameCharacter;
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
        Plugin thisPlugin = R.getMainClass();
        Player player = playerQuitEvent.getPlayer();
        Room targetRoom = Util.searchPlayer(player.getName());
        GameCharacter gameCharacter;

        if (targetRoom != null) {
            gameCharacter = targetRoom.getGameCharacter(player.getName());
            if (gameCharacter.isTeamLeader()) {
                player.setMaxHealth(player.getMaxHealth() - 10);
            }

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
