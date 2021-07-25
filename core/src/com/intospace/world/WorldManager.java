package com.intospace.world;

import box2dLight.DirectionalLight;
import box2dLight.RayHandler;
import box2dLight.RayHandlerOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.intospace.enums.DayNightCycle;
import com.intospace.enums.WorldType;
import com.intospace.game.Constants;
import com.intospace.game.IntoSpaceGame;
import com.intospace.world.generators.OpenSimplex2F;
import com.intospace.world.generators.PerlinNoise;

public class WorldManager implements Disposable {
    public static Tile[][] tiles = new Tile[1000][1000];
    public DirectionalLight sun;

    WorldType type;
    long seed;

    private PerlinNoise perlinNoise;
    private final World world;
    RayHandler rayHandler;
    float sunDirection = 270.05f;

    public WorldManager(WorldType type) {
        this(type, MathUtils.random(1000000L, 100000000L), -9.8f);
    }

    public WorldManager(WorldType type, float gravity) {
        this(type, MathUtils.random(1000000L, 100000000L), gravity);
    }

    public WorldManager(WorldType type, long seed, float gravity) {
        Box2D.init();
        this.type = type;
        this.seed = seed;
        this.world = new World(new Vector2(0, gravity), true);
        generate();
    }

    public void generate() {
        OpenSimplex2F simplexNoise = new OpenSimplex2F(seed);
        perlinNoise = new PerlinNoise(seed, type.getFlatness());

        Gdx.app.log("World", "Seed: " + seed);

        TextureAtlas blockAtlas = IntoSpaceGame.getInstance().assets.get(Assets.BLOCKS);
        TextureAtlas.AtlasRegion dirt = blockAtlas.findRegion("Dirt");
        TextureAtlas.AtlasRegion grassDirt = blockAtlas.findRegion("Grass_Dirt");
        TextureAtlas.AtlasRegion stone = blockAtlas.findRegion("Stone");
        TextureAtlas.AtlasRegion moonStone = blockAtlas.findRegion("Moon_Stone");
        TextureAtlas.AtlasRegion sand = blockAtlas.findRegion("Sand");
        TextureAtlas.AtlasRegion sandStone = blockAtlas.findRegion("Sand_Stone");
        TextureAtlas.AtlasRegion cobblestone = blockAtlas.findRegion("Cobblestone");
        TextureAtlas.AtlasRegion dilithium = blockAtlas.findRegion("Dilithium");

        TextureAtlas.AtlasRegion[] layers = null;

        RayHandlerOptions rayHandlerOptions = new RayHandlerOptions();
        rayHandlerOptions.setDiffuse(true);
        rayHandlerOptions.setGammaCorrection(true);
        rayHandlerOptions.setPseudo3d(false, true);

        rayHandler = new RayHandler(world, rayHandlerOptions);
        rayHandler.setAmbientLight(DayNightCycle.MIDDAY.getAmbientColor());
        rayHandler.setBlur(true);
        rayHandler.setBlurNum(3);
        rayHandler.setCulling(false);
        rayHandler.setShadows(true);

        sun = new DirectionalLight(rayHandler, 1000, DayNightCycle.MIDDAY.getLightColor(), 270.05f);
        sun.setHeight(15);
        sun.setActive(true);
        sun.setStaticLight(false);
        sun.setSoftnessLength(2f); // 3f
        DirectionalLight.setGlobalContactFilter((short) 0b01, (short) 0, (short) 0b10);

        switch (type) {
            case EARTH:
                layers = new TextureAtlas.AtlasRegion[] { grassDirt, dirt, stone, cobblestone };
                break;
            case ROCKY:
                layers = new TextureAtlas.AtlasRegion[] { moonStone, moonStone, stone, cobblestone };
                break;
            case SANDY:
                layers = new TextureAtlas.AtlasRegion[] { sand, sandStone, cobblestone, cobblestone };
                break;
            default:
                break;
        }

        double edgeContingency;
        int edgeMidpoint = (perlinNoise.getNoise(-Constants.MIN_X, Constants.MAX_Y - Constants.MIN_Y - 50) + perlinNoise.getNoise(Constants.MAX_X - Constants.MIN_X, Constants.MAX_Y - Constants.MIN_Y - 50)) / 2;
        for (int x = Constants.MIN_X; x < Constants.MAX_X; ++x) {
            if (x < Constants.EDGE_OFFSET) {
                edgeContingency = x / (double) Constants.EDGE_OFFSET;
            } else if (x >= Constants.MAX_X - Constants.EDGE_OFFSET) {
                edgeContingency = 1 + (Constants.MAX_X - Constants.EDGE_OFFSET - x) / (double) Constants.EDGE_OFFSET;
            } else {
                edgeContingency = 1;
            }
            int worldHeight = 100 + (int) (perlinNoise.getNoise(x - Constants.MIN_X, Constants.MAX_Y - Constants.MIN_Y - 50) * edgeContingency + (1 - edgeContingency) * edgeMidpoint);
            for (int y = Constants.MIN_Y; y < worldHeight; ++y) {
                if (y == worldHeight - 1) {
                    tiles[x][y] = new Tile(layers[0], x * 32, y * 32, world);
                    continue;
                }
                if (y < Constants.MIN_Y + 15) {
                    tiles[x][y] = new Tile(layers[3], x * 32, y * 32, world);
                    continue;
                }
                if (y > worldHeight - 20) {
                    tiles[x][y] = new Tile(layers[1], x * 32, y * 32, world);
                    continue;
                }
                double noise = simplexNoise.noise2_XBeforeY(x / 32f, y / 18f) + (1 - edgeContingency);
                if (y < worldHeight - Constants.MAX_Y + 100 && noise > -0.1f) { // -0.1f
                    tiles[x][y] = new Tile(layers[3], x * 32, y * 32, world);
                } else if (noise > -0.5f) { // -0.5f
                    double oreNoise = simplexNoise.noise2(x, y);
                    if (oreNoise >= 0.85f) {
                        tiles[x][y] = new Tile(dilithium, x * 32, y * 32, world);
                    } else {
                        tiles[x][y] = new Tile(layers[2], x * 32, y * 32, world);
                    }
                }
            }
        }
    }

