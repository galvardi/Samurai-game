package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * Represent a leaf gameObject
 */
public class Leaf extends Block{

    private static final Color BASE_LEAF_COL = new Color(50, 200, 30);
    public static final float ROT_CYCLE_LEN = 3;
    public static final int UPPER_LIFE = 50;
    public static final int OPAQUENESS = 1;
    public static final int LOWER_LIFE = 1;
    public static final int FALL_VEL = 100;
    public static final float WITH_START = 30f;
    public static final float WITH_END = 20f;
    public static final float LEAF_ANGLE = 10f;
    public static final int VELOCITY_Y = 0;
    public static final String GROUND_TAG = "ground";
    public static final String TRUNK_TAG = "trunk";
    private Vector2 initalCoord;
    private final Random rand;
    private int lifeTime;
    private static float FADEOUT_TIME = 10f;


    /**
     * constructor
     * @param topLeftCorner the location of the leaf
     * @param seed the random
     */
    public Leaf(Vector2 topLeftCorner,int seed) {
        super(topLeftCorner,
                new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COL)));
        initalCoord = topLeftCorner;
        this.rand = new Random(seed);
        this.lifeTime = rand.nextInt(UPPER_LIFE - LOWER_LIFE +1)+ LOWER_LIFE;
        new ScheduledTask(this, lifeTime, false,this::fall);
    }

    /**
     * Creates the wind property for a leaf
     * @param delay the delay the property should start
     */
    public void wind(float delay) {
        new ScheduledTask(
                this, delay, true,
                () -> {new Transition<Float>( // leaf size
                            this, (x) -> this.setDimensions(new Vector2(x, x)), WITH_START,
                            WITH_END, Transition.LINEAR_INTERPOLATOR_FLOAT, ROT_CYCLE_LEN,
                            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
                    new Transition<Float>(this,
                            (x) -> this.renderer().setRenderableAngle(x), -LEAF_ANGLE, LEAF_ANGLE
                            , Transition.LINEAR_INTERPOLATOR_FLOAT, ROT_CYCLE_LEN
                            , Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
                }
        );
    }

    /**
     * the circle of life of the leaf, makes the leaf fall and then calls relive.
     */
    public void fall() {
        this.renderer().fadeOut(FADEOUT_TIME,this::reLive);
        this.transform().setVelocityY(FALL_VEL);
    }

    /**
     *make the leaf reaper in its original location
     */
    private void reLive(){
        int deathTime = rand.nextInt(UPPER_LIFE- LOWER_LIFE +1)+ LOWER_LIFE;
        new ScheduledTask(this, deathTime, false, () -> {
            this.renderer().setOpaqueness(OPAQUENESS);
            this.setTopLeftCorner(initalCoord);
            int lifeTime = rand.nextInt(UPPER_LIFE - LOWER_LIFE +1)+ LOWER_LIFE;
            new ScheduledTask(this, lifeTime, false,this::fall);
        });
    }

    /**
     * defines no collision with avatar
     * @param other The other GameObject.
     * @return supers return value unless and not ground or trunk
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (other.getTag().equals(GROUND_TAG) || other.getTag().equals(TRUNK_TAG)) && super.shouldCollideWith(other);
    }

    /**
     * if a leaf collides with the ground it will stop moving
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other instanceof Block) {
            this.transform().setVelocityY(VELOCITY_Y);
        }
    }

}


