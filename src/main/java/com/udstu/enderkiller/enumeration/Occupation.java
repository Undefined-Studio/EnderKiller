package com.udstu.enderkiller.enumeration;

import com.udstu.enderkiller.R;

/**
 * Created by czp on 16-8-12.
 * 职业列表
 */
public enum Occupation {
    ordinariness,
    warlock,
    escapee,
    enchanter,
    apprentice,
    lawyer,
    priest,
    detective,
    teacher,
    fighter;

    @Override
    public String toString() {
        return R.getLang(super.toString());
    }
}
