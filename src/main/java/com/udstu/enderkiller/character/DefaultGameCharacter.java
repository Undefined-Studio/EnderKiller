package com.udstu.enderkiller.character;

import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.character.extend.GameCharacter;
import org.bukkit.entity.Player;

/**
 * Created by czp on 16-9-4.
 * 用于生成实际职业之前的替代
 */
public class DefaultGameCharacter extends GameCharacter {
    public DefaultGameCharacter(Player player, Room room) {
        super(player, room);
    }

    @Override
    public void nextNight() {

    }

    @Override
    public void nextDay() {

    }
}
