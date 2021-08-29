package com.intospace.screens.overlay;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.intospace.game.IntoSpaceGame;
import com.intospace.game.RuntimeVariables;
import com.intospace.screens.*;
import com.intospace.sounds.SoundManager;
import com.intospace.world.Assets;
import com.intospace.world.inventory.ShopItem;

import java.util.ArrayList;

public class ShopMenu extends ScreenBase {
    ArrayList<TextButton> shopButtons = new ArrayList<>();

    public ShopMenu(Game game) {
        super(game);
    }

    @Override
    public void show() {
        this.show(null);
    }

    public void show(GameScreen gameScreen) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.setRound(false);
        table.setBackground(skin.getDrawable("dialogDimMore"));
        table.align(Align.center);
        stage.addActor(table);

        final Image plasmaTool = new Image(IntoSpaceGame.getInstance().assets.get(Assets.PLAYER).findRegion("Plasma_Tool"));
        final Image plasmaTool2 = new Image(IntoSpaceGame.getInstance().assets.get(Assets.PLAYER).findRegion("Plasma_Tool"));
        final Image heart = new Image(IntoSpaceGame.getInstance().assets.get(Assets.INTERFACE).findRegion("Heart"));
        final Image armor = new Image(IntoSpaceGame.getInstance().assets.get(Assets.INTERFACE).findRegion("Armor"));
        final Label screen = new Label("Shop", this.skin);

        final Label plasmaToolRangeLabel = new Label("Plasma Tool Range (Current: " + RuntimeVariables.PLASMA_TOOL_RANGE + ")", this.skin);
        final Label plasmaToolDamageLabel = new Label("Plasma Tool Damage (Current: " + RuntimeVariables.PLASMA_TOOL_DAMAGE + ")", this.skin);
        final Label restoreHealthLabel = new Label("Restore full health", this.skin);
        final Label restoreArmorLabel = new Label("Restore full armor", this.skin);

        final TextButton upgradePlasmaToolRange = new TextButton("Upgrade (-20g)", this.skin);
        upgradePlasmaToolRange.setUserObject(new ShopItem(20));
        shopButtons.add(upgradePlasmaToolRange);
        upgradePlasmaToolRange.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RuntimeVariables.GOLD -= 20;
                checkMoney();
                RuntimeVariables.PLASMA_TOOL_RANGE += 0.5f;
                plasmaToolRangeLabel.setText("Plasma Tool Range (Current: " + RuntimeVariables.PLASMA_TOOL_RANGE + ")");
            }
        });

        final TextButton upgradePlasmaToolDamage = new TextButton("Upgrade (-20g)", this.skin);
        upgradePlasmaToolDamage.setUserObject(new ShopItem(20));
        shopButtons.add(upgradePlasmaToolDamage);
        upgradePlasmaToolDamage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RuntimeVariables.GOLD -= 20;
                checkMoney();
                RuntimeVariables.PLASMA_TOOL_DAMAGE += 1;
                plasmaToolDamageLabel.setText("Plasma Tool Damage (Current: " + RuntimeVariables.PLASMA_TOOL_DAMAGE + ")");
            }
        });

        final TextButton restoreHealth = new TextButton("Purchase (-10g)", this.skin);
        restoreHealth.setUserObject(new ShopItem(10));
        shopButtons.add(restoreHealth);
        restoreHealth.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                RuntimeVariables.GOLD -= 10;
                checkMoney();
                gameScreen.player.setHealth(15);
            }
        });

        final TextButton restoreArmor = new TextButton("Purchase (-10g)", this.skin);
        restoreArmor.setUserObject(new ShopItem(10));
        shopButtons.add(restoreArmor);
        restoreArmor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RuntimeVariables.GOLD -= 10;
                checkMoney();
                gameScreen.player.setArmor(15);
            }
        });

        checkMoney();

        final TextButton back = new TextButton("Back", this.skin);
        table.add(screen);
        table.row();

        HorizontalGroup plasmaToolGroup = new HorizontalGroup();
        plasmaToolGroup.space(10);
        plasmaToolGroup.addActor(plasmaTool);
        plasmaToolGroup.addActor(plasmaToolRangeLabel);
        plasmaToolGroup.addActor(upgradePlasmaToolRange);

        HorizontalGroup plasmaToolDamageGroup = new HorizontalGroup();
        plasmaToolDamageGroup.space(10);
        plasmaToolDamageGroup.addActor(plasmaTool2);
        plasmaToolDamageGroup.addActor(plasmaToolDamageLabel);
        plasmaToolDamageGroup.addActor(upgradePlasmaToolDamage);

        HorizontalGroup healthGroup = new HorizontalGroup();
        healthGroup.space(10);
        healthGroup.addActor(heart);
        healthGroup.addActor(restoreHealthLabel);
        healthGroup.addActor(restoreHealth);

        HorizontalGroup armorGroup = new HorizontalGroup();
        armorGroup.space(10);
        armorGroup.addActor(armor);
        armorGroup.addActor(restoreArmorLabel);
        armorGroup.addActor(restoreArmor);

        table.add(plasmaToolGroup).height(64).padTop(20);
        table.row();
        table.add(plasmaToolDamageGroup).height(64).padTop(10);
        table.row();
        table.add(healthGroup).height(64).padTop(10);
        table.row();
        table.add(armorGroup).height(64).padTop(10);
        table.row();

        table.row().padTop(50);
        table.add(back).align(Align.left);

        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (gameScreen != null) {
                    gameScreen.setShopMenu();
                }
            }
        });
    }

    public void checkMoney() {
        for (TextButton shopButton : shopButtons) {
            ShopItem shopItem = (ShopItem) shopButton.getUserObject();
            if (RuntimeVariables.GOLD < shopItem.price) {
                shopButton.setColor(Color.RED);
                shopButton.setDisabled(true);
            }
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
