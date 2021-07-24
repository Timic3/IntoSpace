package com.intospace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.intospace.game.IntoSpaceGame;
import com.intospace.world.Assets;

public class GameScreen {
    private final TextureAtlas.AtlasRegion dirt;
    private final TextureAtlas.AtlasRegion grassDirt;
    private final TextureAtlas.AtlasRegion stone;
    private final TextureAtlas.AtlasRegion cobblestone;

    final com.intospace.screens.GameScreen gameScreen;

    public int selected = 0;
    OrthographicCamera uiCamera;
    SpriteBatch ui;
    ShapeRenderer rectangle;
    BitmapFont font;
    float versionWidth;

    public GameScreen(com.intospace.screens.GameScreen gameScreen) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        this.gameScreen = gameScreen;

        uiCamera = new OrthographicCamera(width, height);
        uiCamera.position.set(uiCamera.viewportWidth / 2f, uiCamera.viewportHeight / 2f, 1.0f);
        uiCamera.update();

        TextureAtlas blockAtlas = IntoSpaceGame.getInstance().assets.get(Assets.BLOCKS);
        dirt = blockAtlas.findRegion("Dirt");
        grassDirt = blockAtlas.findRegion("Grass_Dirt");
        stone = blockAtlas.findRegion("Stone");
        cobblestone = blockAtlas.findRegion("Cobblestone");

        ui = new SpriteBatch();

        rectangle = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().markupEnabled = true;
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, com.intospace.screens.GameScreen.VERSION);
        versionWidth = glyphLayout.width;
    }

    public void scrolled(float amountX, float amountY) {
        if (amountY < 0) {
            if (selected + 1 > 3)
                selected = 0;
            else
                ++selected;
        } else {
            if (selected - 1 < 0)
                selected = 3;
            else
                --selected;
        }
    }

    public void render() {
        ui.setProjectionMatrix(uiCamera.combined);
        uiCamera.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        rectangle.begin(ShapeRenderer.ShapeType.Filled);
        rectangle.setColor(0, 0, 0, 0.7f);
        rectangle.rect(28, 28, 32 * 4 + 4 * 5, 40);
        rectangle.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        ui.begin();
        font.draw(ui, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, uiCamera.viewportHeight - 5);
        font.draw(ui, "Camera: " + (com.intospace.screens.GameScreen.cameraLocked ? "[#FF0000]Locked" : "[#00FF00]Unlocked"), 5, uiCamera.viewportHeight - 5 - font.getLineHeight());
        font.draw(ui, "Seed: " + (com.intospace.screens.GameScreen.seed), 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 2);
        font.draw(ui, "Elements on screen: " + (com.intospace.screens.GameScreen.elementsOnScreen), 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 3);

        int seconds = gameScreen.worldManager.getTime();

        @SuppressWarnings("DefaultLocale")
        String time = (String.format("%02d", (seconds % 86400) / 3600) + ":" + String.format("%02d", ((seconds % 86400) % 3600) / 60));

        font.draw(ui, "Time: " + time, 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 4);

        font.draw(ui, com.intospace.screens.GameScreen.VERSION, uiCamera.viewportWidth - versionWidth - 5, font.getLineHeight());

        ui.setColor(1, 1, 1, selected != 0 ? 0.5f : 1);
        ui.draw(grassDirt, 32, 32, 32, 32);
        ui.setColor(1, 1, 1, selected != 1 ? 0.5f : 1);
        ui.draw(dirt, 32 * 2 + 4, 32, 32, 32);
        ui.setColor(1, 1, 1, selected != 2 ? 0.5f : 1);
        ui.draw(stone, 32 * 3 + 4 * 2, 32, 32, 32);
        ui.setColor(1, 1, 1, selected != 3 ? 0.5f : 1);
        ui.draw(cobblestone, 32 * 4 + 4 * 3, 32, 32, 32);

        ui.end();
    }

    public void dispose() {
        ui.dispose();
        font.dispose();
    }
}
