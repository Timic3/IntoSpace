package com.intospace.world;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets extends AssetManager {
    public static final AssetDescriptor<TextureAtlas> BLOCKS = new AssetDescriptor<>("Blocks.atlas", TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> PLAYER = new AssetDescriptor<>("Player.atlas", TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> ENEMIES = new AssetDescriptor<>("Enemies.atlas", TextureAtlas.class);
    public static final AssetDescriptor<Skin> SKIN = new AssetDescriptor<>("skins/vis/uiskin.json", Skin.class);

    public static final AssetDescriptor<Texture> LAYER_SKY = new AssetDescriptor<>("layers/Layer.Earth.Sky.png", Texture.class);
    public static final AssetDescriptor<Texture> LAYER_CLOUDS = new AssetDescriptor<>("layers/Layer.Earth.Clouds.png", Texture.class);
    public static final AssetDescriptor<Texture> LAYER_MOUNTAINS = new AssetDescriptor<>("layers/Layer.Earth.Mountains.png", Texture.class);

    public Assets() {
        super();
    }

    public void preload() {
        // Things that need to be loaded in order to show something
        // on the screen (like UI skin)
        this.load(SKIN);

        // Wait and load before proceeding
        this.finishLoading();
    }

    public void load() {
        this.load(BLOCKS);
        this.load(PLAYER);
        this.load(ENEMIES);

        this.load(LAYER_SKY);
        this.load(LAYER_CLOUDS);
        this.load(LAYER_MOUNTAINS);
    }

    public void dispose() {
        super.dispose();
    }
}
