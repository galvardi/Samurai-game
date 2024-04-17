package pepse.world.items;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;


/**
 * this class represents a coin the player can hit in the world lowers his score
 */
public class Fire extends GameObject {

    public static final String AVATAR_TAG = "avatar";
    public static final int SCORE_DIF = -15;
    private final Counter scoreCounter;
    private Sound damageSound;

    /**
     * constructs an instance of a fire
     *
     * @param topLeftCorner fire pit init coords
     * @param dimensions    fire pit dime
     * @param renderable    roaring fire animation renderable
     * @param scoreCounter  decrement score on collision
     * @param damageSound   sound effect played on collision
     */
    public Fire(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                Counter scoreCounter, Sound damageSound) {
        super(topLeftCorner, dimensions, renderable);
        this.scoreCounter = scoreCounter;
        this.damageSound = damageSound;
    }

    /**
     * on collision with player decreases players score and plays sound
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(AVATAR_TAG)) {
            scoreCounter.increaseBy(SCORE_DIF);
            damageSound.play();
        }
    }


}
