package com.intospace.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.intospace.screens.SplashScreen;
import com.intospace.world.Assets;

public class IntoSpaceGame extends Game {
    public Assets assets;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("Game", "Welcome to Into Space!");

        assets = new Assets();
        assets.preload();
        assets.load();

        this.setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        Gdx.app.log("Game", "Disposed of loaded assets");
        assets.dispose();
    }

    public static IntoSpaceGame getInstance() {
        return (IntoSpaceGame) Gdx.app.getApplicationListener();
    }
}
