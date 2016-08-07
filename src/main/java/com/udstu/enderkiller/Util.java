package com.udstu.enderkiller;

/**
 * Created by czp on 16-8-7.
 * Util class
 */
public class Util {
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
