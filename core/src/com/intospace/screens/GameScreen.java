package com.intospace.screens;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.intospace.entities.AIPlayer;
import com.intospace.entities.Player;
import com.intospace.enums.GameState;
import com.intospace.enums.WorldType;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.input.InputManager;
import com.intospace.sounds.SoundManager;
import com.intospace.world.Assets;
import com.intospace.world.ParallaxBackground;
import com.intospace.world.WorldManager;
import com.intospace.world.Tile;

public class GameScreen extends ScreenBase {
	public static final String VERSION = "0.1-alpha";
	public static boolean cameraLocked = true;
	public static long seed;
	public static int elementsOnScreen;

	public GameState state;
	public WorldManager worldManager;

	Box2DDebugRenderer debugRenderer;

	SoundManager soundManager;

	ParallaxBackground background;
	SpriteBatch backgroundBatch;
	SpriteBatch batch;

	Player player;
	AIPlayer ai;
	AIPlayer aiGhost;

	OrthographicCamera gameCamera;

	com.intospace.ui.GameScreen gameScreen;

	Arrive<Vector2> arriveBehavior;
	Wander<Vector2> wanderBehavior;

	ShapeRenderer shapeRenderer = new ShapeRenderer();

	public GameScreen(final Game game) {
		super(game);
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		state = GameState.PAUSED;

		worldManager = new WorldManager(WorldType.EARTH);

		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

		soundManager = SoundManager.getInstance();

		gameScreen = new com.intospace.ui.GameScreen(this);

		gameCamera = new OrthographicCamera();
		gameCamera.setToOrtho(false, width / Constants.PPM, height / Constants.PPM);
		gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0);
		gameCamera.update();

		Array<Texture> textures = new Array<>();
		textures.add(IntoSpaceGame.getInstance().assets.get(Assets.LAYER_SKY));
		textures.add(IntoSpaceGame.getInstance().assets.get(Assets.LAYER_CLOUDS));
		textures.add(IntoSpaceGame.getInstance().assets.get(Assets.LAYER_MOUNTAINS));
		background = new ParallaxBackground(textures);

		backgroundBatch = new SpriteBatch();
		batch = new SpriteBatch();

		ai = new AIPlayer(610, 320, gameCamera, worldManager.getWorld());
		aiGhost = new AIPlayer(0, 420, gameCamera, worldManager.getWorld());
		aiGhost.body.setGravityScale(0);

		/*arriveBehavior = new Arrive<>(ai, player)
				.setTimeToTarget(0.01f)
				.setArrivalTolerance(2f)
				.setDecelerationRadius(0);
		wanderBehavior = new Wander<>(ai)
				.setEnabled(true)
				.setWanderRadius(2f)
				.setWanderOffset(1f)
				.setWanderRate(MathUtils.PI2 * 3);
		ai.setMaxLinearAcceleration(1f);
		ai.setMaxLinearSpeed(1f);
		ai.setBehavior(wanderBehavior);

		aiGhost.setMaxLinearAcceleration(1f);
		aiGhost.setMaxLinearSpeed(1f);
		aiGhost.setBehavior(new Arrive<>(aiGhost, player)
				.setTimeToTarget(0.01f)
				.setArrivalTolerance(2f)
				.setDecelerationRadius(0));
		sun.setIgnoreBody(aiGhost.body);*/

