package com.massivecraft.factions.util;

import java.util.TreeMap;

public class RomanNumber {
    private static TreeMap<Integer, String> map;

    static {
        (map = new TreeMap<>()).put(1000, "M");
        RomanNumber.map.put(900, "CM");
        RomanNumber.map.put(500, "D");
        RomanNumber.map.put(400, "CD");
        RomanNumber.map.put(100, "C");
        RomanNumber.map.put(90, "XC");
        RomanNumber.map.put(50, "L");
        RomanNumber.map.put(40, "XL");
        RomanNumber.map.put(10, "X");
        RomanNumber.map.put(9, "IX");
        RomanNumber.map.put(5, "V");
        RomanNumber.map.put(4, "IV");
        RomanNumber.map.put(1, "I");
    }

    public static String toRoman(int number) {
        int l = RomanNumber.map.floorKey(number);
        if (number == l) {
            return RomanNumber.map.get(number);
        }
        return RomanNumber.map.get(l) + toRoman(number - l);
    }
}
