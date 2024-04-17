package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Create the Sun in the game
 */
public class Sun {

    public static final int SUN_DIM = 150;
    public static final String SUN = "sun";
    public static final float ZERO_ANGLE = 0f;
    public static final int TWO = 2;
    public static final float HALF = 0.5f;
    public static final float A_MULT = 0.8f;
    public static final float B_MULT = 1.2f;
    public static final int B_Y_CORDS = 120;

    /**
     * Static function that creates a gameObject of the Sun
     * @param gameObjects game objects
     * @param layer the layer of the Sun
     * @param windowDimensions  dimension of the window
     * @param cycleLength the length of the cycle of the sun
     * @return the new sun gameObject
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        Vector2 centerCoords = windowDimensions.mult(HALF);
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(SUN_DIM,
                SUN_DIM), new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN);
        gameObjects.addGameObject(sun, layer);
        float a = centerCoords.x() * A_MULT;
        float b = (centerCoords.y() - B_Y_CORDS) * B_MULT;
        new Transition<Float>(sun,
            (theta) -> sun.setCenter(new Vector2((float) (Math.sin(theta) * a + centerCoords.x()),
                    (float) (centerCoords.y() - (Math.cos(theta) * b)))),
                (float) (TWO *Math.PI),ZERO_ANGLE,Transition.LINEAR_INTERPOLATOR_FLOAT
            ,cycleLength,Transition.TransitionType.TRANSITION_LOOP,null);
        return sun;
    }
}
