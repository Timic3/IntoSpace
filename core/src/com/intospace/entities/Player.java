package com.intospace.entities;

import box2dLight.ConeLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
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
import com.intospace.world.Assets;
import com.intospace.world.CollisionBits;
import com.intospace.world.WorldManager;
import com.intospace.world.inventory.InventoryManager;

enum PointerSide {
    EAST, WEST
}

enum State {
    GROUND, JUMPING, FALLING
}

public class Player extends Entity implements InputProcessor, ContactListener {
    static final float MAX_SPEED = 4f;

    private float x;
    private float y;
    private final float width;
    private final float height;

    private int health = 15;
    private int armor = 0;
    private float gracePeriod = 0;

    private float handRotation = 90;

    protected TextureAtlas.AtlasRegion idleTexture;
    protected TextureAtlas.AtlasRegion handTexture;
    protected TextureAtlas.AtlasRegion heldItem;

    private final Animation<TextureRegion> runningAnimation;
    private final OrthographicCamera camera;

    private boolean moveLeft;
    private boolean moveRight;

    private PointerSide pointerSide = PointerSide.WEST;

    private float runningTime;

    private final ConeLight pointLight;

    public Player(float x, float y, OrthographicCamera camera, World world) {
        super(x, y);
        final TextureAtlas atlas = IntoSpaceGame.getInstance().assets.get(Assets.PLAYER);

        this.idleTexture = atlas.findRegion("Player_Idle");
        this.handTexture = atlas.findRegion("Player_Hand");
        this.heldItem = atlas.findRegion("Plasma_Tool");

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
        fixtureDefSmall.filter.categoryBits = CollisionBits.PLAYER; // 0b101
        fixtureDefSmall.filter.groupIndex = (short) 0;
        fixtureDefSmall.filter.maskBits = CollisionBits.TERRAIN | CollisionBits.ENEMY; // 0b110

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(width / 2f / Constants.PPM - 0.1f, height / 2f / Constants.PPM, new Vector2(0, -height / Constants.PPM / 2f + 0.1f), 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = playerShape;
        fixtureDef.friction = 2f;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = CollisionBits.PLAYER; // 0b101
        fixtureDef.filter.groupIndex = (short) 0;
        fixtureDef.filter.maskBits = CollisionBits.TERRAIN | CollisionBits.ENEMY; // 0b110

        body.createFixture(fixtureDef);
        body.createFixture(fixtureDefSmall);
        playerShape.dispose();
        smallPlayerShape.dispose();

        world.setContactListener(this);

        pointLight = new ConeLight(
                WorldManager.getInstance().getRayHandler(),
                600,
                new Color(1f, 1f, 1f, 1f),
                10,
                body.getPosition().x,
                body.getPosition().y,
                0, 20
        );

        body.setUserData(this);

        InventoryManager.getInstance().inventory.put(this.heldItem, -1);
    }

    public void update(float delta) {
        // THIS IS PERFECT!!!
        float impulse = this.getState() == State.GROUND ? 3f : 3f * delta;
        if (moveLeft) {
            body.applyLinearImpulse(-impulse, 0, body.getPosition().x, body.getPosition().y, true);
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

        if (gracePeriod > 0) {
            gracePeriod = Math.max(0, gracePeriod - 1 * delta);
        }

        pointLight.setPosition(body.getPosition().x, body.getPosition().y);
        pointLight.setDirection(handRotation + 180);
        pointLight.update();
    }

    public void render(SpriteBatch batch) {
        if (gracePeriod > 0) {
            batch.setColor(1f, 0.6f, 0.5f, 1f);
        }
        if (moveLeft || moveRight) {
            runningTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = runningAnimation.getKeyFrame(runningTime, true);
            batch.draw(currentFrame, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / 2f / Constants.PPM, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM);
        } else {
            batch.draw(idleTexture, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / 2f / Constants.PPM, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM);
        }

        if (heldItem != null) {
            batch.draw(heldItem, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / Constants.PPM / 2f, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -10 : 10) / Constants.PPM, height / 1.6f / Constants.PPM, (pointerSide == PointerSide.EAST ? -20 : 20) / Constants.PPM, 20 / Constants.PPM, 1, 1, (pointerSide == PointerSide.EAST ? handRotation - 90 : handRotation + 180 + 90));
        }

        if (handTexture != null) {
            batch.draw(handTexture, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / Constants.PPM / 2f, body.getPosition().y - height / Constants.PPM + 0.1f, (pointerSide == PointerSide.EAST ? -10 : 10) / Constants.PPM, height / 1.6f / Constants.PPM, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM, 1, 1, (pointerSide == PointerSide.EAST ? handRotation - 80 : handRotation + 180 + 80));
        }
        batch.setColor(1f, 1f, 1f, 1f);
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
            case Keys.NUMPAD_6:
                if (Constants.DEBUG) this.armor += 1;
                break;
            case Keys.NUM_1:
            case Keys.NUM_2:
            case Keys.NUM_3:
            case Keys.NUM_4:
            case Keys.NUM_5:
            case Keys.NUM_6:
            case Keys.NUM_7:
            case Keys.NUM_8:
            case Keys.NUM_9:
                InventoryManager.getInstance().setSelectedIndex(keycode - Keys.NUM_1);
                this.heldItem = InventoryManager.getInstance().getHeldAtlas();
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
            case Keys.U:
                if (Constants.DEBUG) this.body.setLinearVelocity(0, 10);
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
        this.moveHand(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.moveHand(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        this.heldItem = InventoryManager.getInstance().getHeldAtlas();
        return false;
    }

    public int getHealth() {
        return health;
    }

    public int getArmor() {
        return armor;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void damage(int health) {
        if (this.armor > 0) {
            this.armor -= health;
            return;
        }
        this.health -= health;
    }

    private void moveHand(int screenX, int screenY) {
        Vector3 worldPosition = camera.unproject(new Vector3(screenX, screenY, 0));
        if (worldPosition.x > (body.getPosition().x))
            pointerSide = PointerSide.WEST;
        else
            pointerSide = PointerSide.EAST;
        handRotation = MathUtils.radiansToDegrees * MathUtils.atan2(-(worldPosition.y - body.getPosition().y), -(worldPosition.x - body.getPosition().x));
        if (handRotation < 0)
            handRotation += 360;
    }

    @Override
    public void beginContact(Contact contact) {
        Body contactBodyA = contact.getFixtureA().getBody();
        Body contactBodyB = contact.getFixtureB().getBody();
        if (
                (contactBodyB.getUserData() instanceof Enemy && contactBodyA.getUserData() instanceof Player) ||
                (contactBodyB.getUserData() instanceof Player && contactBodyA.getUserData() instanceof Enemy)
        ) {
            if (gracePeriod == 0) {
                if (contact.getFixtureB().getBody().getPosition().x < this.body.getPosition().x) {
                    this.body.setLinearVelocity(10, 5);
                } else {
                    this.body.setLinearVelocity(-10, 5);
                }
                this.damage(Math.min(WorldManager.level, 8));
                gracePeriod = 1.5f;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
