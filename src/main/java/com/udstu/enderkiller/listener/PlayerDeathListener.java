package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
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
        GameCharacter gameCharacter;

        //玩家在一个房间中时
        if (targetRoom != null) {
            gameCharacter = targetRoom.getGameCharacter(playerName);

            //将玩家的状态标记为死亡
            gameCharacter.setGameCharacterStatus(GameCharacterStatus.dead);

            //玩家为队长时
            if (gameCharacter.isTeamLeader()) {
                targetRoom.getGame().launchTeamLeaderDieVote(player);
            }

            //取消队长身份
            gameCharacter.unsetTeamLeader();

            //更新计分板
            targetRoom.updateScoreBoard();
        }
    }
}
