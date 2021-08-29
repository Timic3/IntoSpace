package com.intospace.entities;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.intospace.game.IntoSpaceGame;
import com.intospace.screens.GameScreen;
import com.intospace.world.Assets;
import com.intospace.world.CollisionBits;

public class Ghost extends Enemy implements Steerable<Vector2> {
    boolean tagged;
    float boundingRadius;
    float maxLinearSpeed, maxLinearAcceleration;
    float maxAngularSpeed, maxAngularAcceleration;

    SteeringBehavior<Vector2> behavior;
    SteeringAcceleration<Vector2> steeringOutput;

    Arrive<Vector2> arriveBehavior;
    Wander<Vector2> wanderBehavior;

    Player player;

    public Ghost(float x, float y, World world, Player player) {
        super(x, y, 32, 64, world);

        // Make it collisionless with terrain
        for (Fixture fixture : body.getFixtureList()) {
            Filter filter = fixture.getFilterData();
            filter.maskBits &= ~CollisionBits.TERRAIN;
            fixture.setFilterData(filter);
        }

        body.setGravityScale(0);

        this.maxLinearSpeed = 1; // 1
        this.maxLinearAcceleration = 1; // 1
        this.maxAngularSpeed = 1;
        this.maxAngularAcceleration = 1;
        this.boundingRadius = 0.1f;

        this.tagged = false;

        this.steeringOutput = new SteeringAcceleration<>(new Vector2());

        final TextureAtlas atlas = IntoSpaceGame.getInstance().assets.get(Assets.ENEMIES);
        this.texture = atlas.findRegion("Ghost");

        this.arriveBehavior = new Arrive<>(this, player)
                .setTimeToTarget(0.01f)
                .setArrivalTolerance(0f)
                .setDecelerationRadius(0);
        this.wanderBehavior = new Wander<>(this)
                .setEnabled(true)
                .setWanderRadius(2f)
                .setWanderOffset(1f)
                .setWanderRate(MathUtils.PI2 * 3);
        this.player = player;
        this.setBehavior(wanderBehavior);
        this.setMaxLinearAcceleration(1f);
        this.setMaxLinearSpeed(1f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (this.getPosition().dst(player.getPosition()) < 7f) {
            this.setBehavior(arriveBehavior);
        } else if (this.getPosition().dst(player.getPosition()) > 5f && this.getBehavior() instanceof Arrive) {
            this.setBehavior(wanderBehavior);
            this.setMaxLinearAcceleration(1f);
            this.setMaxLinearSpeed(1f);
        }

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
