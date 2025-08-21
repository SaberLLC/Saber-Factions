package com.massivecraft.factions.util.spiral.generator;

import com.massivecraft.factions.util.spiral.coord.ChunkCoord;

import java.util.ArrayDeque;
import java.util.Queue;

public class SquareSpiralGenerator implements SpiralGenerator {

    @Override
    public Queue<ChunkCoord> generate(int centerX, int centerZ, int radius) {
        Queue<ChunkCoord> coords = new ArrayDeque<>();
        if (radius <= 0) return coords;

        coords.offer(new ChunkCoord(centerX, centerZ));

        int x = centerX;
        int z = centerZ;
        boolean isZLeg = false;
        boolean isNeg = false;
        int length = -1;
        int current = 0;
        int limit = (radius - 1) * 2;

        while (true) {
            if (current < length) {
                current++;
                if (current >= limit) break;
            } else {
                current = 0;
                isZLeg ^= true;
                if (isZLeg) {
                    isNeg ^= true;
                    length++;
                }
            }

            if (isZLeg) {
                z += (isNeg) ? -1 : 1;
            } else {
                x += (isNeg) ? -1 : 1;
            }

            coords.offer(new ChunkCoord(x, z));
        }

        return coords;
    }
}