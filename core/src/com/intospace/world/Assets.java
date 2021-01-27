package com.intospace.world;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets extends AssetManager {
    public static final AssetDescriptor<TextureAtlas> BLOCKS = new AssetDescriptor<>("Blocks.atlas", TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> PLAYER = new AssetDescriptor<>("Player.atlas", TextureAtlas.class);
    public static final AssetDescriptor<TextureAtlas> ENEMIES = new AssetDescriptor<>("Enemies.atlas", TextureAtlas.class);
    public static final AssetDescriptor<Skin> SKIN = new AssetDescriptor<>("skins/vis/uiskin.json", Skin.class);

    public Assets() {
        super();
    }

    public void load() {
        this.load(BLOCKS);
        this.load(PLAYER);
        this.load(ENEMIES);
        this.load(SKIN);
    }

    public void dispose() {
        super.dispose();
    }
}
