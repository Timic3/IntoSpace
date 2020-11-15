package com.intospace.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.intospace.entities.Player;

public class IntoSpaceGame extends ApplicationAdapter {
	static final String VERSION = "0.1-alpha";
	SpriteBatch batch;
	SpriteBatch hud;
	TextureAtlas blockAtlas;
	TextureAtlas textureAtlas;

	BitmapFont font;
	float versionWidth;

	TextureAtlas.AtlasRegion dirt;
	TextureAtlas.AtlasRegion grassDirt;

	Player player;

	boolean cameraLocked;
	OrthographicCamera gameCamera;
	OrthographicCamera hudCamera;

	@Override
	public void create() {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		gameCamera = new OrthographicCamera();
		gameCamera.setToOrtho(false, width, height);
		gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0);
		gameCamera.update();

		hudCamera = new OrthographicCamera(width, height);
		hudCamera.position.set(hudCamera.viewportWidth / 2f, hudCamera.viewportHeight / 2f, 1.0f);
		hudCamera.update();

		batch = new SpriteBatch();
		hud = new SpriteBatch();
		font = new BitmapFont();
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(font, VERSION);
		versionWidth = glyphLayout.width;

		blockAtlas = new TextureAtlas("Blocks.atlas");
		dirt = blockAtlas.findRegion("Dirt");
		grassDirt = blockAtlas.findRegion("Grass_Dirt");

		textureAtlas = new TextureAtlas("Player.atlas");
		Animation<TextureRegion> runningAnimation = new Animation<TextureRegion>(0.070f, textureAtlas.findRegions("Player_Running"), Animation.PlayMode.LOOP);
		player = new Player(0, 0, textureAtlas.findRegion("Player_Idle"), textureAtlas.findRegion("Player_Hand"), runningAnimation, gameCamera);

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
				gameCamera.zoom += gameCamera.zoom * amountY * .1f;
				gameCamera.update();
				return true;
			}
		});
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public void update() {
		if (Gdx.input.isTouched()) {
			gameCamera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
		}
		if (cameraLocked)
			gameCamera.position.set(player.getOriginX(), player.getOriginY(), 0);
		gameCamera.update();
		batch.setProjectionMatrix(gameCamera.combined);
		hudCamera.update();
		hud.setProjectionMatrix(hudCamera.combined);
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		player.update(delta);
		update();
		Gdx.gl.glClearColor(0, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		for (int x = 0; x < 45; ++x) {
			for (int y = 0; y < 10; ++y) {
				if (y == 9) {
					batch.draw(grassDirt, x * 32, y * 32);
				} else {
					batch.draw(dirt, x * 32, y * 32);
				}
			}
		}
		player.render(batch);
		batch.end();

		hud.begin();
		font.getData().markupEnabled = true;
		font.draw(hud, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, hudCamera.viewportHeight - 5);
		font.draw(hud, "Camera: " + (cameraLocked ? "[#FF0000]Locked" : "[#00FF00]Unlocked"), 5, hudCamera.viewportHeight - 5 - font.getLineHeight());
		font.draw(hud, VERSION, hudCamera.viewportWidth - versionWidth - 5, font.getLineHeight());
		hud.end();
		// Gdx.graphics.setTitle("Into Space (FPS: " + Gdx.graphics.getFramesPerSecond() + ")");
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		hud.dispose();
		font.dispose();
		blockAtlas.dispose();
		textureAtlas.dispose();
	}
}
