package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.listener.implement.DeathEventCallBack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

/**
 * Created by czp on 16-9-13.
 * 任何一个指定的玩家死亡时回调
 */
public class DeathListener implements Listener {
    private List<Player> players;
    private DeathEventCallBack deathEventCallBack;

    public DeathListener(List<Player> players, DeathEventCallBack deathEventCallBack) {
        this.players = players;
        this.deathEventCallBack = deathEventCallBack;
    }

    @EventHandler
    public void onAnyPlayerDie(PlayerDeathEvent playerDeathEvent) {
        for (Player player : players) {
            if (player.getName().equals(playerDeathEvent.getEntity().getName())) {
                deathEventCallBack.deathEventCallBack(playerDeathEvent.getEntity());
                HandlerList.unregisterAll(this);
                return;
            }
        }
    }
}
