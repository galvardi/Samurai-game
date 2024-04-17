package pepse;

import danogl.GameManager;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;

/**
 * Runs the Pepse Game and responsible for the initialization
 * extends GameManager and manage the Pepse game
 */
public class PepseGameManager extends GameManager {


    private static final int SEED = 1;
    public static final int AVATAR_LAYER = Layer.DEFAULT;
    public static final int TARGET_FRAMERATE = 60;
    public static final String WORLD_TAG = "world";
    public static final int PLAYER_START_MULT = 10;
    public static final int NUM_TWO = 2;
    public static final int CAM_START_MULT = 6;
    public static final float HALD_FLOAT = 0.5f;
    private final String THEME_PATH = "assets\\RUDE - Eternal Youth.wav";
    private WorldManager worldManager;
    private Avatar avatar;
    private WindowController windowController;
    private ImageReader imageReader;
    private UserInputListener inputListener;


    /**
     * calls super ,initializes the worldManger, avatar sets up the camera to follow avatar
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.worldManager = new WorldManager(gameObjects(), SEED, windowController, imageReader, soundReader);
        worldManager.setTag(WORLD_TAG);
        gameObjects().addGameObject(worldManager);
        createAvatar();
        creatCamera();
        windowController.setTargetFramerate(TARGET_FRAMERATE);
        Sound sound = soundReader.readSound(THEME_PATH);
        sound.playLooped();
    }

    /**
     * function creates avatar and initializes its coordinates
     */
    private void createAvatar() {
        float middleScreenX = windowController.getWindowDimensions().x() / NUM_TWO;
        int avatarY =
                (int) (Math.floor(worldManager.getTerrain().groundHeightAt(middleScreenX)) / Block.SIZE) * Block.SIZE;
        Vector2 avatarInitalCoords = new Vector2(middleScreenX,
                avatarY - (PLAYER_START_MULT * Block.SIZE));
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, avatarInitalCoords, inputListener,
                imageReader);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * constructs a new camera that follows the avatar and sets it as the current camera in the world
     */
    private void creatCamera() {
        Vector2 followCoords = avatar.getTopLeftCorner().add(Vector2.DOWN.mult((CAM_START_MULT * Block.SIZE)));
        Camera camera = new Camera(avatar,
                windowController.getWindowDimensions().mult(HALD_FLOAT).subtract(followCoords),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());
        setCamera(camera);
        worldManager.setCamera(camera);
    }

    /**
     * calls PepseGameManager run function to start game
     *
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

}
