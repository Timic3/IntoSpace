package com.intospace.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.intospace.entities.Enemy;
import com.intospace.entities.Entity;
import com.intospace.entities.Player;
import com.intospace.entities.Rocket;
import com.intospace.enums.GameState;
import com.intospace.enums.WorldType;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.game.RuntimeVariables;
import com.intospace.screens.GameScreen;
import com.intospace.sounds.SoundManager;
import com.intospace.world.Assets;
import com.intospace.world.Tile;
import com.intospace.world.WorldManager;
import com.intospace.world.inventory.InventoryManager;
import com.intospace.world.quests.DilithiumQuest;
import com.intospace.world.quests.Quest;
import com.intospace.world.quests.QuestManager;
import com.intospace.world.quests.RocketQuest;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class InputManager implements InputProcessor {
    GameScreen gameScreen;
    OrthographicCamera gameCamera;
    WorldManager worldManager;
    SoundManager soundManager;

    Vector3 testPoint = new Vector3();

    int buttonPressed = 0;

    public InputManager(GameScreen gameScreen, OrthographicCamera gameCamera, WorldManager worldManager, SoundManager soundManager) {
        this.gameScreen = gameScreen;
        this.gameCamera = gameCamera;
        this.worldManager = worldManager;
        this.soundManager = soundManager;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Constants.DEBUG) {
            if (keycode == Input.Keys.SPACE) {
                GameScreen.cameraLocked = !GameScreen.cameraLocked;
                this.gameScreen.setCameraLocked();
            } else if (keycode == Input.Keys.NUMPAD_0) {
                //this.gameScreen.travel(WorldType.ROCKY, -2);
                RuntimeVariables.GOLD += 9;
            } else if (keycode == Input.Keys.NUMPAD_1) {
                this.gameScreen.travel(WorldType.SANDY, -8);
            } else if (keycode == Input.Keys.NUMPAD_5) {
                gameScreen.player.damage(3);
            } else if (keycode == Input.Keys.NUMPAD_8) {
                QuestManager.getInstance().getCurrentQuest().progress(33);
            }
        }

        if (keycode == Input.Keys.ESCAPE) {
            gameScreen.setMainMenu();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!GameScreen.cameraLocked) return false;
        testPoint.set(screenX, screenY, 0);
        gameCamera.unproject(testPoint);

        this.buttonPressed = button;
        if (button == Input.Buttons.LEFT) {
            this.blockBreak();
        } else if (button == Input.Buttons.RIGHT) {
            this.blockPlace();
        } else if (button == Input.Buttons.MIDDLE) {
            if (Constants.DEBUG) {
                System.out.println(testPoint.x + ", " + testPoint.y);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!GameScreen.cameraLocked) return false;
        testPoint.set(screenX, screenY, 0);
        gameCamera.unproject(testPoint);

        if (this.buttonPressed == Input.Buttons.LEFT) {
            gameScreen.setCursorParticlePosition(testPoint.x, testPoint.y);
            this.blockBreak();
        } else if (this.buttonPressed == Input.Buttons.RIGHT) {
            this.blockPlace();
        } else if (this.buttonPressed == Input.Buttons.MIDDLE) {
            if (Constants.DEBUG) {
                System.out.println(testPoint.x + ", " + testPoint.y);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (!GameScreen.cameraLocked) {
            gameCamera.zoom += gameCamera.zoom * amountY * .1f;
            gameCamera.update();
        }
        InventoryManager.getInstance().scrolled(amountX, amountY);
        return false;
    }

    private void blockBreak() {
        worldManager.getWorld().QueryAABB(fixture -> {
            if (fixture.getBody().getUserData() instanceof Enemy) {
                Body enemy = fixture.getBody();
                if (gameScreen.player.body.getWorldCenter().dst(enemy.getWorldCenter()) < RuntimeVariables.PLASMA_TOOL_RANGE) {
                    Enemy enemyObject = (Enemy) enemy.getUserData();
                    if (enemyObject.damage(RuntimeVariables.PLASMA_TOOL_DAMAGE)) {
                        gameScreen.addPlasmaParticle(enemy.getWorldCenter().x, enemy.getWorldCenter().y);
                    }
                }
                return false;
            } else if (fixture.getBody().getUserData() instanceof Tile) {
                Tile tile = (Tile) fixture.getBody().getUserData();
                final float edge = Constants.MAX_X / 2f;
                float pointX = testPoint.x % edge;
                if (testPoint.x < 0) {
                    pointX = pointX + Constants.MAX_X * tile.getWidth();
                }
                if (pointX > tile.getX() / Constants.PPM &&
                        pointX < tile.getX() / Constants.PPM + tile.getWidth() &&
                        testPoint.y > tile.getY() / Constants.PPM &&
                        testPoint.y < tile.getY() / Constants.PPM + tile.getHeight() &&
                        gameScreen.player.body.getPosition().dst(tile.getX() / Constants.PPM + tile.getWidth() / 2f, tile.getY() / Constants.PPM + tile.getHeight() / 2f) < 3.5f) {
                    Vector3 screen = gameCamera.project(new Vector3(testPoint.x, testPoint.y, 0));
                    soundManager.breakBlock(MathUtils.lerp(-1, 1, screen.x / Gdx.graphics.getWidth()));
                    WorldManager.tiles[(int) (tile.getX() / 32)][(int) (tile.getY() / 32)] = null;
                    tile.remove();
                    if (tile.getTextureName().equals("Dilithium")) {
                        Quest quest = QuestManager.getInstance().getCurrentQuest();
                        if (quest instanceof DilithiumQuest) {
                            quest.progress(1);
                        }
                    } else if (tile.getTextureName().equals("Gold")) {
                        RuntimeVariables.GOLD += 1;
                    } else {
                        LinkedHashMap<TextureAtlas.AtlasRegion, Integer> inventory = InventoryManager.getInstance().inventory;
                        if (inventory.containsKey(tile.getTexture())) {
                            inventory.put(tile.getTexture(), inventory.get(tile.getTexture()) + 1);
                        } else {
                            inventory.put(tile.getTexture(), 1);
                        }
                    }
                    return false;
                }
                return true;
            } else if (fixture.getBody().getUserData() instanceof Rocket) {
                if (worldManager.getState() == GameState.RUNNING) {
                    gameScreen.setShopMenu();
                }
                return false;
            }
            return true;
        }, testPoint.x - 0.00005f, testPoint.y - 0.00005f, testPoint.x + 0.00005f, testPoint.y + 0.00005f);
    }

    private void blockPlace() {
        AtomicReference<Fixture> fixtureHit = new AtomicReference<>();
        float alignGridX = (int) (testPoint.x * Constants.PPM) - ((int) (testPoint.x * Constants.PPM) % 32);
        float alignGridY = (int) (testPoint.y * Constants.PPM) - ((int) (testPoint.y * Constants.PPM) % 32);
        if (gameScreen.player.body.getWorldCenter().dst(alignGridX / Constants.PPM, alignGridY / Constants.PPM) >= 3.5f)
            return;
        if (testPoint.x < 0) {
            alignGridX = (alignGridX - 32) + Constants.MAX_X * 32;
        } else if (testPoint.x > Constants.MAX_X / 2f) {
            alignGridX = alignGridX - Constants.MAX_X * 32;
        }
        if (testPoint.y < 0) {
            alignGridY -= 32;
        }
        float finalAlignGridX = alignGridX;
        float finalAlignGridY = alignGridY;
        worldManager.getWorld().QueryAABB(fixture -> {
            if ((!(fixture.getBody().getUserData() instanceof Tile) && fixture.getBody().getUserData() instanceof Entity) || WorldManager.tiles[(int) (finalAlignGridX / 32)][(int) (finalAlignGridY / 32)] != null) {
                fixtureHit.set(fixture);
                return true;
            }
            return true;
        }, testPoint.x - 0.000025f, testPoint.y - 0.000025f, testPoint.x + 0.000025f, testPoint.y + 0.000025f);

        if (fixtureHit.get() == null) {
            TextureAtlas blockAtlas = IntoSpaceGame.getInstance().assets.get(Assets.BLOCKS);
            InventoryManager inventoryManager = InventoryManager.getInstance();
            LinkedHashMap<TextureAtlas.AtlasRegion, Integer> inventory = InventoryManager.getInstance().inventory;
            TextureAtlas.AtlasRegion selectedTile = inventoryManager.getHeldAtlas();

            if (blockAtlas.getRegions().contains(selectedTile, false) && inventory.get(selectedTile) != 0) {
                inventory.put(selectedTile, inventory.get(selectedTile) - 1);
                Vector3 screen = gameCamera.project(new Vector3(testPoint.x, testPoint.y, 0));
                WorldManager.tiles[(int) (alignGridX / 32)][(int) (alignGridY / 32)] = new Tile(selectedTile, (int) alignGridX, (int) alignGridY, worldManager.getWorld());
                soundManager.placeBlock(MathUtils.lerp(-1, 1, screen.x / Gdx.graphics.getWidth()));
                if (inventory.get(selectedTile) == 0) {
                    inventory.remove(selectedTile);
                    inventoryManager.scrolled(0, 1);
                    gameScreen.player.scrolled(0, 1);
                }
            }
        } else {
            if (fixtureHit.get().getBody().getUserData() instanceof Rocket) {
                Quest quest = QuestManager.getInstance().getCurrentQuest();
                if (quest instanceof RocketQuest) {
                    quest.progress(1);
                }
            }
        }
    }
}
