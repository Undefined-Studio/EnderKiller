package com.udstu.enderkiller.character.extend;

import com.udstu.enderkiller.enumeration.Alignment;
import org.bukkit.entity.Player;

/**
 * Created by czp on 16-8-12.
 * 所有职业的父类
 */
public class GameCharacter {
    protected Player player = null;
    protected Alignment alignment = null;

    public GameCharacter() {

    }

    public GameCharacter(Player player) {
        this.player = player;
    }

    public GameCharacter(GameCharacter gameCharacter, Alignment alignment) {
        this.player = gameCharacter.getPlayer();
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
