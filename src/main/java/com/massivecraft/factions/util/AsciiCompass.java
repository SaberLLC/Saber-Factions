package com.massivecraft.factions.util;

import com.massivecraft.factions.zcore.util.TL;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class AsciiCompass {

    public static Point getCompassPointForDirection(double inDegrees) {
        double degrees = (inDegrees - 180) % 360;
        if (degrees < 0) {
            degrees += 360;
        }

        if (0 <= degrees && degrees < 22.5) {
            return Point.N;
        } else if (22.5 <= degrees && degrees < 67.5) {
            return Point.NE;
        } else if (67.5 <= degrees && degrees < 112.5) {
            return Point.E;
        } else if (112.5 <= degrees && degrees < 157.5) {
            return Point.SE;
        } else if (157.5 <= degrees && degrees < 202.5) {
            return Point.S;
        } else if (202.5 <= degrees && degrees < 247.5) {
            return Point.SW;
        } else if (247.5 <= degrees && degrees < 292.5) {
            return Point.W;
        } else if (292.5 <= degrees && degrees < 337.5) {
            return Point.NW;
        } else if (337.5 <= degrees && degrees < 360.0) {
            return Point.N;
        } else {
            return null;
        }
    }

    public static List<String> getAsciiCompass(Point point, ChatColor colorActive, String colorDefault) {
        if (point == null) {
            return new ObjectArrayList<>(0);
        }
        ObjectList<String> ret = new ObjectArrayList<>(3);

        StringBuilder builder = new StringBuilder();

        builder.append(Point.NW.toString(Point.NW == point, colorActive, colorDefault))
                .append(Point.N.toString(Point.N == point, colorActive, colorDefault))
                .append(Point.NE.toString(Point.NE == point, colorActive, colorDefault));
        ret.add(builder.toString());

        builder.append(Point.W.toString(Point.W == point, colorActive, colorDefault))
                .append(colorDefault).append("+")
                .append(Point.E.toString(Point.E == point, colorActive, colorDefault));
        ret.add(builder.toString());

        builder.append(Point.SW.toString(Point.SW == point, colorActive, colorDefault))
                .append(Point.S.toString(Point.S == point, colorActive, colorDefault))
                .append(Point.SE.toString(Point.SE == point, colorActive, colorDefault));
        ret.add(builder.toString());
        return ret;
    }

    public static List<String> getAsciiCompass(double inDegrees, ChatColor colorActive, String colorDefault) {
        return getAsciiCompass(getCompassPointForDirection(inDegrees), colorActive, colorDefault);
    }

    public enum Point {

        N('N'),
        NE('/'),
        E('E'),
        SE('\\'),
        S('S'),
        SW('/'),
        W('W'),
        NW('\\');

        public final char asciiChar;

        Point(final char asciiChar) {
            this.asciiChar = asciiChar;
        }

        @Override
        public String toString() {
            return String.valueOf(this.asciiChar);
        }

        public String getTranslation() {
            if (this == N) {
                return TL.COMPASS_SHORT_NORTH.toString();
            }
            if (this == E) {
                return TL.COMPASS_SHORT_EAST.toString();
            }
            if (this == S) {
                return TL.COMPASS_SHORT_SOUTH.toString();
            }
            if (this == W) {
                return TL.COMPASS_SHORT_WEST.toString();
            }
            return toString();
        }

        public String toString(boolean isActive, ChatColor colorActive, String colorDefault) {
            return (isActive ? colorActive : colorDefault) + getTranslation();
        }
    }
}
