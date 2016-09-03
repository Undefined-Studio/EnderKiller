package com.udstu.enderkiller.enumeration;

import com.udstu.enderkiller.R;

/**
 * Created by czp on 16-9-2.
 */
public enum GameCharacterStatus {
    alive,
    dead;

    @Override
    public String toString() {
        return R.getLang(super.toString());
    }
}
