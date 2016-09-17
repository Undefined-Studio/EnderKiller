package com.udstu.enderkiller;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.RoomStatus;
import com.udstu.enderkiller.game.Game8;
import com.udstu.enderkiller.game.extend.Game;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
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
    private RoomStatus roomStatus = null;
    private Scoreboard scoreboard = null;
    private Objective objective = null;
    private Game game = null;

    //模式以游戏人数命名,例如12人局则传入12,此时最大人数为12人
    public Room(String name, int mode) {
        gameCharacters = new LinkedList<>();
        this.name = name;
        slot = mode;
        id = Lobby.getRoomIdStamp();
        roomStatus = RoomStatus.waitingForStart;

        scoreboard = R.getScoreboardManager().getNewScoreboard();
    }

    public boolean isFull() {
        return !(gameCharacters.size() < slot);
    }

    //是否存在玩家
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

    //TODO 每个玩家独立的scoreboard
    //更新计分板
    public void updateScoreBoard() {
        Player player;
        String displayPlayerName;
        String objectiveDisplayName = "";
        GameCharacter gameCharacter;

        if (objective != null) {
            objective.unregister();
        }
        objective = scoreboard.registerNewObjective("CharacterList", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objectiveDisplayName += R.getLang("roomId") + " " + ChatColor.YELLOW + id;
        if (game != null) {
            objectiveDisplayName += ChatColor.RESET + ", " + "Day " + ChatColor.YELLOW + game.getDay();
        }
        objective.setDisplayName(objectiveDisplayName);

        for (int i = 1; i <= gameCharacters.size(); i++) {
            player = gameCharacters.get(i - 1).getPlayer();
            gameCharacter = getGameCharacter(player.getName());
            displayPlayerName = "";

            //玩家为队长时增加前缀
            if (gameCharacter.isTeamLeader()) {
                displayPlayerName += "[" + R.getLang("teamLeader") + "]";
            }
            //玩家不在线时使用黑色显示
            if (!player.isOnline()) {
                displayPlayerName += ChatColor.BLACK;
            }
            //角色死亡时使用删去线
            if (gameCharacter.getGameCharacterStatus() == GameCharacterStatus.dead) {
                displayPlayerName += ChatColor.STRIKETHROUGH;
            }

            displayPlayerName += player.getName();

            objective.getScore(displayPlayerName).setScore(i);

            player.setScoreboard(scoreboard);
        }
    }

    //开始游戏
    public boolean startGame() {
        if (!Boolean.valueOf(R.getConfig("canGameStartWhenRoomNotFull"))) {
            //房间未满时无法开始游戏
            if (!isFull()) {
                return false;
            }
        }
        //房间不处于等待开始状态时不允许开始游戏
        if (roomStatus != RoomStatus.waitingForStart) {
            return false;
        }

        roomStatus = RoomStatus.inGame;

        switch (slot) {
            case 8: {
                game = new Game8(this);
            }
            break;
        }

        game.start();

        return true;
    }

    public void broadcast(String message) {
        for (GameCharacter gameCharacter : gameCharacters) {
            gameCharacter.getPlayer().sendMessage(message);
        }
    }

    public List<GameCharacter> getAliveGameCharacter() {
        List<GameCharacter> aliveGameCharacter = new ArrayList<>();

        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getGameCharacterStatus() == GameCharacterStatus.alive) {
                aliveGameCharacter.add(gameCharacter);
            }
        }

        return aliveGameCharacter;
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

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
