package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class Creature {
    //Physics Fields
    private BodyDef bodyDef;
    private Body body;
    private PolygonShape shape;
    private FixtureDef fixtureDef;
    private Fixture fixture;

    private float linearDamping = .4f;
    private float angularDamping = .5f;
    private float density = 1.25f;
    private float friction = .5f;

    //Collision Category
    public static final short CATEGORY = -1;

    public Creature(World physicsWorld, Vector2 position, Vector2 size){
        setupPhysics(physicsWorld, position, size);

    }

    private void setupPhysics((World physicsWorld, Vector2 position, Vector2 size){
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        body = physicsWorld.createBody(bodyDef);
        body.setLinearDamping(linearDamping);
        body.setAngularDamping(angularDamping);

        shape = new PolygonShape();
        shape.setAsBox(size.x /2, size.y/2);

        fixtureDef = new FixtureDef();
        fixtureDef.filter.groupIndex = CATEGORY;
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;

        fixture = body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }
}
