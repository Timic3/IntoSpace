package com.intospace.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.intospace.game.Entity;

enum PointerSide {
    EAST, WEST
}

public class Player extends Entity implements InputProcessor {
    private float x;
    private float y = 320;
    private float handRotation;
    private TextureAtlas.AtlasRegion idleTexture;
    private TextureAtlas.AtlasRegion handTexture;
    private Animation<TextureRegion> runningAnimation;
    private OrthographicCamera camera;

    private boolean moveLeft;
    private boolean moveRight;

    private PointerSide pointerSide = PointerSide.WEST;

    private float runningTime;

    public Player(float x, float y) {
        super(x, y);
    }

    public Player(float x, float y, TextureAtlas.AtlasRegion idleTexture, TextureAtlas.AtlasRegion handTexture, Animation<TextureRegion> runningAnimation, OrthographicCamera camera) {
        this(x, y);
        this.idleTexture = idleTexture;
        this.handTexture = handTexture;
        this.runningAnimation = runningAnimation;
        this.camera = camera;
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
        int width = idleTexture.getRegionWidth();
        if (moveLeft || moveRight) {
            runningTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = runningAnimation.getKeyFrame(runningTime, true);
            batch.draw(currentFrame, x + (pointerSide == PointerSide.EAST ? width : 0), y, (pointerSide == PointerSide.EAST ? -width : width), currentFrame.getRegionHeight());
        } else {
            batch.draw(idleTexture, x + (pointerSide == PointerSide.EAST ? width : 0), y, (pointerSide == PointerSide.EAST ? -width : width), idleTexture.getRegionHeight());
        }
        batch.draw(handTexture, x + (pointerSide == PointerSide.EAST ? width : 0), y, (pointerSide == PointerSide.EAST ? -10 : 10), handTexture.getRegionHeight() / 1.6f, (pointerSide == PointerSide.EAST ? -width : width), handTexture.getRegionHeight(), 1, 1, (pointerSide == PointerSide.EAST ? handRotation - 80 : handRotation + 180 + 80));
    }

    private void moveLeft(boolean state) {
        moveLeft = state;
        runningTime = 0;
    }

    private void moveRight(boolean state) {
        moveRight = state;
        runningTime = 0;
    }

    public float getOriginX() {
        return x + idleTexture.getRegionWidth() / 2f;
    }

    public float getOriginY() {
        return y + idleTexture.getRegionHeight() / 2f;
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
        return false;
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
        Vector3 worldPosition = camera.unproject(new Vector3(screenX, screenY, 0));
        if (worldPosition.x > this.x + this.idleTexture.getRegionWidth() / 2f)
            pointerSide = PointerSide.WEST;
        else
            pointerSide = PointerSide.EAST;
        handRotation = MathUtils.radiansToDegrees * MathUtils.atan2((this.y + this.idleTexture.getRegionHeight() / 2f) - worldPosition.y, (this.x + this.idleTexture.getRegionWidth() / 2f) - worldPosition.x);
        if (handRotation < 0)
            handRotation += 360;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
