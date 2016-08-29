package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {
	World physicsWorld;

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;
	OrthographicCamera camera;
	SpriteBatch batch;
	Texture img;

	ArrayList<Creature> creatures;

	final float PIXELS_TO_METERS = 100f;
	
	@Override
	public void create () {
		physicsWorld = new World(new Vector2(0, 0), true);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		createCreatures();
	}

	private void createCreatures(){
		creatures = new ArrayList<Creature>();


		//create a single creature for testing
		Vector2 position = new Vector2(17, 12);
		Vector2 size = new Vector2(50 , 50);
		Creature c = new Creature(this, physicsWorld, position,  size);
		System.out.println("POS: " + (int)c.getPos().x + ":" + c.getPos().y);
		creatures.add(c);
	}

	@Override
	public void render () {
		camera.update();
		updateCreatures();
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
	
	@Override
	public void dispose () {
		batch.dispose();
		physicsWorld.dispose();
		debugRenderer.dispose();

		img.dispose();
	}
}
