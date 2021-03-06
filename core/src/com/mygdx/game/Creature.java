package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.NeuralNetwork.NeuralNet;
import com.mygdx.game.NeuralNetwork.Utils;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import java.util.ArrayList;

/**
 * Created by Isaac Assegai on 8/29/16.
 */
public class Creature {
    MyGdxGame parent;



    private boolean isFirstCreature = false;

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
    private double movePower = 0;///.0005f;
    private double rotationPower = 0;///.0000015f;
    private Vector2 previousPosition; //used to calculated totalMovement;
    private float totalMovement = 0;

    //AI
    NeuralNet neuralNet;
    private float fitness = 0;
    private int totalFoodEaten = 0;



    //Inputs
    private boolean rotate = false;
    private boolean thrustForwardPressed = false;


    //Collision Category
    public static final short CATEGORY = -1;
    private static int REFACTORY_LIMIT = Utils.REFACTORY_LIMIT;//5000; //seconds in refactory time.
    private float MAX_SPEED = .5f;
    private float MAX_ROTATION_SPEED = 10f;

    private int LIFE_SPAN = Utils.LIFE_SPAN;
    private int LIFE_LEFT = LIFE_SPAN;
    private long TIME_LIVED = 0;


    private int REFACTORY_TIME_LEFT = REFACTORY_LIMIT;



    private int numChildren = 0; //The number of children this creature has spawned.

    private int generation; //Used by genetic algorithm to track which generation this is in.
    private boolean isPregnant = false; //used to check if we need to birth a child next update.
    private ArrayList<Double> pregnantChrome; //Used to birth children.

    public Creature(MyGdxGame parent, World physicsWorld, Vector2 position, Vector2 size, int gen, int num){
        this.parent = parent;
        this.size = size;
        setGeneration(gen);
        setupPhysics(physicsWorld, position, size);

        //don't give a brain to the first one, I want to control it.

        if(num == 0)isFirstCreature = true;
        initializeEmptyBrain();

    }

    public Creature(MyGdxGame parent, World physicsWorld, Vector2 position, Vector2 size, int gen, int num, ArrayList<Double>chromosome){
        this.parent = parent;
        this.size = size;
        setGeneration(gen);

        setupPhysics(physicsWorld, position, size);

        //don't give a brain to the first one, I want to control it.

        if(num == 0)isFirstCreature = true;
        initializeFullBrain(chromosome);
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

        body.setTransform(body.getPosition(), MathUtils.random(360));
        previousPosition = body.getPosition();
    }

    public void update(){
        float dist = body.getPosition().sub(previousPosition).len2();
        //if(totalMovement > 1)tot
        totalMovement = totalMovement + dist;
        previousPosition = new Vector2(body.getPosition());
        //if(isFirstCreature())System.out.println("totalMovement dist: " + totalMovement+ " " +dist + " LIFE: " + LIFE_LEFT);
        if(neuralNet != null){
            neuralNet.update();
        }

        calculateFitness();
        if(isPregnant)birthChild();
        transferScreens();

        //System.out.println("LIFE_LEFT: " + LIFE_LEFT);
        //get input and apply inpulses.
        if(isThrustForwardPressed()){
            //System.out.println("currentSpeed: " + getCurrentSpeed());
            if(getCurrentSpeed() < MAX_SPEED){
                Vector2 impulse = new Vector2(-(float)Math.sin(body.getAngle()), (float)Math.cos(body.getAngle())).scl((float)movePower);
                body.applyLinearImpulse(impulse, body.getPosition(),true);

            }
        }

        if(isRotatePressed()){
           // if(isFirstCreature())System.out.println("body.getAngularVelocity(): " + rotationPower + "  MAX_ROTATION_SPEED: " + MAX_ROTATION_SPEED);
            if(Math.abs(body.getAngularVelocity()) <= MAX_ROTATION_SPEED){
               // if(isFirstCreature())System.out.println("Rotate: " + rotationPower);
                body.applyAngularImpulse((float) rotationPower, true);
            }
        }

        /*if(isRotateLeftPressed()){
            if(Math.abs(body.getAngularVelocity()) <= MAX_ROTATION_SPEED){
                body.applyAngularImpulse((float)rotationPower, true);
            }
        }

        if(isRotateRightPressed()){
            if(Math.abs(body.getAngularVelocity()) <= MAX_ROTATION_SPEED){
                body.applyAngularImpulse((float)rotationPower, true);
            }
        }8*/

        float time = 1000/Utils.FPS;
        int elapsedTime = (int)(time * 1);
        // System.out.println("ElapsedTime" + elapsedTime);

        //Change Timers
        //REFACTORY_TIME_LEFT -= elapsedTime;

        if(getREFACTORY_TIME_LEFT() <= 0){
            setREFACTORY_TIME_LEFT(0);
        }else{
            setREFACTORY_TIME_LEFT(getREFACTORY_TIME_LEFT()-elapsedTime);
           // if(REFACTORY_TIME_LEFT % 1000 == 0)System.out.println("REFACTORY TIME LEFT: " + REFACTORY_TIME_LEFT);
        }
        //if((REFACTORY_TIME_LEFT > 4900 && REFACTORY_TIME_LEFT <= 5000) || (REFACTORY_TIME_LEFT > 0 && REFACTORY_TIME_LEFT <= 5))System.out.println("REFACTORY TIME LEFT: " + REFACTORY_TIME_LEFT);


        TIME_LIVED += elapsedTime;
        LIFE_LEFT -= elapsedTime;
        if(LIFE_LEFT <= 0){
            kill();
        }

       // if(LIFE_LEFT % 1000 == 0)System.out.println("LIFE_LEFTT: " + LIFE_LEFT);

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

    public boolean isRotatePressed() {
        return rotate;
    }

    public void setRotatePressed(boolean rotate) {
       // if(isFirstCreature())System.out.println("Rotate: " + rotate);
        this.rotate = rotate;
    }



    public boolean isThrustForwardPressed() {
        return thrustForwardPressed;
    }

    public void setThrustForwardPressed(boolean thrustForwardPressed) {
        //System.out.println(this.toString() + " Thrust Forward Pressed");
        this.thrustForwardPressed = thrustForwardPressed;
    }

    /**
     * This Creature will eat the given food.
     */
    public void eat(Food f){
       // System.out.println("Creature has eaten food: " + f.getLIFE_VALUE());
        //First our LIFE_LEFT is incremented with the foods value
        LIFE_LEFT += f.getLIFE_VALUE();
        totalFoodEaten += f.getLIFE_VALUE();

        //System.out.println("Creature has eaten food: " + f.getLIFE_VALUE() + " lifeLeft: " + LIFE_LEFT);
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
            ArrayList<Double> babyChrom = neuralNet.crossOver(neuralNet.getWeights(), otherCreature.neuralNet.getWeights());
           // babyChrom.mutate();
            babyChrom = neuralNet.mutate(babyChrom);
            pregnantChrome = babyChrom;
            isPregnant = true;
        }

    }

