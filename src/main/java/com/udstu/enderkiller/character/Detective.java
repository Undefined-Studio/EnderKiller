package com.udstu.enderkiller.character;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;

/**
 * Created by czp on 16-8-12.
 * 侦探
 */
public class Detective extends GameCharacter {
    public Detective(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.detective;
    }

    @Override
    public void nextDay() {

    }

    @Override
    public void nextNight() {

    }
}
