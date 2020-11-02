package com.intospace.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class IntoSpaceGame extends ApplicationAdapter {
	static final String VERSION = "0.1-alpha";
	SpriteBatch batch;
	SpriteBatch hud;
	TextureAtlas atlas;

	BitmapFont font;
	float versionWidth;

	TextureAtlas.AtlasRegion dirt;
	TextureAtlas.AtlasRegion grassDirt;

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

		atlas = new TextureAtlas("Blocks.atlas");
		dirt = atlas.findRegion("Dirt");
		grassDirt = atlas.findRegion("Grass_Dirt");

		Gdx.input.setInputProcessor(new InputProcessor() {
			@Override
			public boolean keyDown(int keycode) {
				return false;
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
				return false;
			}
		});
	}

	public void update() {
		if (Gdx.input.isTouched()) {
			gameCamera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
		}
		gameCamera.update();
		batch.setProjectionMatrix(gameCamera.combined);
		hudCamera.update();
		hud.setProjectionMatrix(hudCamera.combined);
	}

	@Override
	public void render() {
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
		batch.end();

		hud.begin();
		font.draw(hud, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, hudCamera.viewportHeight - 5);
		font.draw(hud, VERSION, hudCamera.viewportWidth - versionWidth - 5, font.getLineHeight());
		hud.end();
		// Gdx.graphics.setTitle("Into Space (FPS: " + Gdx.graphics.getFramesPerSecond() + ")");
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		hud.dispose();
		font.dispose();
	}
}
