package com.intospace.screens;

import com.badlogic.gdx.*;
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
import com.intospace.entities.Enemy;
import com.intospace.entities.Ghost;
import com.intospace.entities.Player;
import com.intospace.entities.Rocket;
import com.intospace.enums.GameState;
import com.intospace.enums.WorldType;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.game.RuntimeVariables;
import com.intospace.input.InputManager;
import com.intospace.screens.overlay.ShopMenu;
import com.intospace.sounds.SoundManager;
import com.intospace.world.Assets;
import com.intospace.world.ParallaxBackground;
import com.intospace.world.WorldManager;
import com.intospace.world.inventory.InventoryManager;

import java.util.Iterator;

public class GameScreen extends ScreenBase {
	public static final String VERSION = "0.1-alpha";
	public static boolean cameraLocked = true;
	public static int elementsOnScreen;

	public WorldManager worldManager;

	Box2DDebugRenderer debugRenderer;

	SoundManager soundManager;

	ParallaxBackground background;
	SpriteBatch backgroundBatch;
	SpriteBatch batch;

	public Player player;
	public Rocket rocket;
	ParticleEffect rocketEffect;
	ParticleEffect plasmaEffect;
	ParticleEffect cursorEffect;
	ParticleEffectPool plasmaEffectPool;
	Array<ParticleEffectPool.PooledEffect> effects = new Array<>();

	OrthographicCamera gameCamera;

	com.intospace.ui.GameScreen gameScreen;

	ShapeRenderer shapeRenderer = new ShapeRenderer();

	int enemySpawnTicks;

	MainScreen mainMenu;
	ShopMenu shopMenu;

	public GameScreen(final Game game) {
		super(game);
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		worldManager = new WorldManager(WorldType.EARTH);

		debugRenderer = new Box2DDebugRenderer(true, true, true, true, true, true);

		soundManager = SoundManager.getInstance();

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

		Vector2 spawnpoint = worldManager.getSpawnpoint();

		player = new Player(spawnpoint.x, spawnpoint.y, gameCamera, worldManager.getWorld());
		gameCamera.position.set(player.getOriginX(), player.getOriginY(), 0);

		gameScreen = new com.intospace.ui.GameScreen(this);

		rocket = new Rocket(spawnpoint.x, spawnpoint.y, worldManager.getWorld());

		rocketEffect = IntoSpaceGame.getInstance().assets.get(Assets.PARTICLE_ROCKET_EXHAUST);
		rocketEffect.reset(true);
		rocketEffect.scaleEffect(1 / Constants.PPM);
		// rocketEffect.start();

		cursorEffect = IntoSpaceGame.getInstance().assets.get(Assets.PARTICLE_CURSOR_TRAIL);
		cursorEffect.reset(true);
		cursorEffect.scaleEffect(1 / Constants.PPM);
		// cursorEffect.start();

		plasmaEffect = IntoSpaceGame.getInstance().assets.get(Assets.PARTICLE_PLASMA_BURST);
		plasmaEffectPool = new ParticleEffectPool(plasmaEffect, 0, 20);
		plasmaEffect.reset(true);
		plasmaEffect.scaleEffect(1 / Constants.PPM);

		ParticleEmitter particleEmitter = rocketEffect.findEmitter("Rocket_Exhaust");
		final float targetAngle = 270;
		ParticleEmitter.ScaledNumericValue angle = particleEmitter.getAngle();

		float angleHighMin = angle.getHighMin();
		float angleHighMax = angle.getHighMax();
		float spanHigh = angleHighMax - angleHighMin;
		angle.setHigh(targetAngle - spanHigh / 2.0f, targetAngle + spanHigh / 2.0f);

		float angleLowMin = angle.getLowMin();
		float angleLowMax = angle.getLowMax();
		float spanLow = angleLowMax - angleLowMin;
		angle.setLow(targetAngle - spanLow / 2.0f, targetAngle + spanLow / 2.0f);

		mainMenu = new MainScreen(game, true);
		shopMenu = new ShopMenu(game);
	}

