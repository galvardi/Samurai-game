package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Creates the sun halo of the game
 */
public class SunHalo {

    public static final float HALO_SIZE = 1.2f;
    public static final String HALO = "halo";

    /**
     * Static function that creates a gameObject of the halo
     * @param gameObjects game objects
     * @param layer the layer of the
     * @param sun the object to set the halo according to
     * @param color the color of the halo
     * @return the new halo gameObject
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    GameObject sun, Color color) {
        GameObject halo = new GameObject(Vector2.ZERO, sun.getDimensions().mult(HALO_SIZE),
                new OvalRenderable(color));
        gameObjects.addGameObject(halo, layer);
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        halo.setTag(HALO);
        halo.addComponent((t) -> halo.setCenter(sun.getCenter()));
        return halo;
    }
}
