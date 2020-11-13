package com.intospace.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.intospace.game.Entity;

public class Player extends Entity implements InputProcessor {
    private float x;
    private float y = 320;
    private TextureAtlas.AtlasRegion idleTexture;
    private Animation<TextureRegion> runningAnimation;

    private boolean moveLeft;
    private boolean moveRight;

    private float runningTime;

    public Player(float x, float y) {
        super(x, y);
    }

    public Player(float x, float y, TextureAtlas.AtlasRegion idleTexture, Animation<TextureRegion> runningAnimation) {
        this(x, y);
        this.idleTexture = idleTexture;
        this.runningAnimation = runningAnimation;
    }

    public void update(float delta) {
        if (moveLeft) {
            x -= 250 * delta;
        }
        if (moveRight) {
            x += 250 * delta;
        }
    }

    public void render(SpriteBatch batch) {
        if (moveLeft || moveRight) {
            runningTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = runningAnimation.getKeyFrame(runningTime, true);
            batch.draw(currentFrame, x, y);
        } else {
            batch.draw(idleTexture, x, y);
        }
    }

    private void moveLeft(boolean state) {
        moveLeft = state;
        runningTime = 0;
    }

    private void moveRight(boolean state) {
        moveRight = state;
        runningTime = 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.A:
                moveLeft(true);
                break;
            case Keys.D:
                moveRight(true);
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.A:
                moveLeft(false);
                break;
            case Keys.D:
                moveRight(false);
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
