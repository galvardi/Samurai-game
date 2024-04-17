package pepse;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.items.Items;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Iterator;

/**
 * This class is responsible for creating and updating the world of the game
 */
public class WorldManager extends GameObject {
    public static final int CYCLE_LENGTH = 30;
    public static final int TREE_LAYER = Layer.STATIC_OBJECTS;
    public static final int SURFACE_LAYER = Layer.STATIC_OBJECTS;
    public static final int TERRAIN_LAYER = Layer.STATIC_OBJECTS + 1;
    public static final int SCREEN_PARTITIONS = 3;
    public static final int TWO = 2;
    public static final int SUN_R_COLOR = 255;
    public static final int SUN_G_COLOR = 255;
    public static final int SUN_B_COLOR = 0;
    public static final int SUN_A_COLOR = 20;
    private final int bufferSize;
    private final WindowController windowController;
    private final ImageReader imageReader;
    private Items items;
    private int leftBound;
    private int rightBound;
    private Terrain terrain;
    private Tree tree;
    private GameObjectCollection gameobjects;
    private int seed;
    private int windowXDim;
    private SoundReader soundReader;
    private GameObject sky;
    private GameObject night;

    private Camera camera;
    private GameObject sun;
    private GameObject halo;

    /**
     * Initialize all the different objects in the worl of the game
     *
     * @param gameobjects      the collection of game objects in the game
     * @param seed             to make the game random
     * @param windowController window controller
     * @param imageReader      image reader
     * @param soundReader      for items sound effects
     */
    public WorldManager(GameObjectCollection gameobjects, int seed, WindowController windowController,
                        ImageReader imageReader, SoundReader soundReader) {
        super(Vector2.ZERO, Vector2.ZERO, null);
        this.gameobjects = gameobjects;
        this.seed = seed;
        this.windowController = windowController;
        this.windowXDim = (int) windowController.getWindowDimensions().x();
        this.soundReader = soundReader;
        int roundedWinDim = windowXDim - windowXDim % Block.SIZE;
        this.bufferSize = (int) Math.floor((float) roundedWinDim / SCREEN_PARTITIONS);
        this.leftBound = bufferSize;
        this.rightBound = roundedWinDim - bufferSize;
        this.imageReader = imageReader;
        gameobjects.addGameObject(this);
        initWorld();
    }

    /**
     * sets the camera from outside the class
     *
     * @param camera to set
     */
    public void setCamera(Camera camera) {
        this.camera = camera;

    }

    /**
     * returns the terrain created in this class
     *
     * @return the terrain
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * given cord x, get the closest cord of a block
     *
     * @param x given cord
     * @return the closest cord
     */
    private int getClosestX(int x) {
        int res = x % Block.SIZE;
        if (res == 0)
            return x;
        else if (x < 0)
            return x - (Block.SIZE + res);
        return x - res;

    }

    /**
     * responsible for initializing the world
     */
    private void initWorld() {
        this.sky = Sky.create(gameobjects, windowController.getWindowDimensions(), Layer.BACKGROUND);
        gameobjects.layers().shouldLayersCollide(Layer.DEFAULT, SURFACE_LAYER, true);
        this.night = Night.create(gameobjects, Layer.FOREGROUND, windowController.getWindowDimensions(),
                CYCLE_LENGTH / 2);
        this.sun = Sun.create(gameobjects, Layer.BACKGROUND
                , windowController.getWindowDimensions(), CYCLE_LENGTH);
        this.halo = SunHalo.create(gameobjects, Layer.BACKGROUND, this.sun,
                new Color(SUN_R_COLOR, SUN_G_COLOR, SUN_B_COLOR, SUN_A_COLOR));
        this.terrain = new Terrain(gameobjects, TERRAIN_LAYER, windowController.getWindowDimensions(), seed);
        this.tree = new Tree(gameobjects, TREE_LAYER, windowController.getWindowDimensions(), seed, terrain);
        this.items = new Items(gameobjects, seed, Layer.DEFAULT, terrain, imageReader, soundReader,
                windowController.getWindowDimensions());
        createInitialRange();

    }

    /**
     * creates left buffer screen dim (3 buffers) and right buffer
     */
    private void createInitialRange() {
        for (int i = 0 - bufferSize; i <= windowXDim; i += bufferSize) {
            int x = getClosestX(i);
            int y = getClosestX(i + bufferSize);
            terrain.createInRange(x, y);
            tree.createInRange(x, y);
            this.items.createInRange(x, y);
        }
    }

    /**
     * create a new part of the world in the wanted coords
     *
     * @param right if true: add to the right of the rightest section,
     *              if flase: add to the left of the leftest section
     */
    private void addBuffer(Boolean right) {
        if (!right) {

            terrain.createInRange(leftBound - bufferSize * SCREEN_PARTITIONS, leftBound - bufferSize * TWO);
            tree.createInRange(leftBound - bufferSize * SCREEN_PARTITIONS, leftBound - bufferSize * TWO);
            items.createInRange(leftBound - bufferSize * SCREEN_PARTITIONS, leftBound - bufferSize * TWO);
            removeBuffer(right);
            leftBound -= bufferSize;
            rightBound -= bufferSize;
        } else {
            terrain.createInRange(rightBound + bufferSize * TWO, rightBound + bufferSize * SCREEN_PARTITIONS);
            tree.createInRange(rightBound + bufferSize * TWO, rightBound + bufferSize * SCREEN_PARTITIONS);
            items.createInRange(rightBound + bufferSize * TWO, rightBound + bufferSize * SCREEN_PARTITIONS);
            removeBuffer(right);
            rightBound += bufferSize;
            leftBound += bufferSize;
        }

    }

    /**
     * remove an existing part of the world in the wanted coords
     *
     * @param right if true: remove the rightest section,
     *              if flase: remove the leftest section
     */
    private void removeBuffer(Boolean right) {
        Iterator<GameObject> gameobjectsIter = gameobjects.iterator();
        if (!right) {
            for (Iterator<GameObject> it = gameobjectsIter; it.hasNext(); ) {
                GameObject obj = it.next();
                if (obj.getTopLeftCorner().x() > rightBound + bufferSize) {
                    gameobjects.removeGameObject(obj);
                }
            }
        } else {
            for (Iterator<GameObject> it = gameobjectsIter; it.hasNext(); ) {
                GameObject obj = it.next();
                if (obj.getTopLeftCorner().x() < leftBound - bufferSize) {
                    gameobjects.removeGameObject(obj);
                }
            }
        }
    }

    /**
     * check if we need to add or remove part of the world
     *
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.setCenter(camera.getCenter());
        int camX = (int) camera.getCenter().x();
        if (camX < leftBound) {
            addBuffer(false);
        } else if (camX > rightBound) {
            addBuffer(true);
        }
    }


}
