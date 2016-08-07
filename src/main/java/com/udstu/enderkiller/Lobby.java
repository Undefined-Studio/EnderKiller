package com.udstu.enderkiller;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by czp on 16-8-7.
 * Lobby of EnderKiller
 */
public class Lobby {
    public static int roomId = 1;
    private static List<Room> roomList = new LinkedList<>();

    public static boolean add(Room room) {
        if (roomList.size() < Integer.valueOf(R.getConfig("maxRooms"))) {
            roomList.add(room);
            return true;
        } else {
            return false;
        }
    }

    public static List<Room> getRoomList() {
        return roomList;
    }
}
