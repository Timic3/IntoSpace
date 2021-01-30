package com.intospace.world.generators;

public class PerlinNoise {
    private static final double FREQUENCY = 2;
    long seed;

    public PerlinNoise(long seed) {
        this.seed = seed;
    }

    public int getNoise(int x, int range) {
        int chunkSize = 64;
        float noise = 0;
        range /= FREQUENCY;

        while (chunkSize > 0) {
            int chunkIndex = x / chunkSize;
            float progress = (x % chunkSize) / ((float) chunkSize);
            float leftRandom = random(chunkIndex, range);
            float rightRandom = random(chunkIndex + 1, range);

            noise += (1 - progress) * leftRandom + progress * rightRandom;

            chunkSize /= FREQUENCY;
            range /= FREQUENCY;
            range = Math.max(range, 1);
        }

        return Math.round(noise);
    }

    private int random(int x, int range) {
        return (int) (x + seed) % range;
    }
}
