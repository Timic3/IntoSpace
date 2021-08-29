package com.intospace.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ControlsScreen extends ScreenBase {
    MainScreen mainScreen;

    public ControlsScreen(Game game) {
        super(game);
    }
    public ControlsScreen(Game game, MainScreen mainScreen) {
        super(game);
        this.mainScreen = mainScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        table.align(Align.top);
        stage.addActor(table);

        final Label lmb = new Label("Left mouse click - Shoot/break block/open shop by clicking on the rocket", this.skin);
        lmb.setFontScale(0.8f);
        final Label rmb = new Label("Right mouse click - Interact/place block", this.skin);
        rmb.setFontScale(0.8f);
        final Label wasd = new Label("WASD - Move player", this.skin);
        wasd.setFontScale(0.8f);

        table.add(new Label("Controls", this.skin)).padTop(50);
        table.row();
        table.add(lmb);
        table.row().padTop(10);
        table.add(rmb);
        table.row().padTop(10);
        table.add(wasd);
        table.row().padTop(10);

        final TextButton back = new TextButton("Back", this.skin);
        table.row();
        table.add(back).align(Align.left);

        ControlsScreen that = this;
        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                // game.setScreen(new MainScreen(game));
                mainScreen.back();
                that.dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

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