    public void update() {
        this.sunDirection -= 0.005f;
        if (Math.floor(this.sunDirection) <= -90f) {
            this.sunDirection = 270f;
        }

        if (Math.abs(this.sunDirection % 90) < 0.1) {
            this.sun.setDirection(this.sunDirection - 0.1f);
        } else {
            this.sun.setDirection(this.sunDirection);
        }

        float p = (this.sunDirection + 90f) / 360f;
        if (p <= 1f && p >= 0.75f) {
            rayHandler.setAmbientLight(lerpColors(DayNightCycle.MIDDAY.getAmbientColor(), DayNightCycle.SUNSET.getAmbientColor(), 1f - (p - 0.75f) * 4f));
            sun.setColor(lerpColors(DayNightCycle.MIDDAY.getLightColor(), DayNightCycle.SUNSET.getLightColor(), 1f - (p - 0.75f) * 4f));
        } else if (p < 0.75f && p >= 0.5f) {
            rayHandler.setAmbientLight(lerpColors(DayNightCycle.SUNSET.getAmbientColor(), DayNightCycle.MIDNIGHT.getAmbientColor(), 1f - (p - 0.5f) * 4f));
            sun.setColor(lerpColors(DayNightCycle.SUNSET.getLightColor(), DayNightCycle.MIDNIGHT.getLightColor(), 1f - (p - 0.5f) * 4f));
        } else if (p < 0.5f && p >= 0.25f) {
            rayHandler.setAmbientLight(lerpColors(DayNightCycle.MIDNIGHT.getAmbientColor(), DayNightCycle.SUNRISE.getAmbientColor(), 1f - (p - 0.25f) * 4f));
            sun.setColor(lerpColors(DayNightCycle.MIDNIGHT.getLightColor(), DayNightCycle.SUNRISE.getLightColor(), 1f - (p - 0.25f) * 4f));
        } else if (p < 0.25f && p >= 0f) {
            rayHandler.setAmbientLight(lerpColors(DayNightCycle.SUNRISE.getAmbientColor(), DayNightCycle.MIDDAY.getAmbientColor(), 1f - p * 4f));
            sun.setColor(lerpColors(DayNightCycle.SUNRISE.getLightColor(), DayNightCycle.MIDDAY.getLightColor(), 1f - p * 4f));
        }
    }

    private Color lerpColors(Color color1, Color color2, float progress) {
        float r = MathUtils.lerp(color1.r, color2.r, progress);
        float g = MathUtils.lerp(color1.g, color2.g, progress);
        float b = MathUtils.lerp(color1.b, color2.b, progress);
        float a = MathUtils.lerp(color1.a, color2.a, progress);
        return new Color(r, g, b, a);
    }

    public Vector2 getSpawnpoint() {
        return new Vector2(Constants.MAX_X / 2f * 32f, (Constants.MIN_Y + 100 + perlinNoise.getNoise(Constants.MAX_X / 2 - Constants.MIN_X, Constants.MAX_Y - Constants.MIN_Y - 50)) * 32f);
    }

    public World getWorld() {
        return world;
    }

    public WorldType getType() {
        return type;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    public Color getBackgroundColor() {
        return type.getBackgroundColor();
    }

    public int getTime() {
        return (int) ((1f - (this.sunDirection + 90f) / 360f + 0.5f) * 86400);
    }

    @Override
    public void dispose() {
        tiles = new Tile[1000][1000];
        rayHandler.dispose();
        world.dispose();
    }
}
