package com.intospace.world;

import box2dLight.LightData;
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

    private boolean atEdge = false;

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

        EdgeShape groundShape = new EdgeShape();
        groundShape.set(new Vector2(-this.width / 2, this.height / 2), new Vector2(this.width / 2, this.height / 2));

        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(this.width / 2, this.height / 2);

        // TODO: Improve this
        /*ChainShape cs = new ChainShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-this.height / 2f, -this.width / 2f);
        vertices[1] = new Vector2(-this.width / 2f, this.width / 2f);
        vertices[2] = new Vector2(this.width / 2f, this.width / 2f);
        vertices[3] = new Vector2(this.width / 2f, -this.width / 2f);
        cs.createLoop(vertices);*/

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;
        fixtureDef.friction = 3f;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = false;

        fixtureDef.filter.categoryBits = CollisionBits.TERRAIN; // 0b10
        fixtureDef.filter.groupIndex = (short) 0;
        fixtureDef.filter.maskBits = CollisionBits.PLAYER | CollisionBits.ROCKET; // 0b01

        FixtureDef hitFixtureDef = new FixtureDef();
        hitFixtureDef.shape = boxShape;
        hitFixtureDef.isSensor = true;

        hitFixtureDef.filter.categoryBits = (short) 0;
        hitFixtureDef.filter.groupIndex = (short) 0;
        hitFixtureDef.filter.maskBits = (short) 0;

        body.createFixture(fixtureDef);
        body.createFixture(hitFixtureDef).setUserData(new LightData(15));
        groundShape.dispose();
        boxShape.dispose();

        body.setUserData(this);
    }

    public void update(float delta) {

    }

    public void render(SpriteBatch batch, int renderX) {
        if (renderX >= Constants.MAX_X) {
            if (!atEdge) {
                atEdge = true;
                body.setTransform((x + 32 / 2f) / Constants.PPM + Constants.MAX_X * this.width, (y + 32 / 2f) / Constants.PPM, 0);
            }
            batch.draw(texture, x / Constants.PPM + Constants.MAX_X * this.width, y / Constants.PPM, this.width, this.height);
        } else if (renderX < 0) {
            if (!atEdge) {
                atEdge = true;
                body.setTransform((x + 32 / 2f) / Constants.PPM - Constants.MAX_X * this.width, (y + 32 / 2f) / Constants.PPM, 0);
            }
            batch.draw(texture, x / Constants.PPM - Constants.MAX_X * this.width, y / Constants.PPM, this.width, this.height);
        } else {
            if (atEdge) {
                atEdge = false;
                body.setTransform((x + 32 / 2f) / Constants.PPM, (y + 32 / 2f) / Constants.PPM, 0);
            }
            batch.draw(texture, x / Constants.PPM, y / Constants.PPM, this.width, this.height);
        }
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

    public String getTextureName() {
        return this.texture.name;
    }

    public TextureAtlas.AtlasRegion getTexture() {
        return this.texture;
    }
}
