package com.udstu.enderkiller.worldcreator;

import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Created by czp on 16-8-30.
 * Nether World
 */
public class NetherWorldCreator extends WorldCreator {
    public NetherWorldCreator(String name) {
        super(name);
        super.environment(World.Environment.NETHER);
    }
}
