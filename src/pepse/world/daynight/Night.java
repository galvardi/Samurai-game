package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
/**
 * Create the Night in the game
 */
public class Night {


    public static final String NIGHT = "night";
    private static final float DAY_OPAQ = 0f;
    private static final float MIDNIGHT_OPACITY = 0.5f;

    /**
     * Static function that creates a gameObject of the Night and a transition cycle
     * @param gameObjects game objects
     * @param layer the layer of the night
     * @param windowDimensions dimension of the window
     * @param cycleLength the length of the cycle
     * @return the new Night gameObject
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO,windowDimensions,new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.renderer().setOpaqueness(DAY_OPAQ);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT);
        new Transition<Float>(night, night.renderer()::setOpaqueness, DAY_OPAQ, MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength ,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;


    }

}