	@Override
	public void show() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputManager(this, gameCamera, worldManager, soundManager));
		inputMultiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(inputMultiplexer);
		WorldManager.getInstance().setState(GameState.RUNNING);
	}

	public void update() {
		super.render(Gdx.graphics.getDeltaTime());

		if (WorldManager.getInstance().getState() == GameState.PAUSED)
			return;
		if (WorldManager.getInstance().getState() == GameState.COMMENCE_TRAVEL) {
			WorldManager.getInstance().setState(GameState.TRAVELLING_STARS);
			Gdx.input.setInputProcessor(null);
		}
		float delta = Gdx.graphics.getDeltaTime();
		worldManager.getWorld().step(1 / 60f, 6, 2);

		// Camera smoothing
		if (cameraLocked) {
			Vector3 target = new Vector3(player.getOriginX(), player.getOriginY() + 0.5f, 0);
			Vector3 cameraPosition = gameCamera.position;
			background.addCameraX((gameCamera.position.x - target.x) * 0.1f);
			final float speed = 0.1f, invertedSpeed = 1.0f - speed;
			cameraPosition.scl(invertedSpeed);
			target.scl(speed);
			cameraPosition.add(target);
			gameCamera.position.set(cameraPosition);
		}

		enemySpawnTicks++;
		if (enemySpawnTicks > Constants.ENEMY_SPAWN_MAX_TICKS && worldManager.getState() == GameState.RUNNING) {
			if (worldManager.enemies.size() < Constants.ENEMY_MAX_SPAWN && Math.random() > 0.6f) {
				float xAxis;
				float yAxis;
				if (Math.random() >= 0.5f) {
					// Spawn on X axis
					if (Math.random() >= 0.5f) {
						xAxis = (player.getOriginX() + gameCamera.viewportWidth / 2f) * Constants.PPM + 100;
					} else {
						xAxis = (player.getOriginX() - gameCamera.viewportWidth / 2f) * Constants.PPM - 100;
					}
					yAxis = MathUtils.random((player.getOriginY() - gameCamera.viewportHeight / 2f) * Constants.PPM, (player.getOriginY() + gameCamera.viewportHeight / 2f) * Constants.PPM);
				} else {
					// Spawn on Y axis
					if (Math.random() >= 0.5f) {
						yAxis = (player.getOriginY() + gameCamera.viewportHeight / 2f) * Constants.PPM + 100;
					} else {
						yAxis = (player.getOriginY() - gameCamera.viewportHeight / 2f) * Constants.PPM - 100;
					}
					xAxis = MathUtils.random((player.getOriginX() - gameCamera.viewportWidth / 2f) * Constants.PPM, (player.getOriginX() + gameCamera.viewportWidth/ 2f) * Constants.PPM);
				}
				Vector2 potentialSpawn = new Vector2(xAxis, yAxis);
				if (!WorldManager.getInstance().getRayHandler().pointAtLight(potentialSpawn.x / Constants.PPM, potentialSpawn.y / Constants.PPM)) {
					Ghost aiGhost = new Ghost(potentialSpawn.x, potentialSpawn.y, worldManager.getWorld(), player);
					worldManager.enemies.add(aiGhost);
				}
			}
			enemySpawnTicks = 0;
		}

		Iterator<Enemy> iter = worldManager.enemies.iterator();
		while (iter.hasNext()) {
			Enemy enemy = iter.next();
			if (enemy.body.getPosition().dst(player.body.getPosition()) > 17f || enemy.markRemoval) {
				worldManager.getWorld().destroyBody(enemy.body);
				iter.remove();
			} else {
				enemy.update(delta);
			}
		}

		rocket.update(delta);

		if (WorldManager.getInstance().getState() == GameState.TRAVELLING_STARS) {
			rocket.body.applyLinearImpulse(new Vector2(0, 500), rocket.body.getWorldCenter(), true);
			player.body.setTransform(rocket.body.getPosition().x, rocket.body.getPosition().y, 0);
			gameCamera.position.set(rocket.body.getPosition().x, rocket.body.getPosition().y, 0);
		}

		player.update(delta);

		if (Gdx.input.isTouched() && !cameraLocked) {
			gameCamera.translate(
					-Gdx.input.getDeltaX() / Constants.PPM * gameCamera.zoom,
					Gdx.input.getDeltaY() / Constants.PPM * gameCamera.zoom
			);
		} else if (cameraLocked) {
			gameCamera.zoom = 1;
		}

		gameCamera.update();
		batch.setProjectionMatrix(gameCamera.combined);
		worldManager.update();

		/*if (ai.getPosition().dst(player.getPosition()) < 5f) {
			ai.setBehavior(arriveBehavior);
			ai.setMaxLinearAcceleration(3f);
			ai.setMaxLinearSpeed(4f);
		} else if (ai.getPosition().dst(player.getPosition()) > 5f && ai.getBehavior() instanceof Arrive) {
			System.out.println("escaped");
			ai.setBehavior(wanderBehavior);
			ai.setMaxLinearAcceleration(1f);
			ai.setMaxLinearSpeed(1f);
		}*/
	}

	@Override
	public void render(float delta) {
		if (WorldManager.getInstance().getState() == GameState.TRAVELLING) {
			return;
		}

		update();
		final Color backgroundColor = worldManager.getBackgroundColor();
		if (gameCamera.position.y >= Constants.ATMOSPHERE_END_Y) {
			final float colorCoefficient = 1 - MathUtils.norm(0, Constants.ATMOSPHERE_END_Y, MathUtils.clamp(gameCamera.position.y - Constants.ATMOSPHERE_END_Y, 0, Constants.ATMOSPHERE_END_Y));
			background.setAlpha(colorCoefficient);
			Gdx.gl.glClearColor(backgroundColor.r * colorCoefficient, backgroundColor.g * colorCoefficient, backgroundColor.b * colorCoefficient, backgroundColor.a);
			if (gameCamera.position.y >= Constants.ATMOSPHERE_END_Y * 2 + 100) {
				WorldManager.getInstance().setState(GameState.TRAVELLING);
				if (WorldManager.getInstance().getType() == WorldType.EARTH) {
					this.travel(WorldType.ROCKY, -2);
				} else if (WorldManager.getInstance().getType() == WorldType.ROCKY) {
					this.travel(WorldType.SANDY, -8);
				} else if (WorldManager.getInstance().getType() == WorldType.SANDY) {
					this.travel(WorldType.EARTH, -9.8f);
				}
				return;
			}
		} else {
			Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		}
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		if (worldManager.getType() == WorldType.EARTH) {
			background.draw(backgroundBatch, gameCamera.position.y);
		}

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

		rocket.render(batch);

		if (WorldManager.getInstance().getState() != GameState.TRAVELLING_STARS) {
			player.render(batch);
		} else {
			rocketEffect.setPosition(rocket.body.getPosition().x, rocket.body.getPosition().y - rocket.height / Constants.PPM);
			rocketEffect.draw(batch, delta);
		}

		if (!cursorEffect.isComplete()) {
			cursorEffect.draw(batch, delta);
		}

		for (int i = effects.size - 1; i >= 0; i--) {
			ParticleEffectPool.PooledEffect effect = effects.get(i);
			effect.draw(batch, delta);
			if (effect.isComplete()) {
				effect.free();
				effects.removeIndex(i);
			}
		}

		for (Enemy enemy : worldManager.enemies) {
			enemy.render(batch);
		}

		batch.end();

		if (cameraLocked) {
			worldManager.getRayHandler().setCombinedMatrix(gameCamera);
			worldManager.getRayHandler().update();
			worldManager.getRayHandler().render();
		}

		gameScreen.render();

		shapeRenderer.setProjectionMatrix(gameCamera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		// shapeRenderer.rectLine(0, -1000, 0, 1000, 1);
		// shapeRenderer.rectLine(Constants.MAX_X / 2f, -1000, Constants.MAX_X / 2f, 1000, 1);
		shapeRenderer.end();

		if (player.getHealth() <= 0) {
			game.setScreen(new DeathScreen(game));
			WorldManager.level = 1;
			RuntimeVariables.GOLD = 0;
			RuntimeVariables.DEATHS++;
			InventoryManager.getInstance().reset();
			return;
		}

		if (worldManager.getState() == GameState.SHOPPING) {
			shopMenu.render(delta);
		}

		if (worldManager.getState() == GameState.PAUSED) {
			mainMenu.render(delta);
		}

		// debugRenderer.render(worldManager.getWorld(), gameCamera.combined);
		// Gdx.graphics.setTitle("Into Space (FPS: " + Gdx.graphics.getFramesPerSecond() + ")");
	}

	@Override
	public void resize(int width, int height) {
		gameScreen.resize(width, height);
		gameCamera.setToOrtho(false, width / Constants.PPM, height / Constants.PPM);
		gameCamera.position.set(player.getOriginX(), player.getOriginY(), 0);
		gameCamera.update();
	}

	public void travel(WorldType worldType, float gravity) {
		WorldManager.getInstance().setState(GameState.TRAVELLING);
		worldManager.dispose();
		worldManager = new WorldManager(worldType, gravity);
		Vector2 spawnpoint = worldManager.getSpawnpoint();
		player = new Player(spawnpoint.x, spawnpoint.y, gameCamera, worldManager.getWorld());
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputManager(this, gameCamera, worldManager, soundManager));
		inputMultiplexer.addProcessor(player);
		Gdx.input.setInputProcessor(inputMultiplexer);
		rocket = new Rocket(spawnpoint.x, spawnpoint.y, worldManager.getWorld());
		WorldManager.getInstance().setState(GameState.RUNNING);
	}

	public void setCameraLocked() {
		player.body.setActive(cameraLocked);
	}

	public void setMainMenu() {
		if (worldManager.getState() == GameState.RUNNING) {
			mainMenu.show(this);
			worldManager.setState(GameState.PAUSED);
		} else if (worldManager.getState() == GameState.PAUSED) {
			worldManager.setState(GameState.RUNNING);
			mainMenu.dispose();
			InputMultiplexer inputMultiplexer = new InputMultiplexer();
			inputMultiplexer.addProcessor(new InputManager(this, gameCamera, worldManager, soundManager));
			inputMultiplexer.addProcessor(player);
			Gdx.input.setInputProcessor(inputMultiplexer);
		}
	}

	public void setShopMenu() {
		if (worldManager.getState() == GameState.RUNNING) {
			shopMenu.show(this);
			worldManager.setState(GameState.SHOPPING);
		} else {
			worldManager.setState(GameState.RUNNING);
			shopMenu.dispose();
			InputMultiplexer inputMultiplexer = new InputMultiplexer();
			inputMultiplexer.addProcessor(new InputManager(this, gameCamera, worldManager, soundManager));
			inputMultiplexer.addProcessor(player);
			Gdx.input.setInputProcessor(inputMultiplexer);
		}
	}

	public void addPlasmaParticle(float x, float y) {
		ParticleEffectPool.PooledEffect effect = plasmaEffectPool.obtain();
		effect.setPosition(x, y);
		effects.add(effect);
	}

	public void setCursorParticlePosition(float x, float y) {
		cursorEffect.setDuration(20);
		cursorEffect.setPosition(x, y);
	}

	@Override
	public void dispose() {
		batch.dispose();
		gameScreen.dispose();
		worldManager.dispose();
		// rocketEffect.dispose();
		// cursorEffect.dispose();
		backgroundBatch.dispose();
		for (int i = effects.size - 1; i >= 0; i--)
			effects.get(i).free();
		effects.clear();
	}
}
