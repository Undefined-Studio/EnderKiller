package com.udstu.enderkiller.worldcreator;

import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Created by czp on 16-8-30.
 * 主世界
 */
public class MainWorldCreator extends WorldCreator {
    public MainWorldCreator(String name) {
        super(name);
        super.environment(World.Environment.NORMAL);
    }
}
