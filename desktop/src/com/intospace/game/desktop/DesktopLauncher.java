package com.intospace.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.intospace.game.IntoSpaceGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Into Space");
		config.setWindowedMode(1280, 720);
		config.setResizable(false);
		// config.useVsync(true);
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setForegroundFPS(60);
		config.setIdleFPS(60);
		config.setWindowIcon("Icon.png");
		new Lwjgl3Application(new IntoSpaceGame(), config);
	}
}
