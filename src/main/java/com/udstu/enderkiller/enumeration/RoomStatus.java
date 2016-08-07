package com.udstu.enderkiller.enumeration;

import com.udstu.enderkiller.R;

/**
 * Created by czp on 16-8-8.
 * Room status enum
 */
public enum RoomStatus {
    inGame,
    waitingForStart;

    @Override
    public String toString() {
        return R.getLang(super.toString());
    }
}
