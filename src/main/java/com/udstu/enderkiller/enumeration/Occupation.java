package com.udstu.enderkiller.enumeration;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.character.extend.GameCharacter;

import java.lang.reflect.Constructor;

/**
 * Created by czp on 16-8-12.
 * 职业列表
 */
public enum Occupation {
    ordinariness,
    warlock,
    escapee,
    enchanter,
    apprentice,
    lawyer,
    priest,
    detective,
    teacher,
    fighter;

    //根据enum值返回GameCharacter的子类,即实际职业类型
    @SuppressWarnings("unchecked")
    public GameCharacter newGameCharacter(GameCharacter gameCharacter, Alignment alignment) {
        try {
            String className = super.toString();
            className = String.valueOf(Character.toUpperCase(className.charAt(0))) + className.substring(1);

            Class clazz = Class.forName("com.udstu.enderkiller.character." + className);
            Constructor constructor = clazz.getConstructor(GameCharacter.class, Alignment.class);

            return (GameCharacter) constructor.newInstance(gameCharacter, alignment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return R.getLang(super.toString());
    }
}
