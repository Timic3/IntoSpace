package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.intospace.game.IntoSpaceGame;
import com.intospace.world.Assets;

public class ScreenBase implements Screen {
    protected Game game;
    protected Skin skin;
    public Stage stage;
    private int gracePeriod = 0;

    public ScreenBase(Game game) {
        this.game = game;
        this.skin = IntoSpaceGame.getInstance().assets.get(Assets.SKIN);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        gracePeriod++;
        if (Gdx.input.isKeyPressed(Input.Keys.F11) && gracePeriod >= 30) {
            gracePeriod = 0;
            if (Gdx.graphics.isFullscreen())
                Gdx.graphics.setWindowedMode(1280, 720);
            else
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null)
            stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("Screen", "Disposing of " + getClass().getSimpleName());
    }
}