    /**
     * This will return a vector pointing towards the closest creature to this creature.
     * @return
     */
    /*
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
    */

    /**
     * returns a reference to closest creature that is not me.
     */
    public Creature getClosestCreature(){
        //First search for the closest creature
        float distanceToClosestCreature = 101010101f;
        //System.out.println("CRATURES "+parent.creatures);
        Creature closestCreature = this;
        if(!parent.creatures.isEmpty()){
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
        }


        return closestCreature;
    }

    public Food getClosestFood(){
        //First search for the closest creature
        float distanceToClosestFood = 101010101f;
        Food closestFood = null;
        if(body != null){
            for(int i = 0; i < parent.foods.size(); i++){
                //we don't want to compare ourselve to ourself
                Food f = parent.foods.get(i);
                float dist =  body.getPosition().dst(f.getPos());

                if(dist < distanceToClosestFood){
                    distanceToClosestFood = dist;
                    closestFood = f;
                }

            }
        }

        return closestFood;
    }

    /**
     * Returns a Vector2 pointing from us to the given Creature
     */
    public Vector2 getVectorToCreature(Creature c){
        Vector2 toClosest = null;
        if(c != null){
            toClosest = new Vector2(c.body.getPosition().sub(body.getPosition()));
            toClosest.x = toClosest.x * parent.PIXELS_TO_METERS;
            toClosest.y = toClosest.y * parent.PIXELS_TO_METERS;
            //System.out.println("ToClosestCreature + " + toClosest);
        }

        return toClosest;
    }

    public Vector2 getVectorToClosestCreature(){
        Creature closest = getClosestCreature();
        return getVectorToCreature(closest);
    }

