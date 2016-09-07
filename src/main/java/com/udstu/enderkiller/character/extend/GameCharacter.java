package com.udstu.enderkiller.character.extend;

import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.Occupation;
import org.bukkit.entity.Player;

/**
 * Created by czp on 16-8-12.
 * 所有职业的父类
 */
public abstract class GameCharacter {
    protected Player player = null;
    protected Alignment alignment = null;
    protected Occupation occupation = null;
    private GameCharacterStatus gameCharacterStatus = GameCharacterStatus.alive;
    private boolean isTeamLeader = false;

    public GameCharacter(Player player) {
        this.player = player;
    }

    public GameCharacter(GameCharacter gameCharacter, Alignment alignment) {
        this.player = gameCharacter.getPlayer();
        this.alignment = alignment;
    }

    public abstract void nextDay();

    public abstract void nextNight();

    public void killed() {
        player.setHealth(0);
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

    public GameCharacterStatus getGameCharacterStatus() {
        return gameCharacterStatus;
    }

    public void setGameCharacterStatus(GameCharacterStatus gameCharacterStatus) {
        this.gameCharacterStatus = gameCharacterStatus;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public boolean isTeamLeader() {
        return isTeamLeader;
    }

    public void setTeamLeader() {
        isTeamLeader = true;
        player.setMaxHealth(player.getMaxHealth() + 10);
        player.setHealth(player.getHealth() + 10);
    }

    public void unsetTeamLeader() {
        isTeamLeader = false;
        player.setMaxHealth(20);
    }
}
