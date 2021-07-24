package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.intospace.game.IntoSpaceGame;
import com.intospace.world.Assets;

public class ScreenBase implements Screen {
    Game game;
    Skin skin;

    public ScreenBase(Game game) {
        this.game = game;
        this.skin = IntoSpaceGame.getInstance().assets.get(Assets.SKIN);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
        this.dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("Screen", "Disposing of " + getClass().getSimpleName());
    }
}
