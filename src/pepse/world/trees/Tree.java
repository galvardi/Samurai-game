package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * This class randomly generates trees
 */
public class Tree{
    private static final int MAX_TREES = 3;
    private static final float TREE_IN_COL_PROB = 0.1f;
    private static final Color BASE_TRUNK_COL = new Color(100, 50, 20);
    private static final int[] NUM_LEAVE_LIST = new int[]{5, 7};
    public static final int LEAF_LAYER = Layer.DEFAULT;
    public static final String TRUNK_TAG = "trunk";
    public static final float LOW_PATTER_P = 0.5f;
    public static final float HIGH_PATTERN_P = 0.9f;
    public static final int HIGHT_TREE_P = 12;
    public static final int LOW_TREE_P = 7;
    public static final int FIVE = 5;
    public static final int HALF = 2;
    private final Random rand;
    private final HashMap<Integer, ArrayList<ArrayList<Integer>>> templateLeaves;
    private GameObjectCollection gameObjects;
    private int treeLayer;
    private Vector2 windowDimensions;
    private int seed;
    private Terrain terrain;

    /**
     * constructor
     * @param gameObjects the games object
     * @param treeLayer the layer to add new tree to
     * @param windowDimensions the window dimentions
     * @param seed the seed to random
     * @param terrain the terrain of the game
     */
    public Tree(GameObjectCollection gameObjects,
                int treeLayer, Vector2 windowDimensions,
                int seed, Terrain terrain) {
        this.gameObjects = gameObjects;
        this.treeLayer = treeLayer;
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        this.terrain = terrain;
        this.rand = new Random(seed);
        this.templateLeaves = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
        createLeavesTemplates();
    }

    /**
     * create random patterns of the leaves for each size in NUM_LEAVE_LIST
     */
    private void createLeavesTemplates() {
        for (int numLeaves : NUM_LEAVE_LIST) {
            ArrayList<ArrayList<Integer>> rows = new ArrayList<>();
            float p = LOW_PATTER_P + rand.nextFloat() * (HIGH_PATTERN_P - LOW_PATTER_P);
            for (int i = 0; i < numLeaves; i++) {
                ArrayList<Integer> row = new ArrayList<>();
                for (int j = 0; j < numLeaves; j++) {
                    if (rand.nextFloat() < p) row.add(j);
                }
                rows.add(row);
            }
            templateLeaves.put(numLeaves, rows);
        }
    }

    /**
     * create a trunk for new tree by Block gameObjects
     * @param treeSize the size of Blocks
     * @param curX the location to set the Blocks to
     * @return the tree top cord
     */
    private Vector2 createTrunk(int treeSize, int curX) {

        int curY = (int) (Math.floor(terrain.groundHeightAt((float) curX))/Block.SIZE) * Block.SIZE;
        Vector2 treeCord = new Vector2(curX, curY);
        for (int i = 0; i < treeSize; i++) {
            treeCord = treeCord.add(Vector2.UP.mult(Block.SIZE));
            Block block = new Block(treeCord,
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_TRUNK_COL)));
            gameObjects.addGameObject(block, treeLayer);
            block.setTag(TRUNK_TAG);
        }
        return treeCord;

    }

    /**
     * In a certain range, generate randomly trees
     * @param minX left boundary of the range
     * @param maxX right boundary of the range
     */
    public void createInRange(int minX, int maxX) {
        int treeCounter = 0;
        int curX = minX;
        rand.setSeed(Objects.hash(minX, seed));
        Boolean prevPlanted = false;
        int middleLeft = (int) windowDimensions.x()/ HALF - FIVE *Block.SIZE;
        int middleRight = (int) windowDimensions.x()/HALF + FIVE*Block.SIZE;
        for (int i = 0; i < (maxX - minX); i+= Block.SIZE) {
            curX = minX + i;
            if ((i < middleLeft  || i > middleRight )&& !prevPlanted &&
                    rand.nextFloat() < TREE_IN_COL_PROB && treeCounter++ < MAX_TREES) {
                prevPlanted = true;
                int treeSize = rand.nextInt(HIGHT_TREE_P - LOW_TREE_P + 1) + LOW_TREE_P;
                Vector2 treeCord = createTrunk(treeSize, curX);
                createLeaves(treeCord.add(Vector2.UP.mult(Block.SIZE)));
            }
            else prevPlanted = false;
        }
    }
//
    /**
     * randomly choose the num of leaves for a new tree and creates them
     * @param treeCord the top of the tree cord
     */
    private void createLeaves(Vector2 treeCord) {
        int numLeaves = NUM_LEAVE_LIST[rand.nextInt(NUM_LEAVE_LIST.length)];
        int dif = (numLeaves - 1) / HALF;
        treeCord = treeCord.add(Vector2.ONES.mult(-dif * Leaf.SIZE));
        float leftLeafX = treeCord.x();
        int i = 0;
        for (ArrayList<Integer> row : templateLeaves.get(numLeaves)) {
            for (Integer leafCord : row) {
                Leaf leaf = new Leaf(treeCord.add(Vector2.RIGHT.mult(leafCord * Leaf.SIZE)),
                        Objects.hash(treeCord.x(), seed,i++));
                gameObjects.addGameObject(leaf, LEAF_LAYER);
                leaf.wind(rand.nextFloat());

            }
            treeCord = new Vector2(leftLeafX, treeCord.y() + Leaf.SIZE);
        }

    }
}
