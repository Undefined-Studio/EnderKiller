package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
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
        Player player;
        String playerName;
        Room targetRoom;

        player = playerJoinEvent.getPlayer();
        playerName = playerJoinEvent.getPlayer().getName();
        targetRoom = Util.searchPlayer(playerName);
        if (targetRoom != null) {
            targetRoom.getGameCharacter(playerName).setPlayer(player);
            targetRoom.updateScoreBoard();
        }
    }
}
