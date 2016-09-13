package com.udstu.enderkiller.worldcreator;

import com.udstu.enderkiller.R;
import org.bukkit.WorldCreator;

/**
 * Created by czp on 16-8-28.
 * 主城世界
 */
public class SpawnWorldCreator extends WorldCreator {
    public SpawnWorldCreator(String name) {
        super(name);
        //种子
        seed(Long.valueOf(R.getConfig("spawnWorldSeed")));
    }
}
