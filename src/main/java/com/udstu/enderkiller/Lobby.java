package com.udstu.enderkiller;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by czp on 16-8-7.
 * Lobby of EnderKiller
 */
public class Lobby {
    private static int roomIdStamp = 1;
    private static List<Room> roomList = new LinkedList<>();

    public static boolean isFull() {
        return !(roomList.size() < Integer.valueOf(R.getConfig("maxRooms")));
    }

    public static void add(Room room) {
        roomList.add(room);
        roomIdStamp++;
    }

    public static boolean remove(Room room) {
        return roomList.remove(room);
    }

    public static List<Room> getRoomList() {
        return roomList;
    }

    public static int getRoomIdStamp() {
        return roomIdStamp;
    }
}
