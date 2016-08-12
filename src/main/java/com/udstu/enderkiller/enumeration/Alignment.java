package com.udstu.enderkiller.enumeration;

import com.udstu.enderkiller.R;

/**
 * Created by czp on 16-8-12.
 * Alignment enum
 */
public enum Alignment {
    explorer,
    lurker;

    @Override
    public String toString() {
        return R.getLang(super.toString());
    }
}
