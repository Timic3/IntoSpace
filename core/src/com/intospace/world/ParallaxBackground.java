package com.intospace.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.intospace.game.Constants;

public class ParallaxBackground {
    private int scroll;
    private Array<Texture> layers;
    private final int offsetY = 300;

    float x, y, width, height, scaleX, scaleY;
    int originX, originY, rotation, srcX, srcY;
    boolean flipX, flipY;

    private int speedX;
    private int speedY;

    public ParallaxBackground(Array<Texture> layers) {
        this.layers = layers;
        for (int i = 0; i < this.layers.size; i++) {
            this.layers.get(i).setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
            this.layers.get(i).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        this.scroll = 0;
        this.speedX = 3;
        this.speedY = 5;

        x = y = originX = originY = rotation = srcY = 0;
        width = 1920;
        height = 500;
        scaleX = scaleY = 1;
        flipX = flipY = false;
    }

    public void draw(Batch batch, float cameraX, float cameraY) {
        cameraX *= speedX;
        batch.begin();
        for (int i = 0; i < layers.size; i++) {
            srcX = (int) (cameraX + i * 2 * cameraX);
            srcY = (int) -(cameraY - 150 + i * cameraY);
            batch.draw(layers.get(i), x, y, originX, originY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), scaleX, scaleY, rotation, srcX, srcY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), flipX, flipY);
        }
        batch.end();
    }
}
