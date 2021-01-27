package com.intospace.entities;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Entity implements Location<Vector2> {
    private float x;
    private float y;

    public Body body;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {

    }

    public void render() {

    }

    @Override
    public Vector2 getPosition () {
        return body.getPosition();
    }

    @Override
    public float getOrientation () {
        return body.getAngle();
    }

    @Override
    public void setOrientation (float orientation) {
    }

    @Override
    public Location<Vector2> newLocation () {
        return new Box2dLocation();
    }

    @Override
    public float vectorToAngle (Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector (Vector2 outVector, float angle) {
        outVector.x = -(float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }
}
