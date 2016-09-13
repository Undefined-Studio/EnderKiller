package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.RoomStatus;
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
        Room room = Util.searchPlayer(playerName);
        GameCharacter gameCharacter;

        //玩家在一个房间中且游戏已开始时
        if (room != null && room.getRoomStatus() == RoomStatus.inGame) {
            gameCharacter = room.getGameCharacter(playerName);

            //若此角色为队长时进行队长死亡投票
            if (gameCharacter.isTeamLeader()) {
                //先标记为死亡再发起投票
                room.getGameCharacter(playerName).onDeath();
                room.getGame().launchTeamLeaderDieVote(player);
            } else {
                room.getGameCharacter(playerName).onDeath();
            }

            //更新计分板
            room.updateScoreBoard();
        }
    }
}
