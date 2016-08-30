package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.NeuralNetwork.NeuralNet;

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
    private static int REFACTORY_LIMIT = 5000; //seconds in refactory time.
    private float MAX_SPEED = .5f;
    private float MAX_ROTATION_SPEED = 10f;

    private int LIFE_SPAN = 20000; //in milliseconds
    private int LIFE_LEFT = LIFE_SPAN;


    private int REFACTORY_TIME_LEFT = REFACTORY_LIMIT;



    private int numChildren = 0; //The number of children this creature has spawned.

    private int generation; //Used by genetic algorithm to track which generation this is in.
    private boolean isPregnant = false; //used to check if we need to birth a child next update.

    public Creature(MyGdxGame parent, World physicsWorld, Vector2 position, Vector2 size, int gen){
        this.parent = parent;
        this.size = size;
        this.generation = gen;
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

        if(isPregnant)birthChild();
        transferScreens();

        //System.out.println("LIFE_LEFT: " + LIFE_LEFT);
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

        float time = 1000/parent.fps;
        int elapsedTime = (int)(time * 1);
        // System.out.println("ElapsedTime" + elapsedTime);

        //Change Timers
        //REFACTORY_TIME_LEFT -= elapsedTime;

        if(getREFACTORY_TIME_LEFT() <= 0){
            setREFACTORY_TIME_LEFT(0);
        }else{
            setREFACTORY_TIME_LEFT(getREFACTORY_TIME_LEFT()-elapsedTime);
            if(REFACTORY_TIME_LEFT % 1000 == 0)System.out.println("REFACTORY TIME LEFT: " + REFACTORY_TIME_LEFT);
        }
        //if((REFACTORY_TIME_LEFT > 4900 && REFACTORY_TIME_LEFT <= 5000) || (REFACTORY_TIME_LEFT > 0 && REFACTORY_TIME_LEFT <= 5))System.out.println("REFACTORY TIME LEFT: " + REFACTORY_TIME_LEFT);


        LIFE_LEFT -= elapsedTime;
        if(LIFE_LEFT <= 0){
            kill();
        }

        if(LIFE_LEFT % 1000 == 0)System.out.println("LIFE_LEFTT: " + LIFE_LEFT);

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
        //these creatures cannot mate if either of them have any time left on their  REFACTORY_TIME_LEFT

        //we can't create a new creature when called from the collision code (this is)
        //so we are going to set a flag, and in the render code, we will add the new creature
        if(this.getREFACTORY_TIME_LEFT() == 0 && otherCreature.getREFACTORY_TIME_LEFT() == 0){
           isPregnant = true;
        }

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
        toClosest.y = toClosest.y * parent.PIXELS_TO_METERS;
        //System.out.println("ToClosestCreature + " + toClosest);
        return toClosest;
    }

    private void birthChild(){
        System.out.println("THESE CREATURES HAVE MATED");
        this.resetProcreationTimer();
        //otherCreature.resetProcreationTimer();
        this.incrementNumChildren();
        //otherCreature.incrementNumChildren();

        Vector2 size = new Vector2(5 , 10);
        Vector2 position = new Vector2(getPos());
        //the new creature's generation will be the interger average of the parents + 1
       // int averageGeneration = ((this.generation + otherCreature.generation)/2)+1;
        Creature c = new Creature(parent, parent.physicsWorld, position,  size, generation+1);
        //add new creature to game list
        parent.creatures.add(c);
        isPregnant = false;

    }


    /**
     * This will return a vector pointing towards the closest creature to this creature.
     * @return
     */
    /*
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
                if(Math.abs(dist) < Math.abs(distanceToClosestCreature)){
                    distanceTo2ndClosestCreature = distanceToClosestCreature;
                    secondClosestCreature = closestCreature;

                    distanceToClosestCreature = dist;
                    closestCreature = c;
                    System.out.println("Distance Closest -> 2ndClosest: " + distanceToClosestCreature + " " + distanceTo2ndClosestCreature);


                }
            }
        }

        //Now we have the closest creature, lets get a vector from us to them.
        Vector2 to2ndClosest = new Vector2(secondClosestCreature.body.getPosition().sub(body.getPosition()));
        to2ndClosest.x = to2ndClosest.x * parent.PIXELS_TO_METERS;
        to2ndClosest.y = to2ndClosest.y * parent.PIXELS_TO_METERS;
        //System.out.println("ToClosestCreature + " + toClosest);
        return to2ndClosest;
    }
    */

    /**
     * This will return a vector pointing towards the closest creature to this creature.
     * @return
     */
    public Vector2 toClosestFood(){
        //First search for the closest food
        float distanceToClosestFood = 101010101f;
        Food closestFood = parent.foods.get(0);
        for(int i = 0; i < parent.foods.size(); i++){
            Food f = parent.foods.get(i);
                float dist =  body.getPosition().dst(f.getPos());
                if(dist < distanceToClosestFood){
                    distanceToClosestFood = dist;
                    closestFood = f;
                }

        }

        //Now we have the closest creature, lets get a vector from us to them.
        Vector2 toClosest = new Vector2(closestFood.getBody().getPosition().sub(body.getPosition()));
        toClosest.x = toClosest.x * parent.PIXELS_TO_METERS;
        toClosest.y = toClosest.y * parent.PIXELS_TO_METERS;
        //System.out.println("ToClosestCreature + " + toClosest);
        return toClosest;
    }


    public int getREFACTORY_TIME_LEFT() {
        return REFACTORY_TIME_LEFT;
    }

    public int setREFACTORY_TIME_LEFT(int REFACTORY_TIME_LEFT) {
       return this.REFACTORY_TIME_LEFT = REFACTORY_TIME_LEFT;
    }

    /**
     * Kills this creature.
     * If creature had no children, this creature won't actually die:
     * It's brains will be replaced by one of the fittest of it's generation. It's REFACTORY_TIME_LEFT, and LIFE_LEFT will be reset.
     *
     * If it has had children it will be removed, dereferenced and left to the garabage collector
     */
    private void kill(){
        LIFE_LEFT = 0;
        if(hasProcreated()){
            System.out.println("time out, have procreated, do kill");
            parent.physicsWorld.destroyBody(this.body);
            parent.creatures.remove(this);
        }else{


            procreateASexually();
        }
    }

    /**
     * 1. this will be called if the creature has procreated naturally
     * 2. called from where it dies.
     *
     * 1.Moves to new random location.
     * 2. resets refactory time.
     * 3. resets life left.
     * 4. resets Num Children
     * 5. Replace Brain with a new one selected by the genetic algorithm
     */


    /**
     * Create a new creature and remove this one
     */
    private void procreateASexually(){
        this.REFACTORY_TIME_LEFT = REFACTORY_LIMIT;
        this.LIFE_LEFT = LIFE_SPAN;
        numChildren = 0;
        replaceBrain();
        Vector2 newPos = parent.getRandomPosition(this);
        setPos(newPos);
        //this.body.setTransform(newPos, this.body.getAngle());
       // this.
        System.out.println("time out, havn't procreated, a-sexually procreate @ " + newPos);
    }

    private boolean hasProcreated(){
        boolean returnVal = false;
        if(getNumChildren() >= 1){
            returnVal = true;
        }
        return returnVal;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void incrementNumChildren() {
        this.numChildren += 1;
    }

    private void resetProcreationTimer(){
        System.out.println("ProcreationTimerReset");
        this.setREFACTORY_TIME_LEFT(Creature.REFACTORY_LIMIT);
    }

    public void setPos(Vector2 p){
        body.setTransform((p.x - (Gdx.graphics.getWidth() /2) + size.x / 2)/parent.PIXELS_TO_METERS, (-p.y + (Gdx.graphics.getHeight() / 2 ) - size.y / 2)/parent.PIXELS_TO_METERS, body.getAngle());

    }

    private void replaceBrain(){

    }

    public void transferScreens(){

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        //teleport from right to left
        if(getPos().x >= screenWidth){
            setPos(new Vector2(getPos().x - screenWidth, getPos().y));
        }else if(getPos().x < 0){
            setPos(new Vector2(screenWidth - getPos().x, getPos().y));
        }

        if(getPos().y >= screenHeight){
            setPos(new Vector2(getPos().x, getPos().y - screenHeight));
        }else if(getPos().y < 0){
            setPos(new Vector2(getPos().x, screenHeight - getPos().y));
        }
    }


}
