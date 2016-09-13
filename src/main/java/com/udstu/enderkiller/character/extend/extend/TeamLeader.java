package com.udstu.enderkiller.character.extend.extend;

import org.bukkit.entity.Player;

/**
 * Created by czp on 16-9-13.
 * 队长
 */
public class TeamLeader {
    protected Player player = null;
    private boolean isTeamLeader = false;

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
