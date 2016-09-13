package com.udstu.enderkiller.character;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;

/**
 * Created by czp on 16-8-12.
 * 斗士
 */
public class Fighter extends GameCharacter {
    public Fighter(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.fighter;
    }

    @Override
    public void nextDay() {

    }

    @Override
    public void nextNight() {

    }
}
