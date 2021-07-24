package com.intospace.world.generators;

public class PerlinNoise {
    long seed;
    double frequency;

    public PerlinNoise(long seed, double frequency) {
        this.seed = seed;
        this.frequency = frequency;
    }

    public int getNoise(int x, int range) {
        int chunkSize = 64;
        float noise = 0;
        range /= frequency;

        while (chunkSize > 0) {
            int chunkIndex = x / chunkSize;
            float progress = (x % chunkSize) / ((float) chunkSize);
            float leftRandom = random(chunkIndex, range);
            float rightRandom = random(chunkIndex + 1, range);

            noise += (1 - progress) * leftRandom + progress * rightRandom;

            chunkSize /= frequency;
            range /= frequency;
            range = Math.max(range, 1);
        }

        return Math.round(noise);
    }

    private int random(int x, int range) {
        return (int) (x + seed) % range;
    }
}
