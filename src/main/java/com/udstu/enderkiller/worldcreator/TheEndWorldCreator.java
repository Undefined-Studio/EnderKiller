package com.udstu.enderkiller.worldcreator;

import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Created by czp on 16-8-30.
 * The end world
 */
public class TheEndWorldCreator extends WorldCreator {
    public TheEndWorldCreator(String name) {
        super(name);
        super.environment(World.Environment.THE_END);
    }
}
