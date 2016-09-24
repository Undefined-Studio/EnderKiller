package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.enumeration.GameStatus;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

/**
 * Created by czp on 16-9-5.
 * Change the destination of portals in game
 */
public class PlayerPortalListener implements Listener {
    //使用插件 Multiworld 的部分代码
    private static PortalType getPortalType(Location location) {
        Block block = location.getBlock();
        Material material;
        for (BlockFace face : new BlockFace[]{BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST}) {
            material = block.getRelative(face).getType();
            if (material == Material.ENDER_PORTAL) {
                return PortalType.ENDER;
            } else if (material == Material.PORTAL) {
                return PortalType.NETHER;
            }
        }

        return PortalType.CUSTOM;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent playerPortalEvent) {
        TravelAgent travelAgent = playerPortalEvent.getPortalTravelAgent();
        Server server = R.getMainClass().getServer();
        World world = playerPortalEvent.getPlayer().getWorld();
        World theEndWorld;
        Location location = playerPortalEvent.getFrom();
        String worldName = world.getName();
        String worldNamePrefix = R.getConfig("worldNamePrefix");
        String spawnWorldName = R.getConfig("spawnWorldName");
        String roomWorldNamePrefix;
        String mainWorldName;
        String netherWorldName;
        String theEndWorldName;
        int indexOfThirdUnderline;

        try {
            if (!worldName.equals(spawnWorldName) && worldName.substring(0, worldNamePrefix.length()).equals(worldNamePrefix)) {    //当事件触发于EnderKiller插件生成的世界中时
                indexOfThirdUnderline = worldName.indexOf("_", worldNamePrefix.length() + 1);
                roomWorldNamePrefix = worldName.substring(0, indexOfThirdUnderline);
                mainWorldName = roomWorldNamePrefix + "_main";
                netherWorldName = roomWorldNamePrefix + "_nether";
                theEndWorldName = roomWorldNamePrefix + "_the_end";

                if (worldName.equals(mainWorldName)) {  //当此世界是主世界时
                    if (getPortalType(location) == PortalType.NETHER) { //当此传送门为地狱门时
                        location.setWorld(server.getWorld(netherWorldName));
                        location.setX(location.getX() / 8);
                        location.setZ(location.getZ() / 8);
                        location = travelAgent.findOrCreate(location);
                    } else if (getPortalType(location) == PortalType.ENDER) {   //当此传送门为末地门时
                        theEndWorld = server.getWorld(theEndWorldName);
                        //TODO 传送至末地应该有一个黑曜石平台,算法不明
                        //末地出生点的最高点(鸡巴型基岩祭坛顶端)
                        location = theEndWorld.getHighestBlockAt(theEndWorld.getSpawnLocation()).getLocation();
                        //当有人进入末地时,改变对应房间的游戏的状态至屠龙阶段
                        try {
                            Util.searchPlayer(playerPortalEvent.getPlayer().getName()).getGame().setGameStatus(GameStatus.slaughterDragon);
                        } catch (Exception e) {
                            //当此玩家未加入任何房间时
                        }
                    }
                } else if (worldName.equals(netherWorldName)) { //当此世界是地域
                    if (getPortalType(location) == PortalType.NETHER) { //当此传送门为地狱门时
                        location.setWorld(server.getWorld(mainWorldName));
                        location.setX(location.getX() * 8);
                        location.setZ(location.getZ() * 8);
                        location = travelAgent.findOrCreate(location);
                    }
                }

                //设置此传送门指向至对应位置
                playerPortalEvent.setTo(location);
            }
        } catch (Exception e) {

        }
    }
}
