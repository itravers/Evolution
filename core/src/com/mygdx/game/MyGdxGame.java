package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.NeuralNetwork.NeuralNet;
import com.mygdx.game.NeuralNetwork.Utils;

import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor{
	World physicsWorld;

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture img;

	ArrayList<Creature> creatures;
	ArrayList<Food> foods;

	//Keep track of the NUM_FITTEST_GENOMES most fittest genomes that have lived.
	ArrayList<ArrayList<Double>> listFittestGenomes;

	//Maps to the listFittestGenomes, this is the data containing fitness
	ArrayList<Double>listFittestValues;

	final float PIXELS_TO_METERS = 100f;
	float fps = 60f;

	int MINIMUM_POPULATION = Utils.MINIMUM_POPULATION; //When population is less than this, a new creature is auto bred.
	int MAXIMUM_POPULATION = Utils.MAXIMUM_POPULATION; //when population exceeds this, a lesser fit creature is killed.
	
	@Override
	public void create () {

		physicsWorld = new World(new Vector2(0, 0), true);
		setupCollisionHandling();
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		Gdx.input.setInputProcessor(this);

		//setup fittest genome list
		listFittestGenomes = new ArrayList<ArrayList<Double>>();
		listFittestValues = new ArrayList<Double>();

		createFood(Utils.START_FOOD);
		createCreatures(MINIMUM_POPULATION *Utils.START_POPULATION_MULTIPLIER);
	}

	private void createFood(int num){
		foods = new ArrayList<Food>();
		for(int i = 0; i < num; i++) {
			Vector2 size = new Vector2(25, 25);
			Vector2 position = new Vector2(getRandomPositionFromSize(size));

			Food f = new Food(this, physicsWorld, position, size, getRandomFoodValue());
			//System.out.println("FPOS: " + (int)f.getPos().x + ":" + f.getPos().y);
			foods.add(f);
		}
	}

	private void addNewCreature(int creatureNum, ArrayList<Double>g){
		Vector2 size = new Vector2(5 , 10);
		Vector2 position = new Vector2(getRandomPositionFromSize(size));
		int generation = 0; //This is a 0th generation creature.
		Creature c;
		if(g == null){
			c = new Creature(this, physicsWorld, position,  size, generation, creatureNum);
		}else{
			c = new Creature(this, physicsWorld, position,  size, generation, creatureNum, g);
		}
		//c.neuralNet.putWeights(g);
		//System.out.println("CPOS: " + (int)c.getPos().x + ":" + c.getPos().y);
		creatures.add(c);

		if(creatureNum == 0){
			//on first creature, add it's genome to the fittestCreatureList FITTEST_CREATURES_TRACKED times
			for(int i = 0; i < Utils.FITTEST_CREATURES_TRACKED; i++){
				listFittestValues.add((double) c.fitness);
				listFittestGenomes.add(c.neuralNet.getWeights());
			}

		}
	}

	private void createCreatures(int num){
		creatures = new ArrayList<Creature>();
		//create a single creature for testing

		for(int i = 0; i < num; i++) {
			addNewCreature(i, null);
		}
		//System.out.println("ToClosest: " + creatures.get(0).toClosestCreature());
		//System.out.println("To2ndClosest: " + creatures.get(0).to2ndClosestCreature());
		//System.out.println("ToClosestFood: " + creatures.get(0).toClosestFood());

	}

	@Override
	public void render () {
		camera.update();
		updateCreatures();
		updateFood();
		physicsWorld.step(1f/fps,6, 2);




		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);

		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
		//debugMatrix = batch.getProjectionMatrix();
		batch.begin();
		//batch.draw(img, 0, 0);
		batch.end();

		debugRenderer.render(physicsWorld, debugMatrix);
	}

	private void updateCreatures(){
		updateFittestList();
		ensureMinimumPopulation();
		ensureMaximumPopulation();

		for(int i = 0; i < creatures.size(); i++){
			creatures.get(i).update();
		}
	}

	/**
	 * Loop through characters alive.
	 * Compare their fitness to the historically fit
	 * if they are more fit, they displace the historically fit
	 */
	private void updateFittestList(){

		//for each character alive
		for(int i = 0; i < creatures.size(); i++){
			Creature c = creatures.get(i);

			//check if creatures fitness is more than something else on the list
			for(int j = 0; j < listFittestValues.size(); j++){
				if(c.fitness > listFittestValues.get(j)){
					//put this characters genome and fitness values in place of the one in the list
					listFittestGenomes.set(j, (ArrayList<Double>) c.neuralNet.getWeights().clone());
					listFittestValues.set(j, (double) c.fitness);
				}
			}
		}
	}

	/**
	 * If the population is too high, roullete kill one of the least fittest
	 * in the creatures list
	 */
	private void ensureMaximumPopulation(){
		if(creatures.size() >= MAXIMUM_POPULATION){
			//Creature c = rouletteChooseLeastFittestChar();
			Creature c = getLeastFittestCreature();
			c.kill();
		}
	}

	private void ensureMinimumPopulation(){

		if(creatures.size() < MINIMUM_POPULATION){
			//roulette choose a genome from the listFittest
			//roulette choose a creature's genome from creatures list, else if
			//  if there are no creatures in list then:
			        //roulette choose another genome from listFittest
			//breed a new genome
			//create a new creature with said genome

			ArrayList<Double>g1;
			ArrayList<Double>g2;
			ArrayList<Double>newGenome;
			//creatures is not empty, so we are going to choose genome 1 from them
			if(!creatures.isEmpty()){
				g1 = rouletteCopyGenomeFromCreaturesList();
			}else{
				//creatures is empty, so we are going to choose genome 1 from listFittest
				g1 = rouletteCopyGenomeFromFittestList();
			}

			//always get genome 2 from fittestList
			g2 = rouletteCopyGenomeFromFittestList();
			newGenome = NeuralNet.crossOver(g1, g2);

			//create a new creature from genome -1 as generation
			addNewCreature(-1, newGenome);
		}
	}

	/**
	 * Find a less fit individual from the creatures list, return it
	 * @return
     */
	//private Creature rouletteChooseLeastFittestChar(){

	//}

	/**
	 * Copy a genome from the fittest list, based on roulette probability
	 * @return the copied genome
     */
	private ArrayList<Double> rouletteCopyGenomeFromFittestList(){
		//generate a random number between 0 and the highest fitness count
		float slice = MathUtils.random(0, (float)getHighestFitness());

		//this will be set to the chosen chromosome
		ArrayList<Double>chosenChromosome = new ArrayList<Double>();

		//go through the chromosomes adding up the fitness so far
		double fitnessSoFar = 0;
		for(int i = 0; i < listFittestValues.size(); i++){
			fitnessSoFar += listFittestValues.get(i);

			//if the fitnessSoFar > Slice then return the chromosome corresponding with this value
			if(fitnessSoFar >= slice){
				chosenChromosome = listFittestGenomes.get(i);
				break;
			}
		}
		return (ArrayList<Double>)chosenChromosome.clone();
	}

	/**
	 * Copy a genome from creatures list, based on roulette probablity.
	 * @return a copy of the genome
     */
	private ArrayList<Double> rouletteCopyGenomeFromCreaturesList(){
		//generate a random number between 0 and the highest fitness count
		float slice = MathUtils.random(0, (float)getHighestFitness());

		//this will be set to the chosen chromosome
		ArrayList<Double>chosenChromosome = new ArrayList<Double>();

		//go through the chromosomes adding up the fitness so far
		double fitnessSoFar = 0;
		for(int i = 0; i < creatures.size(); i++){
			fitnessSoFar += creatures.get(i).fitness;

			//if the fitnessSoFar > RandomNumberSlice then return chromosome thats here
			if(fitnessSoFar >= slice){
				chosenChromosome = creatures.get(i).neuralNet.getWeights();
				break;
			}
		}
		return (ArrayList<Double>) chosenChromosome.clone();
	}

	private Creature getLeastFittestCreature(){
		double leastFitness = 100000;
		Creature c = null;
		for(int i = 0; i < creatures.size(); i++){
			if(creatures.get(i).fitness < leastFitness){
				leastFitness = creatures.get(i).fitness;
				c = creatures.get(i);
			}
		}
		return c;
	}

	private double getHighestFitness(){
		double fit = 0;
		for(int i = 0; i < listFittestValues.size(); i++){
			if (listFittestValues.get(i) > fit){
				fit = listFittestValues.get(i);
			}
		}
		System.out.println("Highest Fitness: " + fit);
		return fit;
	}

	private void updateFood(){
		for(int i = 0; i < foods.size(); i++){
			foods.get(i).update();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		physicsWorld.dispose();
		debugRenderer.dispose();

		img.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		//System.out.println("keydown");
		if(keycode == Input.Keys.W){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				creatures.get(0).setThrustForwardPressed(true);
			}
		}else if(keycode == Input.Keys.A){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				//creatures.get(0).setRotateLeftPressed(true);
			}
		}else if(keycode == Input.Keys.D){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				//creatures.get(0).setRotateRightPressed(true);
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.W){
			if(!creatures.isEmpty()){
				//System.out.println("W Released");
				creatures.get(0).setThrustForwardPressed(false);
			}
		}else if(keycode == Input.Keys.A){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				//creatures.get(0).setRotateLeftPressed(false);
			}
		}else if(keycode == Input.Keys.D){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				//creatures.get(0).setRotateRightPressed(false);
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	private void setupCollisionHandling() {
		physicsWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// Check to see if the collision is between the second sprite and the bottom of the screen
				// If so apply a random amount of upward force to both objects... just because

				Object a = contact.getFixtureA().getBody().getUserData();
				Object b = contact.getFixtureB().getBody().getUserData();


				if (a instanceof Creature && b instanceof Creature) {
					//Creature to Creature Collision
					//System.out.println("Creature to Creature Collision");
					//we want them to try to mate
					((Creature)a).mate((Creature)b);
				} else if ((a instanceof Creature && b instanceof Food) || (b instanceof Creature && a instanceof Food)) {
					//Creature to Food Collision
					//System.out.println("Creature to Food Collsion");
					Creature c;
					Food f;
					if((a instanceof Creature && b instanceof Food)){
						c = (Creature)a;
						f = (Food)b;
					}else{
						c = (Creature)b;
						f = (Food)a;
					}
					c.eat(f); //The Creature Eats the food.
				}


			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});
	}

	public Vector2 getRandomPosition(Object o){
		Vector2 size;
		if(o instanceof Creature){
			size = ((Creature)o).getSize();
		}else{
			size = ((Food)o).getSize();
		}
		return getRandomPositionFromSize(size);
	}

	public Vector2 getRandomPositionFromSize(Vector2 size){
		float xLimit = Gdx.graphics.getWidth();
		float yLimit = Gdx.graphics.getHeight();
		return new Vector2(MathUtils.random(xLimit)-size.x, MathUtils.random(yLimit)-size.y);
	}

	public float getRandomFoodValue(){
		return MathUtils.random(Food.LIMIT);
	}
}
