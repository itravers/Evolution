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

	final float PIXELS_TO_METERS = 100f;
	
	@Override
	public void create () {
		physicsWorld = new World(new Vector2(0, 0), true);
		setupCollisionHandling();
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		Gdx.input.setInputProcessor(this);
		createCreatures();
		createFood(2);
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

	private void createCreatures(){
		creatures = new ArrayList<Creature>();
		//create a single creature for testing
		Vector2 position = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() / 2);
		Vector2 size = new Vector2(5 , 10);
		Creature c = new Creature(this, physicsWorld, position,  size);
		System.out.println("CPOS: " + (int)c.getPos().x + ":" + c.getPos().y);
		creatures.add(c);
	}

	@Override
	public void render () {
		camera.update();
		updateCreatures();
		updateFood();
		physicsWorld.step(1f/60f,6, 2);




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
		for(int i = 0; i < creatures.size(); i++){
			creatures.get(i).update();
		}
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
				creatures.get(0).setRotateLeftPressed(true);
			}
		}else if(keycode == Input.Keys.D){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				creatures.get(0).setRotateRightPressed(true);
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
				creatures.get(0).setRotateLeftPressed(false);
			}
		}else if(keycode == Input.Keys.D){
			if(!creatures.isEmpty()){
				//System.out.println("W Pressed");
				creatures.get(0).setRotateRightPressed(false);
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
					System.out.println("Creature to Creature Collision");
					//we want them to try to mate
					//a.mate(b);
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