		Vector2 spawnpoint = worldManager.getSpawnpoint();
		player = new Player(spawnpoint.x, spawnpoint.y, gameCamera, worldManager.getWorld());
		gameCamera.position.set(player.getOriginX(), player.getOriginY(), 0);
	}

	@Override
	public void show() {
		state = GameState.RUNNING;
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(new InputManager(this, gameCamera, worldManager, soundManager));
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void update() {
		if (state == GameState.PAUSED)
			return;
		float delta = Gdx.graphics.getDeltaTime();
		worldManager.getWorld().step(1 / 60f, 6, 2);

		// Camera smoothing
		Vector3 target = new Vector3(player.getOriginX(), player.getOriginY() + 0.5f, 0);
		Vector3 cameraPosition = gameCamera.position;
		final float speed = 0.1f, invertedSpeed = 1.0f - speed;
		cameraPosition.scl(invertedSpeed);
		target.scl(speed);
		cameraPosition.add(target);
		gameCamera.position.set(cameraPosition);

		player.update(delta);
		// ai.update(delta);
		// aiGhost.update(delta);
		/*if (Gdx.input.isTouched()) {
			gameCamera.translate(
					-Gdx.input.getDeltaX() / Constants.PPM * gameCamera.zoom,
					Gdx.input.getDeltaY() / Constants.PPM * gameCamera.zoom
			);
		}*/
		/*if (cameraLocked)
			gameCamera.position.set(player.getOriginX(), player.getOriginY(), 0);*/
		gameCamera.update();
		batch.setProjectionMatrix(gameCamera.combined);
		worldManager.update();
		worldManager.getRayHandler().setCombinedMatrix(gameCamera);
		worldManager.getRayHandler().update();

		if (ai.getPosition().dst(player.getPosition()) < 5f) {
			ai.setBehavior(arriveBehavior);
			ai.setMaxLinearAcceleration(3f);
			ai.setMaxLinearSpeed(4f);
		} else if (ai.getPosition().dst(player.getPosition()) > 5f && ai.getBehavior() instanceof Arrive) {
			System.out.println("escaped");
			ai.setBehavior(wanderBehavior);
			ai.setMaxLinearAcceleration(1f);
			ai.setMaxLinearSpeed(1f);
		}
	}

	@Override
	public void render(float delta) {
		if (state == GameState.TRAVELLING) {
			return;
		}

		update();
		final Color backgroundColor = worldManager.getBackgroundColor();
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		background.draw(backgroundBatch, gameCamera.position.x, gameCamera.position.y);

		batch.begin();

		float viewportWidthZoom = gameCamera.viewportWidth * gameCamera.zoom;
		float viewportHeightZoom = gameCamera.viewportHeight * gameCamera.zoom;
		int startX = MathUtils.clamp(
				(int) (gameCamera.position.x - viewportWidthZoom / 2) * 2 - 2,
				-Constants.MAX_X,
				Constants.MAX_X
		);
		int startY = MathUtils.clamp(
				(int) (gameCamera.position.y - viewportHeightZoom / 2) * 2 - 2,
				0,
				Constants.MAX_Y
		);

		int elementsOnScreen = 0;
		// Y: MathUtils.clamp(startY + (int) viewportHeightZoom * 2 + 6, 0, WorldManager.tiles.length)
		// X: MathUtils.clamp(startX + (int) viewportWidthZoom * 2 + 6, 0, WorldManager.tiles.length)
		for (int y = startY; y < MathUtils.clamp(startY + (int) viewportHeightZoom * 2 + 6, 0, WorldManager.tiles.length); y++) {
			for (int x = startX; x < MathUtils.clamp(startX + (int) viewportWidthZoom * 2 + 6, -Constants.MAX_X, WorldManager.tiles.length + Constants.MAX_X); x++) {
				int worldX = x;
				if (x >= Constants.MAX_X) {
					worldX -= Constants.MAX_X;
				} else if (x < 0) {
					worldX = Constants.MAX_X - Math.abs(worldX);
				}
				if (WorldManager.tiles[worldX][y] != null) {
					WorldManager.tiles[worldX][y].render(batch, x);
					elementsOnScreen++;
				}
			}
		}

		GameScreen.elementsOnScreen = elementsOnScreen;

		player.render(batch);
		// ai.render(batch);
		// aiGhost.render(batch);

		batch.end();
		worldManager.getRayHandler().render();

		gameScreen.render();

		shapeRenderer.setProjectionMatrix(gameCamera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.rectLine(0, -1000, 0, 1000, 1);
		shapeRenderer.rectLine(Constants.MAX_X / 2f, -1000, Constants.MAX_X / 2f, 1000, 1);
		shapeRenderer.end();

		// debugRenderer.render(worldManager.getWorld(), gameCamera.combined);
		// Gdx.graphics.setTitle("Into Space (FPS: " + Gdx.graphics.getFramesPerSecond() + ")");
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public void travel(WorldType worldType, float gravity) {
		state = GameState.TRAVELLING;
		worldManager.dispose();
		worldManager = new WorldManager(worldType, gravity);
		Vector2 spawnpoint = worldManager.getSpawnpoint();
		player = new Player(spawnpoint.x, spawnpoint.y, gameCamera, worldManager.getWorld());
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(new InputManager(this, gameCamera, worldManager, soundManager));
		Gdx.input.setInputProcessor(inputMultiplexer);
		state = GameState.RUNNING;
	}

	public void setLightning() {
		float value = cameraLocked ? 0.1f : 1f;
		worldManager.getRayHandler().setAmbientLight(value, value, value, value);
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		gameScreen.dispose();
		worldManager.dispose();
	}
}
