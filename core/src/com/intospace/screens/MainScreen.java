package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
    boolean ingame = false;
    Screen currentScreen;

    public MainScreen(Game game) {
        super(game);
    }

    public MainScreen(Game game, boolean ingame) {
        super(game);
        this.ingame = ingame;
    }

    @Override
    public void show() {
        this.show(null);
    }

    public void show(GameScreen gameScreen) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        if (ingame)
            table.setBackground(skin.getDrawable("dialogDim"));
        table.align(Align.top);
        stage.addActor(table);

        final Image title = new Image(new Texture(Gdx.files.internal("images/logo.png")));
        final Label screen = new Label("Main Menu", this.skin);
        final TextButton play = new TextButton(this.ingame ? "Resume" : "Play", this.skin);
        if (ingame) {
            play.setColor(0, 1f, 1f, 1f);
        }
        final TextButton controls = new TextButton("Controls", this.skin);
        final TextButton options = new TextButton("Options", this.skin);
        final TextButton credits = new TextButton("Credits", this.skin);
        final TextButton quit = new TextButton("Quit", this.skin);
        table.add(title).width(title.getWidth() * 0.45f).height(title.getHeight() * 0.45f).padTop(50);
        table.row();
        if (!ingame) {
            table.add(screen).padTop(20);
            table.row();
        }
        table.add(play).width(250).padTop(20);
        table.row();
        table.add(controls).width(250).padTop(20);
        table.row();
        table.add(options).width(250).padTop(10);
        table.row();
        table.add(credits).width(250).padTop(10);
        table.row();
        table.add(quit).width(250).padTop(20);

        MainScreen that = this;
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!ingame) {
                    game.setScreen(new IntroScreen(game));
                } else {
                    if (gameScreen != null) {
                        gameScreen.setMainMenu();
                    }
                    // that.dispose();
                }
            }
        });

        controls.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new ControlsScreen(game));
                currentScreen = new ControlsScreen(game, that);
                currentScreen.show();
            }
        });

        options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new OptionsScreen(game));
                currentScreen = new OptionsScreen(game, that);
                currentScreen.show();
            }
        });

        credits.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new CreditsScreen(game));
                currentScreen = new CreditsScreen(game, that);
                currentScreen.show();
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
        super.render(delta);

        if (!this.ingame) {
            Gdx.gl.glClearColor(0.23f, 0.23f, 0.23f, 1.0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        if (currentScreen == null) {
            stage.act(delta);
            stage.draw();
        } else if (currentScreen instanceof ControlsScreen || currentScreen instanceof OptionsScreen || currentScreen instanceof CreditsScreen) {
            currentScreen.render(delta);
        }
    }

    public void back() {
        this.currentScreen = null;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
