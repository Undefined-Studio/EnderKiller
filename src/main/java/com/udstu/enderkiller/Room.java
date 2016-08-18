package com.udstu.enderkiller;

import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.RoomStatus;
import org.bukkit.entity.Player;

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
    private RoomStatus status;

    //模式以游戏人数命名,例如12人局则传入12,此时最大人数为12人
    public Room(String name, int mode) {
        gameCharacters = new LinkedList<>();
        this.name = name;
        slot = mode;
        id = Lobby.getRoomIdStamp();
        status = RoomStatus.waitingForStart;
    }

    public boolean isFull() {
        return !(gameCharacters.size() < slot);
    }

    public void add(GameCharacter gameCharacter) {
        gameCharacters.add(gameCharacter);
    }

    public boolean remove(Player player) {
        for (GameCharacter gameCharacter : gameCharacters) {
            if (gameCharacter.getPlayer() == player) {
                return gameCharacters.remove(gameCharacter);
            }
        }

        return false;
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
