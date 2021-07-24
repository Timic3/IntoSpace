package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class IntroScreen extends ScreenBase {
    private Stage stage;

    public IntroScreen(Game game) {
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

        final Label story = new Label(
                "You wake up on a familiar planet, deserted and full of mysteries.\n\n" +
                "Aside from an unknown object that looks like a transport of some kind and mysterious tool in your hand, everything seems natural.\n\n" +
                "You don't remember anything of your previous life. Explore the universe and create your own story.", this.skin);
        story.setWrap(true);
        story.setAlignment(Align.center);
        table.add(story).width(800);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.23f, 0.23f, 0.23f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY) || Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
