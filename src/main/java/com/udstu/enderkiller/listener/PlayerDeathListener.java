package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Created by czp on 16-9-7.
 * 玩家死亡监听器
 */
public class PlayerDeathListener implements Listener {
    @EventHandler
    public void onPlayerDie(PlayerDeathEvent playerDeathEvent) {
        Player player = playerDeathEvent.getEntity();
        String playerName = player.getName();
        Room targetRoom = Util.searchPlayer(playerName);

        //玩家在一个房间中时
        if (targetRoom != null) {
            targetRoom.getGameCharacter(playerName).onDeath();

            //进行队长死亡投票
            targetRoom.getGame().launchTeamLeaderDieVote(player);

            //更新计分板
            targetRoom.updateScoreBoard();
        }
    }
}
