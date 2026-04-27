package com.massivecraft.factions.util.spiral;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.util.spiral.coord.ChunkCoord;

public class ChunkProcessingContext {
    private final ChunkCoord coord;
    private final FLocation fLocation;

    public ChunkProcessingContext(ChunkCoord coord, FLocation fLocation) {
        this.coord = coord;
        this.fLocation = fLocation;
    }

    public ChunkCoord getCoord() {
        return coord;
    }

    public FLocation getFLocation() {
        return fLocation;
    }

    public int getX() {
        return coord.x;
    }

    public int getZ() {
        return coord.z;
    }
}