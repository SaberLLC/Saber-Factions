package com.massivecraft.factions.util;

import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

import java.util.*;

public class AsciiCompass {

    public enum Point {

        N('N'),
        NE('/'),
        E('E'),
        SE('\\'),
        S('S'),
        SW('/'),
        W('W'),
        NW('\\');

        private final char asciiChar;

        public static final Point[] VALUES = values();

        Point(char asciiChar) {
            this.asciiChar = asciiChar;
        }

        public Point getOppositePoint() {
            return VALUES[(ordinal() + 4) % 8];
        }

        @Override
        public String toString() {
            return Character.toString(this.asciiChar);
        }

        public String getTranslation() {
            switch(this) {
                case N:
                    return TL.COMPASS_SHORT_NORTH.toString();
                case E:
                    return TL.COMPASS_SHORT_EAST.toString();
                case S:
                    return TL.COMPASS_SHORT_SOUTH.toString();
                case W:
                    return TL.COMPASS_SHORT_WEST.toString();
                default:
                    return toString();
            }
        }

        public String toString(boolean isActive, ChatColor ACTIVE_COLOR, String colorDefault) {
            return (isActive ? ACTIVE_COLOR : colorDefault) + getTranslation();
        }

        public static Point fromAngle(float degrees) {
            return VALUES[FastMath.round(degrees / 45f + 4.5f) & 7];
        }
    }

    private static final ChatColor ACTIVE_COLOR = ChatColor.DARK_GREEN;
    private static final String DEFAULT_COLOR = TextUtil.parse("<gray>");

    private static final Map<Point, List<Component>> COMPASSES = new EnumMap<>(Point.class);

    static {
        for (Point point : Point.VALUES) {
            List<Component> ret = new ArrayList<>(3);

            StringBuilder builder = new StringBuilder(4);

            builder.append(Point.NW.toString(Point.NW == point, ACTIVE_COLOR, DEFAULT_COLOR))
                    .append(Point.N.toString(Point.N == point, ACTIVE_COLOR, DEFAULT_COLOR))
                    .append(Point.NE.toString(Point.NE == point, ACTIVE_COLOR, DEFAULT_COLOR));
            ret.add(TextUtil.parseFancy(builder.toString()).build());

            builder.setLength(0);
            builder.append(Point.W.toString(Point.W == point, ACTIVE_COLOR, DEFAULT_COLOR))
                    .append(DEFAULT_COLOR).append("+")
                    .append(Point.E.toString(Point.E == point, ACTIVE_COLOR, DEFAULT_COLOR));
            ret.add(TextUtil.parseFancy(builder.toString()).build());

            builder.setLength(0);
            builder.append(Point.SW.toString(Point.SW == point, ACTIVE_COLOR, DEFAULT_COLOR))
                    .append(Point.S.toString(Point.S == point, ACTIVE_COLOR, DEFAULT_COLOR))
                    .append(Point.SE.toString(Point.SE == point, ACTIVE_COLOR, DEFAULT_COLOR));
            ret.add(TextUtil.parseFancy(builder.toString()).build());

            COMPASSES.put(point, ret);
        }
    }

    private static List<Component> get(Point point) {
        return point == null ? Collections.emptyList() : COMPASSES.get(point);
    }

    public static List<Component> getAsciiCompass(float degrees) {
        return get(Point.fromAngle(degrees));
    }
}