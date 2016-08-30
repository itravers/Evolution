package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class Creature {
    MyGdxGame parent;

    //Physics Fields
    private BodyDef bodyDef;
    private Body body;
    private PolygonShape shape;
    private FixtureDef fixtureDef;
    private Fixture fixture;

    private float linearDamping = .8f;
    private float angularDamping = 1f;
    private float density = 1.25f;
    private float friction = .5f;
    private Vector2 size;
    private float movePower = .0005f;
    private float rotationPower = .0000015f;



    //Inputs
    private boolean rotateRightPressed = false;
    private boolean rotateLeftPressed = false;
    private boolean thrustForwardPressed = false;


    //Collision Category
    public static final short CATEGORY = -1;
    private float MAX_SPEED = .5f;
    private float MAX_ROTATION_SPEED = 10f;

    private float LIFE_SPAN = 10f;
    private float LIFE_LEFT = LIFE_SPAN;

    public Creature(MyGdxGame parent, World physicsWorld, Vector2 position, Vector2 size){
        this.parent = parent;
        this.size = size;
        setupPhysics(physicsWorld, position, size);

    }

    private void setupPhysics(World physicsWorld, Vector2 position, Vector2 size){
        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //bodyDef.position.set(position);
        bodyDef.position.set((position.x - (Gdx.graphics.getWidth() /2) + size.x / 2)/parent.PIXELS_TO_METERS, (-position.y + (Gdx.graphics.getHeight() / 2 ) - size.y / 2)/parent.PIXELS_TO_METERS);
        //bodyDef.position.set((position.x + size.x / 2) / parent.PIXELS_TO_METERS,
                             //(position.y + size.y / 2) / parent.PIXELS_TO_METERS);

        body = physicsWorld.createBody(bodyDef);
        body.setLinearDamping(linearDamping);
        body.setAngularDamping(angularDamping);

        shape = new PolygonShape();
        shape.setAsBox((size.x /2)/parent.PIXELS_TO_METERS, (size.y / 2)/parent.PIXELS_TO_METERS);
        //shape.setAsBox((size.x / 2) / parent.PIXELS_TO_METERS,
                      // (size.y / 2) / parent.PIXELS_TO_METERS);

        fixtureDef = new FixtureDef();
        //fixtureDef.filter.groupIndex = CATEGORY;
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;

        fixture = body.createFixture(fixtureDef);
        body.setUserData(this);
        shape.dispose();
    }

    public void update(){
        //get input and apply inpulses.
        if(isThrustForwardPressed()){
            //System.out.println("currentSpeed: " + getCurrentSpeed());
            if(getCurrentSpeed() < MAX_SPEED){
                Vector2 impulse = new Vector2(-(float)Math.sin(body.getAngle()), (float)Math.cos(body.getAngle())).scl(movePower);
                body.applyLinearImpulse(impulse, body.getPosition(),true);

            }
        }

        if(isRotateLeftPressed()){
            if(Math.abs(body.getAngularVelocity()) <= MAX_ROTATION_SPEED){
                body.applyAngularImpulse(+rotationPower, true);
            }
        }

        if(isRotateRightPressed()){
            if(Math.abs(body.getAngularVelocity()) <= MAX_ROTATION_SPEED){
                body.applyAngularImpulse(-rotationPower, true);
            }
        }
    }



    private float getCurrentSpeed(){
        return body.getLinearVelocity().len2();
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

    public boolean isRotateRightPressed() {
        return rotateRightPressed;
    }

    public void setRotateRightPressed(boolean rotateRightPressed) {
        this.rotateRightPressed = rotateRightPressed;
    }

    public boolean isRotateLeftPressed() {
        return rotateLeftPressed;
    }

    public void setRotateLeftPressed(boolean rotateLeftPressed) {
        this.rotateLeftPressed = rotateLeftPressed;
    }

    public boolean isThrustForwardPressed() {
        return thrustForwardPressed;
    }

    public void setThrustForwardPressed(boolean thrustForwardPressed) {
        this.thrustForwardPressed = thrustForwardPressed;
    }

    /**
     * This Creature will eat the given food.
     */
    public void eat(Food f){
        System.out.println("Creature has eaten food.");
        //First our LIFE_LEFT is incremented with the foods value
        LIFE_LEFT += f.getLIFE_VALUE();

        //reset the food.
        f.reset();
    }

    public Vector2 getSize(){
        return size;
    }

    public void mate(Creature otherCreature){
        System.out.println("THESE CREATURES HAVE MATED");
    }

    /**
     * This will return a vector pointing towards the closest creature to this creature.
     * @return
     */
    public Vector2 toClosestCreature(){
        //First search for the closest creature
        float distanceToClosestCreature = 101010101f;
        Creature closestCreature = parent.creatures.get(0);
        for(int i = 0; i < parent.creatures.size(); i++){
            //we don't want to compare ourselve to ourself
            Creature c = parent.creatures.get(i);
            if(c != this){
                float dist =  body.getPosition().dst(c.getPos());
                if(dist < distanceToClosestCreature){
                    distanceToClosestCreature = dist;
                    closestCreature = c;
                }
            }
        }

        //Now we have the closest creature, lets get a vector from us to them.
        Vector2 toClosest = new Vector2(closestCreature.body.getPosition().sub(body.getPosition()));
        toClosest.x = toClosest.x * parent.PIXELS_TO_METERS;
        toClosest.y = -toClosest.y * parent.PIXELS_TO_METERS;
        //System.out.println("ToClosestCreature + " + toClosest);
        return toClosest;
    }

    /**
     * This will return a vector pointing towards the closest creature to this creature.
     * @return
     */
    public Vector2 to2ndClosestCreature(){
        //First search for the closest creature
        float distanceToClosestCreature = 101010101f;
        float distanceTo2ndClosestCreature = 101010101f;
        Creature closestCreature = this;
        Creature secondClosestCreature = this;
        for(int i = 0; i < parent.creatures.size(); i++){
            //we don't want to compare ourselve to ourself
            Creature c = parent.creatures.get(i);
            if(c != this){
                float dist =  body.getPosition().dst(c.getPos());
                if(dist < distanceToClosestCreature){
                    distanceTo2ndClosestCreature = distanceToClosestCreature;
                    secondClosestCreature = closestCreature;

                    distanceToClosestCreature = dist;
                    closestCreature = c;


                }
            }
        }

        //Now we have the closest creature, lets get a vector from us to them.
        Vector2 to2ndClosest = new Vector2(secondClosestCreature.body.getPosition().sub(body.getPosition()));
        to2ndClosest.x = to2ndClosest.x * parent.PIXELS_TO_METERS;
        to2ndClosest.y = -to2ndClosest.y * parent.PIXELS_TO_METERS;
        //System.out.println("ToClosestCreature + " + toClosest);
        return to2ndClosest;
    }
}
