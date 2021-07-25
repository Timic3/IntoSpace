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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.screens.GameScreen;
import com.intospace.world.Assets;
import com.intospace.world.WorldManager;

enum PointerSide {
    EAST, WEST
}

enum State {
    GROUND, JUMPING, FALLING
}

public class Player extends Entity implements InputProcessor {
    static final float MAX_SPEED = 4f;

    private float x;
    private float y;
    private final float width;
    private final float height;

    private float handRotation = 90;
    protected TextureAtlas.AtlasRegion idleTexture;
    protected TextureAtlas.AtlasRegion handTexture;
    private final Animation<TextureRegion> runningAnimation;
    private final OrthographicCamera camera;

    private boolean moveLeft;
    private boolean moveRight;

    private PointerSide pointerSide = PointerSide.WEST;

    private float runningTime;

    public Player(float x, float y, OrthographicCamera camera, World world) {
        super(x, y);
        final TextureAtlas atlas = IntoSpaceGame.getInstance().assets.get(Assets.PLAYER);

        this.idleTexture = atlas.findRegion("Player_Idle");
        this.handTexture = atlas.findRegion("Player_Hand");
        this.runningAnimation = new Animation<>(0.070f, atlas.findRegions("Player_Running"), Animation.PlayMode.LOOP);
        this.camera = camera;
        this.width = this.idleTexture.getRegionWidth();
        this.height = this.idleTexture.getRegionHeight();

        // Collisions
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + width / 2f) / Constants.PPM, (y + height) / Constants.PPM);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        PolygonShape smallPlayerShape = new PolygonShape();
        smallPlayerShape.setAsBox(width / 2f / Constants.PPM - 0.075f, height / 2f / Constants.PPM - 0.01f, new Vector2(0, -height / Constants.PPM / 2f + 0.1f), 0);
        FixtureDef fixtureDefSmall = new FixtureDef();
        fixtureDefSmall.shape = smallPlayerShape;
        fixtureDefSmall.friction = 0f;
        fixtureDefSmall.density = 0.1f;
        fixtureDefSmall.restitution = 0f;

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(width / 2f / Constants.PPM - 0.1f, height / 2f / Constants.PPM, new Vector2(0, -height / Constants.PPM / 2f + 0.1f), 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = playerShape;
        fixtureDef.friction = 2f;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        body.createFixture(fixtureDefSmall);
        playerShape.dispose();
        smallPlayerShape.dispose();

        body.setUserData(this);
    }

    public void update(float delta) {
        // THIS IS PERFECT!!!
        float impulse = this.getState() == State.GROUND ? 5f : 5f * delta;
        if (moveLeft) {
            body.applyLinearImpulse(-impulse, 0, body.getPosition().x, body.getPosition().y - 5.5f, true);
        }
        if (moveRight) {
            body.applyLinearImpulse(impulse, 0, body.getPosition().x, body.getPosition().y, true);
        }

        if (body.getLinearVelocity().x < -MAX_SPEED) {
            body.setLinearVelocity(-MAX_SPEED, body.getLinearVelocity().y);
        }
        if (body.getLinearVelocity().x > MAX_SPEED) {
            body.setLinearVelocity(MAX_SPEED, body.getLinearVelocity().y);
        }

        if (body.getPosition().x > Constants.MAX_X / 2f) {
            body.setTransform(body.getPosition().x - Constants.MAX_X / 2f, body.getPosition().y, 0);
            camera.position.set(camera.position.x - Constants.MAX_X / 2f, camera.position.y, 0);
        } else if (body.getPosition().x <= 0) {
            body.setTransform(body.getPosition().x + Constants.MAX_X / 2f, body.getPosition().y, 0);
            camera.position.set(camera.position.x + Constants.MAX_X / 2f, camera.position.y, 0);
        }
    }

    public void render(SpriteBatch batch) {
        if (this instanceof AIPlayer) {
            if (body.getLinearVelocity().x < 0) {
                pointerSide = PointerSide.EAST;
            } else if (body.getLinearVelocity().x > 0) {
                pointerSide = PointerSide.WEST;
            }
        }
        if (moveLeft || moveRight) {
            runningTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = runningAnimation.getKeyFrame(runningTime, true);
            batch.draw(currentFrame, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / 2f / Constants.PPM, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM);
        } else {
            batch.draw(idleTexture, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / 2f / Constants.PPM, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM);
        }
        if (handTexture != null) {
            batch.draw(handTexture, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / Constants.PPM / 2f, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -10 : 10) / Constants.PPM, height / 1.6f / Constants.PPM, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM, 1, 1, (pointerSide == PointerSide.EAST ? handRotation - 80 : handRotation + 180 + 80));
        }
        //batch.draw(idleTexture, body.getPosition().x - width / Constants.PPM / 2f, body.getPosition().y - height / Constants.PPM / 2f, width / Constants.PPM, height / Constants.PPM);
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
        return body.getPosition().x;
    }

    public float getOriginY() {
        return body.getPosition().y;
    }

    public State getState() {
        if (body.getLinearVelocity().y > 0) {
            return State.JUMPING;
        } else if (body.getLinearVelocity().y < 0) {
            return State.FALLING;
        }
        return State.GROUND;
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
            case Keys.W:
                if (getState() == State.GROUND) {
                    body.applyForceToCenter(0, 90f, true);
                }
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
        if (worldPosition.x > (body.getPosition().x))
            pointerSide = PointerSide.WEST;
        else
            pointerSide = PointerSide.EAST;
        handRotation = MathUtils.radiansToDegrees * MathUtils.atan2((body.getPosition().y + height / Constants.PPM / 2f) - worldPosition.y, (body.getPosition().x) - worldPosition.x);
        if (handRotation < 0)
            handRotation += 360;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
