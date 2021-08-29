package com.intospace.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.world.Assets;
import com.intospace.world.CollisionBits;

public class Rocket extends Entity {
    public TextureAtlas.AtlasRegion texture;

    public Rocket(float x, float y, World world) {
        super(x, y, 32 * 2, 64 * 2);
        final TextureAtlas atlas = IntoSpaceGame.getInstance().assets.get(Assets.OBJECTS);
        this.texture = atlas.findRegion("Rocket");

        // Collisions
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + width / 2f) / Constants.PPM, (y + height) / Constants.PPM);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM, new Vector2(0, -height / Constants.PPM / 2f), 0);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0f;
        fixtureDef.density = 1111f;
        fixtureDef.restitution = 0;
        fixtureDef.filter.categoryBits = CollisionBits.ROCKET; // 0b1111
        fixtureDef.filter.groupIndex = (short) 0;
        fixtureDef.filter.maskBits = CollisionBits.TERRAIN; // 0b1010
        //fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shape.dispose();

        body.setUserData(this);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, body.getPosition().x - width / Constants.PPM / 2f, body.getPosition().y - height / Constants.PPM, width / Constants.PPM, height / Constants.PPM);
    }
}
