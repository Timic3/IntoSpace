package com.intospace.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.intospace.game.Constants;
import com.intospace.world.CollisionBits;
import com.intospace.world.WorldManager;

public class Enemy extends Entity {
    public TextureAtlas.AtlasRegion texture;

    private PointerSide pointerSide = PointerSide.WEST;

    private int health = 10 + WorldManager.level;
    private float gracePeriod = 0;

    public boolean markRemoval = false;

    public Enemy(float x, float y, int width, int height, World world) {
        super(x, y, width, height);

        // Collisions
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + width / 2f) / Constants.PPM, (y + height) / Constants.PPM);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        PolygonShape enemyShape = new PolygonShape();
        enemyShape.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM, new Vector2(0, -height / Constants.PPM / 2f), 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = enemyShape;
        fixtureDef.friction = 2f;
        fixtureDef.density = 0.5f;
        fixtureDef.restitution = 2f;
        fixtureDef.filter.categoryBits = CollisionBits.ENEMY; // 0b0110
        fixtureDef.filter.groupIndex = (short) 0;
        fixtureDef.filter.maskBits = CollisionBits.PLAYER | CollisionBits.TERRAIN; // 0b0110
        //fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        enemyShape.dispose();

        body.setUserData(this);
    }

    public void update(float delta) {
        if (body.getLinearVelocity().x < 0) {
            pointerSide = PointerSide.EAST;
        } else if (body.getLinearVelocity().x > 0) {
            pointerSide = PointerSide.WEST;
        }

        if (body.getPosition().x > Constants.MAX_X / 2f) {
            body.setTransform(body.getPosition().x - Constants.MAX_X / 2f, body.getPosition().y, 0);
        } else if (body.getPosition().x <= 0) {
            body.setTransform(body.getPosition().x + Constants.MAX_X / 2f, body.getPosition().y, 0);
        }

        if (gracePeriod > 0) {
            gracePeriod = Math.max(0, gracePeriod - 1 * delta);
        }
    }

    public void render(SpriteBatch batch) {
        if (gracePeriod > 0) {
            batch.setColor(1f, 0.6f, 0.5f, 1f);
        }
        batch.draw(texture, body.getPosition().x + (pointerSide == PointerSide.EAST ? width : -width) / Constants.PPM / 2f, body.getPosition().y - height / Constants.PPM, (pointerSide == PointerSide.EAST ? -width : width) / Constants.PPM, height / Constants.PPM);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public boolean damage(int health) {
        if (gracePeriod > 0)
            return false;

        this.health -= health;
        gracePeriod = 1.5f;
        if (this.health <= 0) {
            this.markRemoval = true;
        }

        return true;
    }
}
