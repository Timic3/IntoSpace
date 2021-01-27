package com.intospace.entities;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.intospace.game.IntoSpaceGame;
import com.intospace.screens.GameScreen;
import com.intospace.world.Assets;

public class AIPlayer extends Player implements Steerable<Vector2> {
    boolean tagged;
    float boundingRadius;
    float maxLinearSpeed, maxLinearAcceleration;
    float maxAngularSpeed, maxAngularAcceleration;

    SteeringBehavior<Vector2> behavior;
    SteeringAcceleration<Vector2> steeringOutput;

    public AIPlayer(float x, float y, OrthographicCamera camera, World world) {
        super(x, y, camera, world);
        this.maxLinearSpeed = 4;
        this.maxLinearAcceleration = 5;
        this.maxAngularSpeed = 1;
        this.maxAngularAcceleration = 1;
        this.boundingRadius = 0.1f;

        this.tagged = false;

        this.steeringOutput = new SteeringAcceleration<>(new Vector2());
        if (x == 0 && y == 420) {
            final TextureAtlas atlas = IntoSpaceGame.getInstance().assets.get(Assets.ENEMIES);

            this.idleTexture = atlas.findRegion("Ghost");
            this.handTexture = null;
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        GdxAI.getTimepiece().update(delta);
        if (behavior != null) {
            behavior.calculateSteering(steeringOutput);
            /*Vector2 force = steeringOutput.linear.scl(delta);
            //body.applyForceToCenter(force, true);
            body.applyLinearImpulse(force, body.getPosition(), true);
            if (body.getLinearVelocity().x < -MAX_SPEED) {
                body.setLinearVelocity(-MAX_SPEED, body.getLinearVelocity().y);
            }*/
            applySteering(steeringOutput, delta);
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steering, float delta) {
        boolean anyAccelerations = false;

        if (!steeringOutput.linear.isZero()) {
            //body.applyForceToCenter(steeringOutput.linear, true);
            //System.out.println(steeringOutput.linear);
            if (body.getGravityScale() == 0) {
                body.applyLinearImpulse(steeringOutput.linear.x, steeringOutput.linear.y, body.getPosition().x, body.getPosition().y, true);
            } else {
                body.applyLinearImpulse(steeringOutput.linear.x, 0, body.getPosition().x, body.getPosition().y, true);
            }
            anyAccelerations = true;
        }

        if (true) {
            if (steeringOutput.angular != 0) {
                body.applyTorque(steeringOutput.angular, true);
                anyAccelerations = true;
            }
        } else {
            Vector2 linVel = getLinearVelocity();
            if (!linVel.isZero(getZeroLinearSpeedThreshold())) {
                float newOrientation = vectorToAngle(linVel);
                body.setAngularVelocity((newOrientation - getAngularVelocity()) * delta);
                body.setTransform(body.getPosition(), newOrientation);
            }
        }

        if (anyAccelerations) {
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            float maxLinearSpeed = getMaxLinearSpeed();
            if (currentSpeedSquare > (maxLinearSpeed * maxLinearSpeed)) {
                body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }
            float maxAngVelocity = getMaxAngularSpeed();
            if (body.getAngularVelocity() > maxAngVelocity) {
                body.setAngularVelocity(maxAngVelocity);
            }
        }
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0.1f;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    public void setBehavior(SteeringBehavior<Vector2> behavior) {
        this.behavior = behavior;
    }

    public SteeringBehavior<Vector2> getBehavior() {
        return behavior;
    }
}
