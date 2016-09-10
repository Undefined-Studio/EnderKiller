package com.udstu.enderkiller.task;

import com.udstu.enderkiller.game.extend.Game;
import org.bukkit.World;

/**
 * Created by czp on 16-9-2.
 * Run per tick
 */
public class TimeLapseTask implements Runnable {
    private Game game;
    private World world = null;

    public TimeLapseTask(Game game, World world) {
        this.game = game;
        this.world = world;
    }

    @Override
    public void run() {
        long time = world.getTime();
        if (time == 22200) {  //开始日出
            game.nextDay();
        } else if (time == 13800) {   //日落结束
            game.nextNight();
        }
    }
}
