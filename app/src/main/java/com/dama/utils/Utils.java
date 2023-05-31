package com.dama.utils;

public class Utils {
    public static String colorToString(int color){
        //Convert int color in String #RRGGBB
        return String.format("#%06X", (0xFFFFFF & color));
    }


}
