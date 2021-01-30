package com.intospace.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static SoundManager soundManager;

    private final Preferences settings = Gdx.app.getPreferences("IntoSpace.Settings");

    private final Music music;

    private final Sound blockBreak;
    private final Sound blockPlace;

    private SoundManager() {
        music = Gdx.audio.newMusic(Gdx.files.internal("music/sunrise.mp3"));
        music.setLooping(true);
        music.setVolume(settings.getInteger("music", 100) / 100f);
        music.play();

        blockBreak = Gdx.audio.newSound(Gdx.files.internal("sounds/block_break.mp3"));
        blockPlace = Gdx.audio.newSound(Gdx.files.internal("sounds/block_pick.mp3"));
    }

    public void setSfx(int volume) {
        // TODO
    }

    public void setMusic(int volume) {
        music.setVolume(volume / 100f);
    }

    public void placeBlock(float x) {
        blockPlace.play(settings.getInteger("sfx", 100) / 100f, 1, x);
    }

    public void breakBlock(float x) {
        blockBreak.play(settings.getInteger("sfx", 100) / 100f, 1, x);
    }

    public static SoundManager getInstance() {
        if (soundManager == null) {
            soundManager = new SoundManager();
        }

        return soundManager;
    }
}
