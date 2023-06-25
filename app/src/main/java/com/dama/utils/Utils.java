package com.dama.utils;

public class Utils {
    public static String colorToString(int color){
        //Convert int color in String #RRGGBB
        return String.format("#%06X", (0xFFFFFF & color));
    }
    public static boolean isIntPresent(int[] array, int target) {
        for (int num : array) {
            if (num == target)
                return true;
        }
        return false;
    }

    public static boolean isCharPresent(char[] array, char target) {
        for (char c : array) {
            if (c == target)
                return true;
        }
        return false;
    }

    public static int[] removeIntFromArray(int[] array, int target){
        int[] newArray = new int[array.length-1];
        int j=0;
        for (int i: array){
            if(i!=target){
                newArray[j]=i;
                j++;
            }
        }
        return newArray;
    }
}
