package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.enumeration.Alignment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Created by czp on 16-9-17.
 * 实体死亡监听器,用于监听末影龙死亡事件
 */
public class DragonDeathListener implements Listener {
    @EventHandler
    public void onDragonDeath(EntityDeathEvent entityDeathEvent) {
        LivingEntity livingEntity = entityDeathEvent.getEntity();
        String worldName = livingEntity.getWorld().getName();
        String worldNamePrefix = R.getConfig("worldNamePrefix");
        String spawnWorldName = R.getConfig("spawnWorldName");
        String worldNameExceptPrefix;
        String roomIdStr;
        int roomId;
        Room room;

        //不为末影龙时
        if (livingEntity.getType() != EntityType.ENDER_DRAGON) {
            return;
        }
        //当此末影龙处在插件生成的世界时
        if (!worldName.equals(spawnWorldName) && worldName.substring(0, worldNamePrefix.length()).equals(worldNamePrefix)) {
            worldNameExceptPrefix = worldName.substring(worldNamePrefix.length() + 1);
            roomIdStr = worldNameExceptPrefix.substring(0, worldNameExceptPrefix.indexOf("_"));
            roomId = Integer.valueOf(roomIdStr);
            room = Util.searchRoom(roomId);
            room.getGame().gameOver(Alignment.explorer);
        }
    }
}
