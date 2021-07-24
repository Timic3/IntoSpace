package com.intospace.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.intospace.enums.GameState;
import com.intospace.enums.WorldType;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.screens.GameScreen;
import com.intospace.sounds.SoundManager;
import com.intospace.world.Assets;
import com.intospace.world.Tile;
import com.intospace.world.WorldManager;

public class InputManager implements InputProcessor {
    GameScreen gameScreen;
    OrthographicCamera gameCamera;
    WorldManager worldManager;
    SoundManager soundManager;

    public InputManager(GameScreen gameScreen, OrthographicCamera gameCamera, WorldManager worldManager, SoundManager soundManager) {
        this.gameScreen = gameScreen;
        this.gameCamera = gameCamera;
        this.worldManager = worldManager;
        this.soundManager = soundManager;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            GameScreen.cameraLocked = !GameScreen.cameraLocked;
            this.gameScreen.setLightning();
        } else if (keycode == Input.Keys.ESCAPE) {
            // this.gameScreen.setState(GameState.PAUSED);
            // this.gameScreen.setScreen(new OptionsScreen(game, that));
        } else if (keycode == Input.Keys.NUMPAD_0) {
            this.gameScreen.travel(WorldType.ROCKY, -2);
        } else if (keycode == Input.Keys.NUMPAD_1) {
            this.gameScreen.travel(WorldType.SANDY, -8);
        } else if (keycode == Input.Keys.NUMPAD_5) {
            worldManager.sun.setStaticLight(!worldManager.sun.isStaticLight());
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    Vector3 testPoint = new Vector3();
    QueryCallback blockDestroy = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.getBody().getUserData() instanceof Tile) {
                Tile tile = (Tile) fixture.getBody().getUserData();
                final float edge = Constants.MAX_X / 2f;
                float pointX = testPoint.x % edge;
                if (testPoint.x < 0) {
                    pointX = pointX + Constants.MAX_X * tile.getWidth();
                }
                if (pointX > tile.getX() / Constants.PPM &&
                        pointX < tile.getX() / Constants.PPM + tile.getWidth() &&
                        testPoint.y > tile.getY() / Constants.PPM &&
                        testPoint.y < tile.getY() / Constants.PPM + tile.getHeight()) {
                    Vector3 screen = gameCamera.project(new Vector3(testPoint.x, testPoint.y, 0));
                    soundManager.breakBlock(MathUtils.lerp(-1, 1, screen.x / Gdx.graphics.getWidth()));
                    // tiles.remove(tile);
                    WorldManager.tiles[(int) (tile.getX() / 32)][(int) (tile.getY() / 32)] = null;
                    tile.remove();
                    return false;
                }
                return true;
            }

            return true;
        }
    };

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        testPoint.set(screenX, screenY, 0);
        gameCamera.unproject(testPoint);
        if (button == Input.Buttons.LEFT) {
            worldManager.getWorld().QueryAABB(blockDestroy, testPoint.x - 0.5f, testPoint.y - 0.5f, testPoint.x + 0.5f, testPoint.y + 0.5f);
        } else if (button == Input.Buttons.RIGHT) {
            float alignGridX = (int) (testPoint.x * Constants.PPM) - ((int) (testPoint.x * Constants.PPM) % 32);
            float alignGridY = (int) (testPoint.y * Constants.PPM) - ((int) (testPoint.y * Constants.PPM) % 32);
            if (testPoint.x < 0) {
                alignGridX = (alignGridX - 32) + Constants.MAX_X * 32;
            } else if (testPoint.x > Constants.MAX_X / 2f) {
                alignGridX = alignGridX - Constants.MAX_X * 32;
            }
            if (testPoint.y < 0) {
                alignGridY -= 32;
            }

            // tiles.add(new Tile(selectedTile, (int) alignGridX, (int) alignGridY, world));
            if (WorldManager.tiles[(int) alignGridX / 32][(int) alignGridY / 32] == null) {
                // TODO: Add proper selector
                TextureAtlas blockAtlas = IntoSpaceGame.getInstance().assets.get(Assets.BLOCKS);
                TextureAtlas.AtlasRegion selectedTile = blockAtlas.findRegion("Dirt");

                WorldManager.tiles[(int) alignGridX / 32][(int) alignGridY / 32] = new Tile(selectedTile, (int) alignGridX, (int) alignGridY, worldManager.getWorld());
                soundManager.placeBlock(MathUtils.lerp(-1, 1, (float) screenX / Gdx.graphics.getWidth()));
            }
        } else if (button == Input.Buttons.MIDDLE) {
            System.out.println(testPoint.x + ", " + testPoint.y);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // gameScreen.scrolled(amountX, amountY);
        if (!GameScreen.cameraLocked) {
            gameCamera.zoom += gameCamera.zoom * amountY * .1f;
            gameCamera.update();
        }
        return false;
    }
}
