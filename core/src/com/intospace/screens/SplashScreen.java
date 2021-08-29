package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.intospace.game.IntoSpaceGame;
import com.intospace.world.Assets;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends ScreenBase {
    private AssetManager assets;

    private ProgressBar progress;

    public SplashScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        assets = IntoSpaceGame.getInstance().assets;

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        table.align(Align.top);
        stage.addActor(table);

        final Label screen = new Label("Loading...", this.skin);
        progress = new ProgressBar(0, 1, 0.001f, false, this.skin);
        table.center().add(screen);
        table.row().padTop(20);
        table.center().add(progress).width(400);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(0.23f, 0.23f, 0.23f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assets.update()) {
            game.setScreen(new MainScreen(game));
        }

        progress.setValue(assets.getProgress());

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
