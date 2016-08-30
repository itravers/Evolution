package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class Food {
    MyGdxGame parent;

    //Physics Fields
    private BodyDef bodyDef;



    private Body body;
    private CircleShape shape;
    private FixtureDef fixtureDef;
    private Fixture fixture;
    private Vector2 size;
    private Vector2 newPosition = null; //Used to update the position manually.
    public static final short CATEGORY = -2;


    public static float LIMIT = 60; //The largest food possible.
    private float LIFE_VALUE;

    public Food(MyGdxGame parent, World physicsWorld, Vector2 position, Vector2 size, float value){
        this.parent = parent;
        this.size = size;
        setLIFE_VALUE(value);
        setupPhysics(physicsWorld, position, size);

    }

    private void setupPhysics(World physicsWorld, Vector2 position, Vector2 size){
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        //bodyDef.position.set(position);
        bodyDef.position.set((position.x - (Gdx.graphics.getWidth() /2) + size.x / 2)/parent.PIXELS_TO_METERS, (-position.y + (Gdx.graphics.getHeight() / 2 ) - size.y / 2)/parent.PIXELS_TO_METERS);
        //bodyDef.position.set((position.x + size.x / 2) / parent.PIXELS_TO_METERS,
        //(position.y + size.y / 2) / parent.PIXELS_TO_METERS);

        body = physicsWorld.createBody(bodyDef);

        shape = new CircleShape();
        shape.setRadius((size.x /2)/parent.PIXELS_TO_METERS);
        //shape.setAsBox((size.x /2)/parent.PIXELS_TO_METERS, (size.y / 2)/parent.PIXELS_TO_METERS);
        //shape.setAsBox((size.x / 2) / parent.PIXELS_TO_METERS,
        // (size.y / 2) / parent.PIXELS_TO_METERS);

        fixtureDef = new FixtureDef();
        fixtureDef.filter.groupIndex = CATEGORY;
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        fixture = body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    public Vector2 getPos(){
        //Vector2 pos = new Vector2((body.getPosition().x - (Gdx.graphics.getWidth() /2) + size.x / 2)*parent.PIXELS_TO_METERS, body.getPosition().y);
        float x = body.getPosition().x;
        float x2 = x * parent.PIXELS_TO_METERS;
        float x3 = x2 - (size.x / 2);
        float x4 = x3 + Gdx.graphics.getWidth()/2;

        //System.out.println("x: " + x);
        //System.out.println("x2: " + x2);
        //System.out.println("x3 " + x3);
        //System.out.println("x4 " + x4);

        float y = body.getPosition().y;
        float y2 = y * parent.PIXELS_TO_METERS;
        float y3 = y2 + (size.y / 2);
        float y4 = -(y3 - Gdx.graphics.getHeight()/2);

        /// System.out.println("y: " + y);
        // System.out.println("y2: " + y2);
        // System.out.println("y3 " + y3);
        //System.out.println("y4 " + y4);

        Vector2 pos = new Vector2(x4 , y4);
        //System.out.println("Graphics width: " + (Gdx.graphics.getWidth()/2));
        return pos;
    }

    public float getLIFE_VALUE() {
        return LIFE_VALUE;
    }

    public void setLIFE_VALUE(float LIFE_VALUE) {
        this.LIFE_VALUE = LIFE_VALUE;
    }

    //Updates food to a new location, with a new value
    public void reset(){
        setPosition(parent.getRandomPosition(this));
        setLIFE_VALUE(parent.getRandomFoodValue());
    }

    public void setPosition(Vector2 p){
       // bodyDef.position.set((p.x - (Gdx.graphics.getWidth() /2) + size.x / 2)/parent.PIXELS_TO_METERS, (-p.y + (Gdx.graphics.getHeight() / 2 ) - size.y / 2)/parent.PIXELS_TO_METERS);
        //body.setTransform(p, body.getAngle());
        newPosition = p;
    }

    public Vector2 getSize(){
        return size;
    }

    public void update(){
        if(newPosition != null){
            System.out.println("moving food to: " + newPosition.x + ":" + newPosition.y);
           setPos(newPosition);
            newPosition = null;
        }
    }

    public Body getBody() {
        return body;
    }

    public void setPos(Vector2 p){
        body.setTransform((p.x - (Gdx.graphics.getWidth() /2) + size.x / 2)/parent.PIXELS_TO_METERS, (-p.y + (Gdx.graphics.getHeight() / 2 ) - size.y / 2)/parent.PIXELS_TO_METERS, body.getAngle());

    }

}
