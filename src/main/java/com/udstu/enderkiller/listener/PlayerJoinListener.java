package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.extend.GameCharacter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by czp on 16-8-4.
 * Player join listener
 */
public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        String playerName = player.getName();
        Room targetRoom = Util.searchPlayer(playerName);
        GameCharacter gameCharacter;

        if (targetRoom != null) {
            gameCharacter = targetRoom.getGameCharacter(playerName);
            if (gameCharacter.isTeamLeader()) {
                player.setMaxHealth(player.getMaxHealth() + 10);
                player.setHealth(player.getHealth() + 10);
            }

            targetRoom.getGameCharacter(playerName).setPlayer(player);
            targetRoom.updateScoreBoard();
        }
    }
}
