package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CreditsScreen extends ScreenBase {
    private Stage stage;

    public CreditsScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        table.align(Align.top);
        stage.addActor(table);

        final Label me = new Label("Timotej M.", this.skin);
        me.setFontScale(0.8f);
        final Label music = new Label("Royalty Free Music", this.skin);
        music.setFontScale(0.8f);
        final Label engine = new Label("LibGDX and third-party libraries", this.skin);
        engine.setFontScale(0.8f);

        table.add(new Label("Programming", this.skin)).padTop(50);
        table.row();
        table.add(me);
        table.row().padTop(20);
        table.add(new Label("Music", this.skin));
        table.row();
        table.add(music);
        table.row().padTop(20);
        table.add(new Label("Libraries", this.skin));
        table.row();
        table.add(engine);
        final TextButton back = new TextButton("Back", this.skin);
        table.row();
        table.add(back).align(Align.left);
        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.23f, 0.23f, 0.23f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        Gdx.app.debug("IntoSpace", "Disposing of credits screen");
        stage.dispose();
    }
}
