package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TransitionScreen implements Screen {
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Screen currentScreen;
    private Screen nextScreen;

    private Game game;

    private float alpha = 0;
    private boolean fadeDirection = true;

    public TransitionScreen(Screen current, Screen next, Game game) {
        this.currentScreen = current;
        this.nextScreen = next;

        game.setScreen(next);
        game.setScreen(current);

        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        if (fadeDirection) {
            currentScreen.render(Gdx.graphics.getDeltaTime());
        } else {
            nextScreen.render(Gdx.graphics.getDeltaTime());
        }

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(1, 1, 1, alpha);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        if (alpha >= 1) {
            fadeDirection = false;
        }
        else if (alpha <= 0 && !fadeDirection) {
            game.setScreen(nextScreen);
        }
        alpha += fadeDirection ? 0.1 : -0.1;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
