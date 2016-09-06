package com.udstu.enderkiller.listener;

import com.udstu.enderkiller.R;
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
    //The code from Multiworld
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
            if (!worldName.equals(spawnWorldName) && worldName.substring(0, worldNamePrefix.length()).equals(worldNamePrefix)) {
                indexOfThirdUnderline = worldName.indexOf("_", worldNamePrefix.length() + 1);
                roomWorldNamePrefix = worldName.substring(0, indexOfThirdUnderline);
                mainWorldName = roomWorldNamePrefix + "_main";
                netherWorldName = roomWorldNamePrefix + "_nether";
                theEndWorldName = roomWorldNamePrefix + "_the_end";

                if (worldName.equals(mainWorldName)) {
                    if (getPortalType(location) == PortalType.NETHER) {
                        location.setWorld(server.getWorld(netherWorldName));
                        location.setX(location.getX() / 8);
                        location.setZ(location.getZ() / 8);
                        location = travelAgent.findOrCreate(location);
                    } else if (getPortalType(location) == PortalType.ENDER) {
                        //(100,54,0)是默认的末地出生点
                        location = new Location(server.getWorld(theEndWorldName), 100, 54, 0);
                    }
                } else if (worldName.equals(netherWorldName)) {
                    if (getPortalType(location) == PortalType.NETHER) {
                        location.setWorld(server.getWorld(mainWorldName));
                        location.setX(location.getX() * 8);
                        location.setZ(location.getZ() * 8);
                        location = travelAgent.findOrCreate(location);
                    }
                }

                playerPortalEvent.setTo(location);
            }
        } catch (Exception e) {

        }
    }
}
