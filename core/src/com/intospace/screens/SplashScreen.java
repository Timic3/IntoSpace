package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends ScreenBase {
    private Stage stage;
    private float elapsed;

    private ProgressBar progress;

    public SplashScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        table.align(Align.top);
        stage.addActor(table);

        final Label screen = new Label("Loading...", this.skin);
        progress = new ProgressBar(0, 5, 0.001f, false, this.skin);
        table.center().add(screen);
        table.row().padTop(20);
        table.center().add(progress).width(400);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.23f, 0.23f, 0.23f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        elapsed += delta;
        progress.setValue(elapsed * 6);

        if (elapsed > 1) {
            game.setScreen(new MainScreen(game));
        }
    }

    @Override
    public void dispose() {
        Gdx.app.debug("IntoSpace", "Disposing of splash screen");
        stage.dispose();
    }
}
