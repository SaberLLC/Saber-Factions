package com.massivecraft.factions.util.spiral.generator;

import com.massivecraft.factions.util.spiral.coord.ChunkCoord;

import java.util.Queue;

public interface SpiralGenerator {
    Queue<ChunkCoord> generate(int centerX, int centerZ, int radius);
}