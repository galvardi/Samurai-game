package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

/**
 * Create the ground layer
 */
public class Terrain {

    private static final String GROUND = "ground";
    public static final int SURFACE_LAYER = 1;
    public static final float GROUND_HEIGHT = 3F / 4;
    public static final int GROUND_MULT = 13;
    public static final int TOP_LAYERS = 2;
    private final int seed;
    private final NoiseGenerator perlinNoise;
    private final Random rand;
    private int groundHeightAtX0;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 25;
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private Vector2 windowDimensions;

    /**
     * constructor
     * @param gameObjects game objects
     * @param groundLayer the layer of the ground
     * @param windowDimensions the window dimensions
     * @param seed the random
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        this.rand = new Random(seed);
        this.groundHeightAtX0 = (int) (windowDimensions.y() * GROUND_HEIGHT);
        this.perlinNoise = new NoiseGenerator(seed);

    }

    /**
     * Uses the perlin Noise class to select the height of the ground at a certain point
     * @param x the point to get the height of
     * @return the height
     */
    public float groundHeightAt(float x) {
        x = (float) x / Block.SIZE;
        double i = perlinNoise.noise(x);
        return (float) (groundHeightAtX0 +( i * GROUND_MULT * Block.SIZE));
    }

    /**
     * build the ground according to the height from the perlin noise in a certain range
     * @param minX left bound it the range
     * @param maxX right bound it the range
     */
    public void createInRange(int minX, int maxX) {
        int curX = minX;
        int curY;
        int blockInt = 0;
        while (curX <= maxX) {
            curY = (int) (Math.floor(groundHeightAt((float) curX))/Block.SIZE) * Block.SIZE;
            blockInt = 0;
            while (blockInt < TERRAIN_DEPTH) {
                Block block = new Block(new Vector2(curX, curY),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                if (blockInt < TOP_LAYERS) {
                    gameObjects.addGameObject(block, groundLayer - SURFACE_LAYER);
                } else
                    gameObjects.addGameObject(block, groundLayer);
                blockInt++;
                block.setTag(GROUND);
                curY += Block.SIZE;
            }
            curX += Block.SIZE;
        }
    }
}