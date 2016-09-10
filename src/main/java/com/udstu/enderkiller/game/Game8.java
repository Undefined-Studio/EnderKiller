package com.udstu.enderkiller.game;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.game.extend.Game;

/**
 * Created by czp on 16-8-25.
 * 八人场模式
 */
public class Game8 extends Game {
    public Game8(Room room) {
        super(room);
    }

    @Override
    protected void initOccupation() {
        //潜伏者2人,探险家6人
        alignments = new Alignment[]{
                Alignment.lurker, Alignment.lurker,
                Alignment.explorer, Alignment.explorer, Alignment.explorer, Alignment.explorer, Alignment.explorer, Alignment.explorer
        };
        //潜伏者阵营:巫师1人,普通人1人
        lurkers = new Occupation[]{
                Occupation.warlock,
                Occupation.ordinariness
        };
        //探险者阵营:牧师1人,斗士1人,侦探1人,普通人3人
        explorers = new Occupation[]{
                Occupation.priest, Occupation.fighter, Occupation.detective,
                Occupation.ordinariness, Occupation.ordinariness, Occupation.ordinariness
        };
    }
}
