package com.udstu.enderkiller;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.RoomStatus;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by czp on 16-8-7.
 * Room of EnderKiller
 */
public class Room {
    private List<GameCharacter> gameCharacters;
    private String name;
    private int slot;
    private int id;
    private RoomStatus status = null;
    private Scoreboard scoreboard = null;
    private Objective objective = null;

    //模式以游戏人数命名,例如12人局则传入12,此时最大人数为12人
    public Room(String name, int mode) {
        gameCharacters = new LinkedList<>();
        this.name = name;
        slot = mode;
        id = Lobby.getRoomIdStamp();
        status = RoomStatus.waitingForStart;

        scoreboard = R.getScoreboardManager().getNewScoreboard();
    }

    public boolean isFull() {
        return !(gameCharacters.size() < slot);
    }

    //是否存在玩家
    public boolean isExistPlayer(Player player) {
        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getPlayer().getName().equals(player.getName())) {
                return true;
            }
        }

        return false;
    }

    public boolean isExistPlayer(String playerName) {
        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getPlayer().getName().equals(playerName)) {
                return true;
            }
        }

        return false;
    }

    public void add(GameCharacter gameCharacter) {
        gameCharacters.add(gameCharacter);
        updateScoreBoard();
    }

    public boolean remove(Player player) {
        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getPlayer().getName().equals(player.getName())) {
                gameCharacters.remove(gameCharacter);
                player.setScoreboard(R.getScoreboardManager().getNewScoreboard());
                updateScoreBoard();
                return true;
            }
        }

        return false;
    }

    //获取对应玩家的角色指针
    public GameCharacter getGameCharacter(String playerName) {
        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getPlayer().getName().equals(playerName)) {
                return gameCharacter;
            }
        }

        return null;
    }

    public void updateScoreBoard() {
        Player player;
        String playerName;

        if (objective != null) {
            objective.unregister();
        }
        objective = scoreboard.registerNewObjective("CharacterList", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(R.getLang("roomId") + " " + ChatColor.YELLOW + id);

        for (int i = 1; i <= gameCharacters.size(); i++) {
            player = gameCharacters.get(i - 1).getPlayer();
            playerName = "";

            //玩家不在线时使用黑色显示
            if (!player.isOnline()) {
                playerName += ChatColor.BLACK;
            }

            playerName += player.getName();

            objective.getScore(playerName).setScore(i);

            player.setScoreboard(scoreboard);
        }
    }

    public void addToScoreBoard(String playerName) {

    }

    public List<GameCharacter> getGameCharacters() {
        return gameCharacters;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public int getId() {
        return id;
    }

    public RoomStatus getStatus() {
        return status;
    }
}
