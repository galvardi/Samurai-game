package pepse.world.items;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.ui.NumericScoreCounter;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.Objects;
import java.util.Random;

/**
 * this class is responsible for creating items player can interact with as well as placing them in the world
 */
public class Items {


    public static final int SCORE_Y_DIM = 30;
    public static final Vector2 FIRE_DIM = new Vector2(SCORE_Y_DIM, 60);
    public static final Vector2 COIN_DIM = new Vector2(SCORE_Y_DIM, 35);
    public static final String SCORE_TAG = "score";
    public static final String FIRE_DAMAGE_WAV = "assets\\fire damage.wav";
    public static final String COIN_SOUND_WAV = "assets\\coin sound.wav";
    public static final int SCORE_X_DIM = 80;
    public static final double SPIN_TIME_BETWEEN_CLIPS = 0.8d;
    public static final double FIRE_TIME_BETWEEN_CLIPS = 0.9d;
    public static final float FIRE_LOWER_P = 0.01f;
    public static final float FIRE_UPPER_P = 0.1f;
    public static final float COIN_LOWER_P = 0.1f;
    public static final float COIN_UPPER_P = 0.3f;
    public static final int PLAYER_RANGE_LEFT = 2;
    public static final int PLAYER_RANGE_RIGHT = 5;
    public static final int COIN_START_Y = 200;
    public static final int COIN_Y_CHANGE = 400;
    public static final int SCORE_COORD = 20;
    private final Random rand;
    private final Sound damageSound;
    private final Sound coinSound;
    private SoundReader soundReader;
    private Vector2 windowDims;
    private final Counter scoreCounter;
    private final NumericScoreCounter numericScoreCounter;
    private final ImageReader imageReader;
    private final AnimationRenderable spinAnimation;
    private final AnimationRenderable fireAnimation;

    private GameObjectCollection gameObjects;
    private static final String[] FIRE_ANIMATION = new String[]{"assets\\fire\\fire1.png", "assets\\" +
            "fire\\fire2.png", "assets\\fire\\fire3.png", "assets\\fire\\fire4.png", "assets\\" +
            "fire\\fire5.png"};
    public static final String[] SPIN_ANIMATIONS = new String[]{"coins\\coin_01.png", "assets" +
            "\\coins\\coin_02.png", "assets\\coins\\coin_03.png", "assets\\coins\\coin_04.png",
            "assets\\coins\\coin_05.png", "assets\\coins\\coin_06.png", "assets\\coins\\coin_07" +
            ".png", "assets\\coins\\coin_08.png"};
    private int seed;
    private int itemsLayer;
    private Terrain terrain;

    /**
     * @param gameObjects for adding items to gameobjectscollection
     * @param seed        random init
     * @param itemsLayer  layer to add items to
     * @param terrain     terrain for getting ground height
     * @param imageReader for loading item's animation images
     * @param soundReader for loading item's sounds
     * @param windowDims  for object placement
     */
    public Items(GameObjectCollection gameObjects, int seed, int itemsLayer, Terrain terrain,

                 ImageReader imageReader, SoundReader soundReader, Vector2 windowDims) {

        this.gameObjects = gameObjects;
        this.seed = seed;
        this.itemsLayer = itemsLayer;
        this.terrain = terrain;
        this.rand = new Random(seed);
        this.soundReader = soundReader;
        this.windowDims = windowDims;
        this.scoreCounter = new Counter();
        this.imageReader = imageReader;
        this.numericScoreCounter = new NumericScoreCounter(scoreCounter,
                new Vector2(SCORE_COORD, SCORE_COORD),
                new Vector2(SCORE_X_DIM, SCORE_Y_DIM));
        numericScoreCounter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(numericScoreCounter, Layer.UI);
        numericScoreCounter.setTag(SCORE_TAG);
        this.spinAnimation = new AnimationRenderable(SPIN_ANIMATIONS, imageReader,
                true, SPIN_TIME_BETWEEN_CLIPS);
        this.fireAnimation = new AnimationRenderable(FIRE_ANIMATION, imageReader,
                true, FIRE_TIME_BETWEEN_CLIPS);
        this.damageSound = soundReader.readSound(FIRE_DAMAGE_WAV);
        this.coinSound = soundReader.readSound(COIN_SOUND_WAV);
    }


    /**
     * creates and randomly places coins and firePits in the given range
     * @param minX stating x coord
     * @param maxX final x coord
     */
    public void createInRange(int minX, int maxX) {
        rand.setSeed(Objects.hash(minX, seed));
        int curX = minX;
        int curY;
        Boolean prevCoinPlanted = false;
        Boolean prevFirePlanted = false;
        while (curX <= maxX) {
            float pFire = FIRE_LOWER_P + rand.nextFloat() * (FIRE_UPPER_P - FIRE_LOWER_P);
            float pCoin = COIN_LOWER_P + rand.nextFloat() * (COIN_UPPER_P - COIN_LOWER_P);
            //make sure item isn't placed on player
            int middleLeft = (int) windowDims.x() / PLAYER_RANGE_LEFT - PLAYER_RANGE_RIGHT * Block.SIZE;
            int middleRight = (int) windowDims.x() / PLAYER_RANGE_LEFT + PLAYER_RANGE_RIGHT * Block.SIZE;

            float random = rand.nextFloat();
            if (random < pFire) {
                if (!prevFirePlanted && curX < middleLeft || curX > middleRight) {
                    curY = (int) (Math.floor(terrain.groundHeightAt((float) curX)) / Block.SIZE) *
                            Block.SIZE - (PLAYER_RANGE_LEFT * Block.SIZE);
                    Fire fire = new Fire(new Vector2(curX, curY), FIRE_DIM, fireAnimation,
                            scoreCounter, damageSound);
                    gameObjects.addGameObject(fire, itemsLayer);
                    prevFirePlanted = true;
                    prevCoinPlanted = false;
                }
            } else if (!prevCoinPlanted && random < pCoin) {
                Coin coin = new Coin(new Vector2(curX, COIN_START_Y - random * COIN_Y_CHANGE), COIN_DIM,
                        spinAnimation, scoreCounter,
                        gameObjects, itemsLayer, coinSound);
                gameObjects.addGameObject(coin, itemsLayer);
                prevCoinPlanted = true;
                prevFirePlanted = false;

            }
            curX += Block.SIZE;
        }
    }


}
