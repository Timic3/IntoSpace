package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DeathScreen extends ScreenBase {
    private int gracePeriod = 0;

    public DeathScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        table.align(Align.center);
        stage.addActor(table);

        final Label story = new Label("You died :(", this.skin);
        story.setWrap(true);
        story.setAlignment(Align.center);
        story.setFontScale(1f);
        table.add(story).width(800);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(0.23f, 0.23f, 0.23f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        gracePeriod++;
        if ((Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) && gracePeriod >= 100) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
