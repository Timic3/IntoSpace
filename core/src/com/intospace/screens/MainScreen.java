package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainScreen extends ScreenBase {
    private Stage stage;

    public MainScreen(Game game) {
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

        final Image title = new Image(new Texture(Gdx.files.internal("images/logo.png")));
        final Label screen = new Label("Main Menu", this.skin);
        final TextButton play = new TextButton("Play", this.skin);
        final TextButton options = new TextButton("Options", this.skin);
        final TextButton credits = new TextButton("Credits", this.skin);
        final TextButton quit = new TextButton("Quit", this.skin);
        table.add(title).padTop(50);
        table.row();
        table.add(screen).padTop(20);
        table.row();
        table.add(play).width(250).padTop(20);
        table.row();
        table.add(options).width(250).padTop(10);
        table.row();
        table.add(credits).width(250).padTop(10);
        table.row();
        table.add(quit).width(250).padTop(10);

        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new OptionsScreen(game));
            }
        });

        credits.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CreditsScreen(game));
            }
        });

        quit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
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
        Gdx.app.debug("IntoSpace", "Disposing of main screen");
        stage.dispose();
    }
}
