package com.udstu.enderkiller.character.extend;

import com.udstu.enderkiller.enumeration.Alignment;
import org.bukkit.entity.Player;

/**
 * Created by czp on 16-8-12.
 * 所有职业的父类
 */
public class Character {
    protected Player player = null;
    protected Alignment alignment = null;

    public Character() {

    }

    public Character(Player player) {
        this.player = player;
    }

    public Character(Character character, Alignment alignment) {
        this.player = character.getPlayer();
        this.alignment = alignment;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
}
