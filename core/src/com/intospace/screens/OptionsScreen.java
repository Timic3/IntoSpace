package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.intospace.sounds.SoundManager;

public class OptionsScreen extends ScreenBase {
    private Stage stage;
    private ScreenBase previousScreen;

    public OptionsScreen(Game game) {
        super(game);
    }

    public OptionsScreen(Game game, ScreenBase previousScreen) {
        super(game);
        this.previousScreen = previousScreen;
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

        final Preferences settings = Gdx.app.getPreferences("IntoSpace.Settings");
        int fps = settings.getInteger("fps", 60);
        boolean vsync = settings.getBoolean("vsync", true);
        int sfx = settings.getInteger("sfx", 100);
        int music = settings.getInteger("music", 100);

        //final Image title = new Image(new Texture(Gdx.files.internal("images/logo.png")));
        final Label screen = new Label("Options", this.skin);
        //final TextButton play = new TextButton("Play", this.skin);
        //final TextButton options = new TextButton("Options", this.skin);
        //final TextField username = new TextField("", this.skin);
        final Slider fpsSlider = new Slider(30, 144, 1, false, this.skin);
        fpsSlider.setValue(fps);
        final TextField fpsField = new TextField(String.valueOf(fps), this.skin);
        fpsField.setMaxLength(3);
        fpsField.setAlignment(Align.center);
        final CheckBox vSync = new CheckBox("", this.skin);
        vSync.setChecked(vsync);
        final Slider sfxSlider = new Slider(0, 100, 1, false, this.skin);
        sfxSlider.setValue(sfx);
        final TextField sfxField = new TextField(String.valueOf(sfx), this.skin);
        fpsField.setMaxLength(3);
        fpsField.setAlignment(Align.center);
        final Slider musicSlider = new Slider(0, 100, 1, false, this.skin);
        musicSlider.setValue(music);
        final TextField musicField = new TextField(String.valueOf(music), this.skin);
        fpsField.setMaxLength(3);
        fpsField.setAlignment(Align.center);
        final TextButton back = new TextButton("Back", this.skin);
        //table.add(title).padTop(50);
        table.row();
        table.add(screen).colspan(3).padTop(20);
        table.row().padTop(20).padBottom(10);
        table.add(new Label("FPS", this.skin)).width(300).align(Align.left);
        table.add(fpsSlider).width(300);
        table.add(fpsField).width(100).align(Align.center).padLeft(30);
        table.row().padBottom(10);
        table.add(new Label("VSync", this.skin)).width(300).align(Align.left);
        table.add(vSync).colspan(2).align(Align.left);
        table.row().padTop(20).padBottom(10);
        table.add(new Label("Sound Effects", this.skin)).width(300).align(Align.left);
        table.add(sfxSlider).width(300);
        table.add(sfxField).width(100).align(Align.center).padLeft(30);
        table.row().padBottom(10);
        table.add(new Label("Music", this.skin)).width(300).align(Align.left);
        table.add(musicSlider).width(300);
        table.add(musicField).width(100).align(Align.center).padLeft(30);
        //table.add(options).width(250).padTop(10);
        table.row().padTop(50);
        table.add(back).align(Align.left);

        fpsField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return c >= '0' && c <= '9';
            }
        });

        fpsField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    float fps = Float.parseFloat(fpsField.getText());
                    if (fps >= 30 && fps <= 144) {
                        fpsSlider.setValue(fps);
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        });

        fpsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fpsSlider.isDragging()) {
                    fpsField.setText(String.valueOf((int) fpsSlider.getValue()));
                }
            }
        });

        sfxField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    float sfx = Float.parseFloat(sfxField.getText());
                    if (sfx >= 0 && sfx <= 100) {
                        sfxSlider.setValue(sfx);
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (sfxSlider.isDragging()) {
                    sfxField.setText(String.valueOf((int) sfxSlider.getValue()));
                }
            }
        });

        musicField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    float music = Float.parseFloat(musicField.getText());
                    if (music >= 0 && music <= 100) {
                        musicSlider.setValue(music);
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        });

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (musicSlider.isDragging()) {
                    musicField.setText(String.valueOf((int) musicSlider.getValue()));
                    SoundManager.getInstance().setMusic((int) musicSlider.getValue());
                }
            }
        });

        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (previousScreen != null) {
                    game.setScreen(previousScreen);
                } else {
                    game.setScreen(new MainScreen(game));
                }
                settings.putInteger("fps", Integer.parseInt(fpsField.getText()));
                settings.putBoolean("vsync", vSync.isChecked());
                settings.putInteger("sfx", Integer.parseInt(sfxField.getText()));
                settings.putInteger("music", Integer.parseInt(musicField.getText()));
                SoundManager.getInstance().setMusic(Integer.parseInt(musicField.getText()));
                settings.flush();
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
        stage.dispose();
    }
}
