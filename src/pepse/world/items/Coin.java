package pepse.world.items;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * this class represents a coin the player can get in the world to raise his score
 */
public class Coin extends GameObject {

    public static final String AVATAR_TAG = "avatar";
    public static final int SCORE_INCREMENT = 10;
    private final GameObjectCollection gameObjects;
    private int coinLayer;
    private Sound coinSound;
    private Counter scoreCounter;


    /**
     * constructs an instance of a coin
     * @param topLeftCorner coin init coords
     * @param dimensions coin dimensions
     * @param renderable spinning coin animation renderable
     * @param scoreCounter to update counter on collision
     * @param gameObjects to remove coin on collision
     * @param coinLayer layer to add coin to
     * @param coinSound sound on collision
     */
    public Coin(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Counter scoreCounter,
                GameObjectCollection gameObjects, int coinLayer, Sound coinSound) {
        super(topLeftCorner, dimensions, renderable);
        this.scoreCounter = scoreCounter;
        this.gameObjects = gameObjects;
        this.coinLayer = coinLayer;
        this.coinSound = coinSound;
    }

    /**
     * if collision is with avatar increases score by ten ,plays sound and removes coin
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(AVATAR_TAG)) {
            scoreCounter.increaseBy(SCORE_INCREMENT);
            gameObjects.removeGameObject(this, coinLayer);
            coinSound.play();
        }
    }
}
