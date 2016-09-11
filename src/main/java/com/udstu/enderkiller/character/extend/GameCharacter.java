package com.udstu.enderkiller.character.extend;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.Occupation;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    //潜伏者阵营的玩家初始获得一个恶魂之泪
    public void giveInitItems() {
        Material material = Material.GHAST_TEAR;
        int amount = 1;

        if (alignment == Alignment.lurker) {
            player.getInventory().addItem(new ItemStack(material, amount));
            player.sendMessage(R.getLang("youGet") + ": " + material.toString() + " * " + amount);
        }
    }

    public void kill() {
        player.setHealth(0);
    }

    public void onDeath() {
        gameCharacterStatus = GameCharacterStatus.dead;
        unsetTeamLeader();
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
        if (!isTeamLeader()) {
            player.setMaxHealth(player.getMaxHealth() + 10);
            player.setHealth(player.getHealth() + 10);
        }
        isTeamLeader = true;
    }

    public void unsetTeamLeader() {
        if (isTeamLeader()) {
            player.setMaxHealth(player.getMaxHealth() - 10);
        }
        isTeamLeader = false;
    }
}
