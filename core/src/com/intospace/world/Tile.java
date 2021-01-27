package com.intospace.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.intospace.entities.Entity;
import com.intospace.game.Constants;

public class Tile extends Entity {
    private final TextureAtlas.AtlasRegion texture;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public Body body;
    public World world;

    public Tile(TextureAtlas.AtlasRegion texture, float x, float y, World world) {
        super(x, y);
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.width = 32 / Constants.PPM;
        this.height = 32 / Constants.PPM;
        this.world = world;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((x + 32 / 2f) / Constants.PPM, (y + 32 / 2f) / Constants.PPM);
        body = world.createBody(bodyDef);

        /*PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(this.width / 2, this.height / 2);*/

        // TODO: Improve this
        ChainShape cs = new ChainShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-this.height / 2f, -this.width / 2f);
        vertices[1] = new Vector2(-this.width / 2f, this.width / 2f);
        vertices[2] = new Vector2(this.width / 2f, this.width / 2f);
        vertices[3] = new Vector2(this.width / 2f, -this.width / 2f);
        cs.createLoop(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = cs;
        fixtureDef.friction = 3f;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0f;
        body.createFixture(fixtureDef);
        cs.dispose();

        body.setUserData(this);
    }

    public void update(float delta) {

    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x / Constants.PPM, y / Constants.PPM, this.width, this.height);
    }

    public void remove() {
        world.destroyBody(body);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
