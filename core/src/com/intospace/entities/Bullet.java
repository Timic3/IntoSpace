package com.intospace.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Bullet extends Entity {
    public TextureAtlas.AtlasRegion texture;

    public Bullet(float x, float y, int width, int height) {
        super(x, y, width, height);
    }
}
