package com.intospace.screens;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.intospace.entities.AIPlayer;
import com.intospace.entities.Player;
import com.intospace.game.Constants;
import com.intospace.world.Assets;
import com.intospace.world.Tile;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenBase {
	public static final String VERSION = "0.1-alpha";
	public static boolean cameraLocked = true;
	public static TextureAtlas.AtlasRegion dirt;
	public static TextureAtlas.AtlasRegion grassDirt;
	public static TextureAtlas.AtlasRegion stone;
	public static TextureAtlas.AtlasRegion cobblestone;

	public Assets assets;

	World world;
	Box2DDebugRenderer debugRenderer;
	RayHandler rayHandler;

	SpriteBatch batch;
	TextureAtlas blockAtlas;

	Player player;
	AIPlayer ai;
	AIPlayer aiGhost;

	OrthographicCamera gameCamera;

	com.intospace.ui.GameScreen gameScreen;

	List<Tile> tiles = new ArrayList<>();

	Arrive<Vector2> arriveBehavior;
	Wander<Vector2> wanderBehavior;

	Sound blockBreak;
	Sound blockPick;

	public GameScreen(Game game) {
		super(game);
	}

	@Override
	public void show() {
		Box2D.init();
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		assets = new Assets();
		assets.load();
		assets.finishLoading();

		Music music = Gdx.audio.newMusic(Gdx.files.internal("music/sunrise.mp3"));
		music.setLooping(true);
		music.setVolume(0.05f);
		music.play();

		blockBreak = Gdx.audio.newSound(Gdx.files.internal("sounds/block_break.mp3"));
		blockPick = Gdx.audio.newSound(Gdx.files.internal("sounds/block_pick.mp3"));

		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);
		world = new World(new Vector2(0, -9.8f), true);
		RayHandler.useDiffuseLight(true);
		RayHandler.setGammaCorrection(true);
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.1f);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(4);
		// rayHandler.setShadows(false);

		// PointLight light = new PointLight(rayHandler, 100, Color.WHITE, 220, 0, 2500 / Constants.PPM);
		// light.setSoftnessLength(2f);
		// light.setXray(true);
		DirectionalLight sun = new DirectionalLight(rayHandler, 1000, new Color(1f, 1f, 1f, 0.8f), -81f);
		sun.setActive(true);
		sun.setStaticLight(false);
		sun.setSoftnessLength(3f);

		gameScreen = new com.intospace.ui.GameScreen();

		gameCamera = new OrthographicCamera();
		gameCamera.setToOrtho(false, width / Constants.PPM, height / Constants.PPM);
		gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0);
		gameCamera.update();

		batch = new SpriteBatch();

		blockAtlas = assets.get(Assets.BLOCKS);
		dirt = blockAtlas.findRegion("Dirt");
		grassDirt = blockAtlas.findRegion("Grass_Dirt");
		stone = blockAtlas.findRegion("Stone");
		cobblestone = blockAtlas.findRegion("Cobblestone");

		player = new Player(0, 320, gameCamera, world);
		ai = new AIPlayer(610, 320, gameCamera, world);
		aiGhost = new AIPlayer(0, 420, gameCamera, world);
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

		for (float x = 0; x < 45; ++x) {
			for (float y = 0; y < 10; ++y) {
				if (y == 9) {
					tiles.add(new Tile(grassDirt, x * 32, y * 32, world));
				} else {
					tiles.add(new Tile(dirt, x * 32, y * 32, world));
				}
			}
		}

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.SPACE)
					cameraLocked = !cameraLocked;
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				testPoint.set(screenX, screenY, 0);
				gameCamera.unproject(testPoint);
				if (button == 0) {
					world.QueryAABB(callback, testPoint.x - 0.5f, testPoint.y - 0.5f, testPoint.x + 0.5f, testPoint.y + 0.5f);
				} else if (button == 1) {
					float alignGridX = (int) (testPoint.x * Constants.PPM) - ((int) (testPoint.x * Constants.PPM) % 32);
					float alignGridY = (int) (testPoint.y * Constants.PPM) - ((int) (testPoint.y * Constants.PPM) % 32);
					if (testPoint.x < 0) {
						alignGridX -= 32;
					}
					if (testPoint.y < 0) {
						alignGridY -= 32;
					}

					TextureAtlas.AtlasRegion selectedTile = null;
					if (gameScreen.selected == 0)
						selectedTile = grassDirt;
					else if (gameScreen.selected == 1)
						selectedTile = dirt;
					else if (gameScreen.selected == 2)
						selectedTile = stone;
					else if (gameScreen.selected == 3)
						selectedTile = cobblestone;

					blockPick.play(1.0f);
					tiles.add(new Tile(selectedTile, (int) alignGridX, (int) alignGridY, world));
				}
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
			public boolean scrolled(float amountX, float amountY) {
				gameScreen.scrolled(amountX, amountY);
				/*
				gameCamera.zoom += gameCamera.zoom * amountY * .1f;
				gameCamera.update();
				return true;
				*/
				return false;
			}
		});
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			if (fixture.getBody().getUserData() instanceof Tile) {
				Tile tile = (Tile) fixture.getBody().getUserData();
				if (testPoint.x > tile.getX() / Constants.PPM &&
						testPoint.x < tile.getX() / Constants.PPM + tile.getWidth() &&
						testPoint.y > tile.getY() / Constants.PPM &&
						testPoint.y < tile.getY() / Constants.PPM + tile.getHeight()) {
					blockBreak.play(1.0f);
					tiles.remove(tile);
					tile.remove();
					return false;
				}
				/*
				if (fixture.getBody() == groundBody) return true;

				if (fixture.testPoint(testPoint.x, testPoint.y)) {
					hitBody = fixture.getBody();
					return false;
				} else
				return true;
				 */
				return true;
			}

			return true;
		}
	};

	public void update() {
		float delta = Gdx.graphics.getDeltaTime();
		world.step(1 / 60f, 6, 2);
		rayHandler.update();
		player.update(delta);
		ai.update(delta);
		aiGhost.update(delta);
		if (Gdx.input.isTouched()) {
			gameCamera.translate(-Gdx.input.getDeltaX() / Constants.PPM, Gdx.input.getDeltaY() / Constants.PPM);
		}
		if (cameraLocked)
			gameCamera.position.set(player.getOriginX(), player.getOriginY(), 0);
		gameCamera.update();
		batch.setProjectionMatrix(gameCamera.combined);
		rayHandler.setCombinedMatrix(gameCamera);

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
		update();
		Gdx.gl.glClearColor(0, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		batch.begin();
		/*for (float x = 0; x < 45; ++x) {
			for (float y = 0; y < 10; ++y) {
				if (y == 9) {
					batch.draw(grassDirt, x * 32 / Constants.PPM, y * 32 / Constants.PPM, 32 / Constants.PPM, 32 / Constants.PPM);
				} else {
					batch.draw(dirt, x * 32 / Constants.PPM, y * 32 / Constants.PPM, 32 / Constants.PPM, 32 / Constants.PPM);
				}
			}
		}*/
		for (Tile tile : tiles) {
			tile.render(batch);
		}
		player.render(batch);
		ai.render(batch);
		aiGhost.render(batch);
		batch.end();
		rayHandler.render();

		gameScreen.render();

		/*
		hud.begin();
		font.draw(hud, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, hudCamera.viewportHeight - 5);
		font.draw(hud, "Camera: " + (cameraLocked ? "[#FF0000]Locked" : "[#00FF00]Unlocked"), 5, hudCamera.viewportHeight - 5 - font.getLineHeight());
		font.draw(hud, VERSION, hudCamera.viewportWidth - versionWidth - 5, font.getLineHeight());

		hud.setColor(1, 1, 1, selected != 0 ? 0.5f : 1);
		hud.draw(grassDirt, 32, 32, 32, 32);
		hud.setColor(1, 1, 1, selected != 1 ? 0.5f : 1);
		hud.draw(dirt, 32 * 2 + 4, 32, 32, 32);
		hud.setColor(1, 1, 1, selected != 2 ? 0.5f : 1);
		hud.draw(stone, 32 * 3 + 4 * 2, 32, 32, 32);
		hud.setColor(1, 1, 1, selected != 3 ? 0.5f : 1);
		hud.draw(cobblestone, 32 * 4 + 4 * 3, 32, 32, 32);

		hud.end();
		*/

		// debugRenderer.render(world, gameCamera.combined);
		// Gdx.graphics.setTitle("Into Space (FPS: " + Gdx.graphics.getFramesPerSecond() + ")");
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		gameScreen.dispose();
		assets.dispose();
		rayHandler.dispose();
		world.dispose();
	}
}
