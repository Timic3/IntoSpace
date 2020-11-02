package com.intospace.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.intospace.game.IntoSpaceGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;
		config.title = "Into Space";
		config.forceExit = false;
		new LwjglApplication(new IntoSpaceGame(), config);
	}
}