    private void birthChild(){
       // System.out.println("THESE CREATURES HAVE MATED : " + parent.creatures.size() + " topFitness: " + parent.getHighestFitness());
       // System.out.println("birthChild() GENERATION: " + getGeneration());
        System.out.println(" birthChild() GENERATION: " + getGeneration() + "   HIGHESTFITNESSEVER: " + parent.getHighestFitness() + "   AveragePopulationFitness: " + parent.getAverageFitness() + "   bottomFitness " + parent.getLowestFitness()+ "   topFitness: " + parent.getTopFitnessAlive() + "   pop: " + parent.creatures.size());
        this.resetProcreationTimer();
        //otherCreature.resetProcreationTimer();
        this.incrementNumChildren();
        //otherCreature.incrementNumChildren();

        Vector2 size = new Vector2(5 , 10);
        Vector2 position = new Vector2(getPos());
        //the new creature's generation will be the interger average of the parents + 1
       // int averageGeneration = ((this.generation + otherCreature.generation)/2)+1;
        //Creature c = new Creature(parent, parent.physicsWorld, position,  size, generation+1, 1);
        Creature c = new Creature(parent, parent.physicsWorld, position, size, getGeneration()+1, 1, pregnantChrome);

        //Creature c = new Creature(parent, pregnantChrome);
        //add new creature to game list
        parent.creatures.add(c);

        pregnantChrome = null;
        isPregnant = false;

        //mating is now good for you, make you live longer.
        //LIFE_LEFT += 5000;//each time you birth a child, you will life longer.

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
        Vector2 toClosest = null;
        Food closestFood = null;
        if(body != null){
            for(int i = 0; i < parent.foods.size(); i++){
                Food f = parent.foods.get(i);
               // System.out.println("body: " + body);
                float dist =  body.getPosition().dst(f.getPos());
                if(dist < distanceToClosestFood){
                    distanceToClosestFood = dist;
                    closestFood = f;
                }

            }

            //Now we have the closest creature, lets get a vector from us to them.
            toClosest = new Vector2(closestFood.getBody().getPosition().sub(body.getPosition()));
            toClosest.x = toClosest.x * parent.PIXELS_TO_METERS;
            toClosest.y = toClosest.y * parent.PIXELS_TO_METERS;
            //System.out.println("ToClosestCreature + " + toClosest);
        }



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
    public void kill(){
        LIFE_LEFT = 0;
            //System.out.println("time out, have procreated, do kill");
            parent.physicsWorld.destroyBody(this.body);
            this.TIME_LIVED = 0;
            parent.creatures.remove(this);


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

    /*private void procreateASexually(){
        this.REFACTORY_TIME_LEFT = REFACTORY_LIMIT;
        this.LIFE_LEFT = LIFE_SPAN;
        numChildren = 0;
        replaceBrain();
        Vector2 newPos = parent.getRandomPosition(this);
        setPos(newPos);
        //this.body.setTransform(newPos, this.body.getAngle());
       // this.
       // System.out.println("time out, havn't procreated, a-sexually procreate @ " + newPos);
    }*/

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
       // System.out.println("ProcreationTimerReset");
        this.setREFACTORY_TIME_LEFT(Creature.REFACTORY_LIMIT);
    }

    public void setPos(Vector2 p){
        body.setTransform((p.x - (Gdx.graphics.getWidth() /2) + size.x / 2)/parent.PIXELS_TO_METERS, (-p.y + (Gdx.graphics.getHeight() / 2 ) - size.y / 2)/parent.PIXELS_TO_METERS, body.getAngle());
        previousPosition = body.getPosition();
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

    private void initializeFullBrain(ArrayList<Double>chromosome){
        neuralNet = new NeuralNet(this);
        neuralNet.putWeights(chromosome);
        initializeInputs();
    }

    /**
     * Sets up an initial random neural network
     * Hook up inputs
     * Hook up outputs
     */
    private void initializeEmptyBrain(){


        neuralNet = new NeuralNet(this);

        initializeInputs();
    }

    private void initializeInputs(){
        //hookup inputs keep this synched with updateInputs in nn
        Vector2 toClosestCreature = getVectorToClosestCreature();
        Creature closestCreature = getClosestCreature();
        Vector2 toClosestFood = toClosestFood();
        Food closestFood = getClosestFood();


        neuralNet.addInput(body.getLinearVelocity().x);
        neuralNet.addInput(body.getLinearVelocity().y);
        neuralNet.addInput(body.getAngularVelocity());

        //System.out.println("toClosestCreature: " + toClosestCreature);
        neuralNet.addInput(toClosestCreature.x);
        neuralNet.addInput(toClosestCreature.y);
        neuralNet.addInput(closestCreature.getREFACTORY_TIME_LEFT());
        neuralNet.addInput(closestCreature.getFitness());

        neuralNet.addInput(toClosestFood.x);
        neuralNet.addInput(toClosestFood.y);
        neuralNet.addInput(closestFood.getLIFE_VALUE());

        neuralNet.addInput(LIFE_LEFT);
        neuralNet.addInput(REFACTORY_TIME_LEFT);
    }

    public Body getBody(){return this.body;}

    public int getLIFE_LEFT(){
        return LIFE_LEFT;
    }

    public void calculateFitness(){
        //add total movement here when it's calculated
        //if(TIME_LIVED > LIFE_SPAN * 4){
           // fitness = (TIME_LIVED / 500) + (this.getNumChildren() * 10)+(totalFoodEaten/(Utils.FOOD_LIMIT/2)) + (totalMovement*200) + (((float)generation)/5);
       // }else{
           // fitness = (TIME_LIVED / 1000) + (this.getNumChildren() * 2)+(totalFoodEaten/(Utils.FOOD_LIMIT/2)) + (totalMovement*100)+ (((float)generation)/5);
       // }
        fitness = (TIME_LIVED / 1000) + (this.getNumChildren() * 2);// + (((float)generation)/5);

    }

    public float getFitness(){
        calculateFitness();
        return fitness;
    }

    public boolean isFirstCreature() {
        return isFirstCreature;
    }

    public void setFirstCreature(boolean firstCreature) {
        isFirstCreature = firstCreature;
    }

    public void setMovePower(double p){
        this.movePower = p;
    }

    public void setRotatePower(double p){
        //if(isFirstCreature())System.out.println("Rot Pow: " + p);
        this.rotationPower = p;
    }

    public int getGeneration(){
        return generation;
    }

    public void setGeneration(int g){
        this.generation = g;
    }


}
