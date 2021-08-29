package com.intospace.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.intospace.entities.Player;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.game.RuntimeVariables;
import com.intospace.world.Assets;
import com.intospace.world.WorldManager;
import com.intospace.world.inventory.InventoryManager;
import com.intospace.world.quests.Quest;
import com.intospace.world.quests.QuestManager;

import java.util.Map;

public class GameScreen {
    private final TextureAtlas.AtlasRegion heart;
    private final TextureAtlas.AtlasRegion armor;

    final com.intospace.screens.GameScreen gameScreen;

    OrthographicCamera uiCamera;
    SpriteBatch ui;
    ShapeRenderer rectangle;
    BitmapFont font;
    float versionWidth;

    QuestManager questManager;
    InventoryManager inventoryManager;

    TextureAtlas.AtlasRegion compassBase;
    TextureAtlas.AtlasRegion compassCursor;

    public GameScreen(com.intospace.screens.GameScreen gameScreen) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        questManager = QuestManager.getInstance();
        inventoryManager = InventoryManager.getInstance();

        this.gameScreen = gameScreen;

        TextureAtlas interfaceAtlas = IntoSpaceGame.getInstance().assets.get(Assets.INTERFACE);
        this.heart = interfaceAtlas.findRegion("Heart");
        this.armor = interfaceAtlas.findRegion("Armor");
        this.compassBase = interfaceAtlas.findRegion("Compass_Base");
        this.compassCursor = interfaceAtlas.findRegion("Compass_Cursor");

        uiCamera = new OrthographicCamera(width, height);
        uiCamera.position.set(uiCamera.viewportWidth / 2f, uiCamera.viewportHeight / 2f, 1.0f);
        uiCamera.update();

        ui = new SpriteBatch();

        rectangle = new ShapeRenderer();

        font = new BitmapFont();
        font.getData().markupEnabled = true;
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, com.intospace.screens.GameScreen.VERSION);
        versionWidth = glyphLayout.width;
    }

    public void render() {
        ui.setProjectionMatrix(uiCamera.combined);
        uiCamera.update();

        /*Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        rectangle.begin(ShapeRenderer.ShapeType.Filled);
        rectangle.setColor(0, 0, 0, 0.7f);
        rectangle.rect(28, 28, 32 * 4 + 4 * 5, 40);
        rectangle.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);*/

        ui.begin();
        font.draw(ui, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, uiCamera.viewportHeight - 5);
        // font.draw(ui, "Camera: " + (com.intospace.screens.GameScreen.cameraLocked ? "[#FF0000]Locked" : "[#00FF00]Unlocked"), 5, uiCamera.viewportHeight - 5 - font.getLineHeight());
        // font.draw(ui, "Seed: " + (.seed), 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 2);
        // font.draw(ui, "Elements on screen: " + (com.intospace.screens.GameScreen.elementsOnScreen), 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 2);

        int seconds = gameScreen.worldManager.getTime();

        @SuppressWarnings("DefaultLocale")
        String time = (String.format("%02d", (seconds % 86400) / 3600) + ":" + String.format("%02d", ((seconds % 86400) % 3600) / 60));

        final long seed = WorldManager.getInstance().getSeed();
        font.draw(ui, "Time: " + time, 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 2);
        font.draw(ui, "Seed: " + seed, 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 3);
        font.draw(ui, "Gold: [#FFD700]" + RuntimeVariables.GOLD, 5, uiCamera.viewportHeight - 5 - font.getLineHeight() * 4);

        font.draw(ui, com.intospace.screens.GameScreen.VERSION, uiCamera.viewportWidth - versionWidth - 5, font.getLineHeight());

        for (int i = 0; i < 15; i++) {
            if (i >= gameScreen.player.getHealth()) {
                ui.setColor(0.3f, 0.3f, 0.3f, 1f);
            } else {
                ui.setColor(1f, 1f, 1f, 1f);
            }
            ui.draw(heart, (uiCamera.viewportWidth - 24) - 18 * i, uiCamera.viewportHeight - 24, 16, 16);
        }

        for (int i = 0; i < 15; i++) {
            if (i < gameScreen.player.getArmor()) {
                ui.draw(armor, (uiCamera.viewportWidth - 24) - 18 * i, uiCamera.viewportHeight - 44, 16, 16);
            }
        }

        Quest quest = questManager.getCurrentQuest();
        font.draw(ui, String.format(quest.description, quest.progress, quest.goal), uiCamera.viewportWidth - 210, uiCamera.viewportHeight - font.getLineHeight() - 34, 200, 0, true);

        int i = 0;
        for (Map.Entry<TextureAtlas.AtlasRegion, Integer> entry : inventoryManager.inventory.entrySet()) {
            if (i == inventoryManager.selectedIndex) {
                ui.setColor(1, 1, 1, 1);
            } else {
                ui.setColor(1, 1, 1, 0.3f);
            }
            ui.draw(entry.getKey(), 40 * i + 16, 32, 32, 32);
            if (entry.getValue() != -1) {
                font.draw(ui, entry.getValue().toString(), 40 * i + 16 - 2, font.getLineHeight() + 24 + 4, 30, 0, false);
            }
            // font.draw(ui, "Plasma tool", 40 * i, font.getLineHeight());
            ++i;
        }

        ui.setColor(1, 1, 1, 1);

        if (gameScreen.player.body.getPosition().dst(gameScreen.rocket.body.getPosition()) > 4.5f) {
            ui.draw(this.compassBase, uiCamera.viewportWidth - 340, uiCamera.viewportHeight - 46, 32, 32);

            float rotation = MathUtils.radiansToDegrees * MathUtils.atan2(-(gameScreen.rocket.body.getPosition().y - gameScreen.player.body.getPosition().y), -(gameScreen.rocket.body.getPosition().x - gameScreen.player.body.getPosition().x));
            ui.draw(this.compassCursor, uiCamera.viewportWidth - 340, uiCamera.viewportHeight - 46, 16, 16, 32, 32, 1.5f, 1.5f, rotation + 90);
        }

        ui.end();
    }

    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.position.set(uiCamera.viewportWidth / 2f, uiCamera.viewportHeight / 2f, 1.0f);
        uiCamera.update();
    }

    public void dispose() {
        ui.dispose();
        font.dispose();
    }
}
