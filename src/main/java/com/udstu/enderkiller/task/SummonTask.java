package com.udstu.enderkiller.task;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;

import java.util.List;

/**
 * Created by czp on 16-9-15.
 * 召集任务
 */
public class SummonTask implements Runnable {
    private List<GameCharacter> gameCharacters = null;
    private GameCharacter teamLeader = null;

    public SummonTask(List<GameCharacter> gameCharacters, GameCharacter teamLeader) {
        this.gameCharacters = gameCharacters;
        this.teamLeader = teamLeader;
    }

    //将存活且不为队长的角色传送至队长所在位置
    @Override
    public void run() {
        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getGameCharacterStatus() == GameCharacterStatus.alive && !gameCharacter.getPlayer().getName().equals(teamLeader.getPlayer().getName())) {
                gameCharacter.getPlayer().sendMessage(R.getLang("teleporting"));
                gameCharacter.getPlayer().teleport(teamLeader.getPlayer());
            }
        }

        teamLeader.setSummonTask(null);
    }
}
