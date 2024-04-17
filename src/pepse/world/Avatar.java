package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * this class represents the avatar the player controls in the game
 */
public class Avatar extends GameObject {

    public static final String[] STANDING_ANIMATIONS = new String[]{"assets\\sam\\idle_0.png",
            "assets\\sam\\idle_1.png", "assets\\sam\\idle_2.png", "assets\\sam\\idle_3.png"};
    public static final String[] WALKING_ANIMATIONS = new String[]{"assets\\sam\\run_0.png", "assets" +
            "\\sam\\run_1.png", "assets\\sam\\run_2.png", "assets\\sam\\run_3.png"};

    public static final String[] JUMPING_ANIMATIONS = new String[]{"assets\\sam\\jump_0.png",
            "assets\\sam\\jump_1.png"
            , "assets\\sam\\jump_2.png", "assets\\sam\\jump_3.png"};
    public static final String[] FLYING_ANIMATIONS = new String[]{"assets\\sam\\swim_0.png",
            "assets\\sam\\swim_1.png", "assets\\sam\\swim_2.png", "assets\\sam\\swim_3.png"};
    public static final float INIT_ENERGY = 100f;
    public static final int GRAVITY_ACL = 500;
    public static final double ENERGY_DIF = 0.5;
    public static final int X_VEL = 300;
    public static final int Y_VEL_JUMP = -300;
    public static final String TRUNK_TAG = "trunk";
    public static final String AVATAR_TAG = "avatar";
    public static final double ANIMATION_TIME_BETWEEN_CLIPS = 0.2d;
    private final AnimationRenderable standingAnimation;
    private final AnimationRenderable walkingAnimation;
    private final AnimationRenderable jumpingAnimation;
    private final AnimationRenderable flyingAnimation;
    private GameObjectCollection gameObjects;
    private UserInputListener inputListener;
    private ImageReader imageReader;

    private float energy;
    private static Vector2 DIM = new Vector2(60, 60);

    /**
     * constructs an instance of avatar game object
     *
     * @param topLeftCorner avatar init coords
     * @param dimensions    avatar dimensions
     * @param renderable    avatar initial animation renderable
     * @param inputListener to allow the player to the avatars movement using the keys
     * @param imageReader   to allow avatar to load animation renderables
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable
            , UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        energy = INIT_ENERGY;
        this.standingAnimation = new AnimationRenderable(STANDING_ANIMATIONS, imageReader
                , true, ANIMATION_TIME_BETWEEN_CLIPS);
        this.walkingAnimation = new AnimationRenderable(WALKING_ANIMATIONS, imageReader
                , true, ANIMATION_TIME_BETWEEN_CLIPS);
        this.jumpingAnimation = new AnimationRenderable(JUMPING_ANIMATIONS, imageReader
                , true, ANIMATION_TIME_BETWEEN_CLIPS);
        this.flyingAnimation = new AnimationRenderable(FLYING_ANIMATIONS, imageReader
                , true, ANIMATION_TIME_BETWEEN_CLIPS);
        this.transform().setAccelerationY(GRAVITY_ACL); // gravity
    }

    /**
     * updates the avatars movement at each frame according to key input
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
        walking();
        jumpingFlying();
        if (this.getVelocity().x() == 0) {
            this.renderer().setRenderable(this.standingAnimation);
        }

    }

    /**
     * handles avatars  y-axis movement and animations i.e. jumping and flying
     */
    private void jumpingFlying() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.getVelocity().y() == 0) {
            this.transform().setVelocityY(Y_VEL_JUMP);
            this.renderer().setRenderable(this.jumpingAnimation);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0) {
            this.transform().setVelocityY(Y_VEL_JUMP);
            energy -= ENERGY_DIF;
            this.renderer().setRenderable(this.flyingAnimation);
        }
    }

    /**
     * handles the avatars x-axis movement and animations
     */
    private void walking() {
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            this.transform().setVelocityX(-X_VEL);
            this.renderer().setRenderable(this.walkingAnimation);
            this.renderer().setIsFlippedHorizontally(true);
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.transform().setVelocityX(X_VEL);
            this.renderer().setRenderable(this.walkingAnimation);
            this.renderer().setIsFlippedHorizontally(false);
        } else {
            this.transform().setVelocityX(0);
        }
        if (this.getVelocity().y() == 0 && energy < INIT_ENERGY) {
            energy += ENERGY_DIF;
        }
    }

    /**
     * creates an instance of avatar
     * @param gameObjects to add avatar to game objects collection
     * @param layer layer to add avatar to
     * @param topLeftCorner avatar init coords
     * @param inputListener to allow keyboard control of avatar movement
     * @param imageReader to allow loading of animation renderables
     * @return and avatar instance
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Avatar avatar = new Avatar(topLeftCorner, DIM, null, inputListener, imageReader);
        avatar.renderer().setRenderable(avatar.standingAnimation);
        avatar.setTag(AVATAR_TAG);
        gameObjects.addGameObject(avatar, layer);
        return avatar;

    }

    /**
     * on collision with terrain sets avatar's y velocity to 0
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        if (other instanceof Block && !(other.getTag().equals(TRUNK_TAG))) {// no collision with tree
            this.transform().setVelocityY(0);
        }
    }
}
