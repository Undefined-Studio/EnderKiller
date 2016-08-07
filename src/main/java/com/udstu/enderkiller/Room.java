package com.udstu.enderkiller;

import com.udstu.enderkiller.enumeration.RoomStatus;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by czp on 16-8-7.
 * Room of EnderKiller
 */
public class Room {
    private List<Player> players;
    private String name;
    private int slot;
    private int id;
    private RoomStatus status = RoomStatus.waitingForStart;

    //模式以游戏人数命名,例如12人局则传入12,此时最大人数12人
    public Room(String name, int mode) {
        players = new LinkedList<>();
        this.name = name;
        slot = mode;
        id = Lobby.roomId++;
    }

    public boolean add(Player player) {
        if (players.size() < slot) {
            players.add(player);
            return true;
        } else {
            return false;
        }
    }

    public List<Player> getPlayers() {
        return players;
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
