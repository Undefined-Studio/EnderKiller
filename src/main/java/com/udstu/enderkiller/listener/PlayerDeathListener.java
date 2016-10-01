package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.R;
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
            gameCharacter = room.getGameCharacters(playerName);

            //若此角色为队长时进行队长死亡投票
            if (gameCharacter.isTeamLeader()) {
                //先标记为死亡再发起投票
                room.getGameCharacters(playerName).onDeath();
                room.getGame().launchTeamLeaderDieVote(player);
            } else {
                room.getGameCharacters(playerName).onDeath();
            }

            //更新计分板
            room.updateScoreBoard();
            //检查游戏是否结束.使用计划任务保持顺序
            R.getMainClass().getServer().getScheduler().runTask(R.getMainClass(), new Runnable() {
                @Override
                public void run() {
                    room.getGame().checkGameOver();
                }
            });
        }
    }
}
