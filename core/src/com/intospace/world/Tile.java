package com.intospace.world;

public class Tile {
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;

    private Tile(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
