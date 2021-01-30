package com.intospace.world;

enum WorldType {
    EARTH,
    ROCKY,

    WorldType() {

    }
}

public class WorldGenerator {
    WorldType type;
    long seed;

    public WorldGenerator(WorldType type, long seed) {
        this.type = type;
        this.seed = seed;
    }
}
