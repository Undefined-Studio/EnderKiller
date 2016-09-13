package com.udstu.enderkiller.character;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;

/**
 * Created by czp on 16-8-12.
 * 普通人
 */
public class Ordinariness extends GameCharacter {
    public Ordinariness(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.ordinariness;
    }

    @Override
    public void nextDay() {

    }

    @Override
    public void nextNight() {

    }
}
