package pepse.world;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Create the Sky in the game
 */
public class Sky {

    public static final String COLOR_SKY = "#80C6E5";
    private static final Color BASIC_SKY_COLOR = Color.decode(COLOR_SKY);
    public static final String SKY_TAG = "sky";

    /**
     * Static function that creates a gameObject of the sky
     * @param gameObjects game objects
     * @param windowDimensions dimension of the window
     * @param skyLayer the layer of the sky
     * @return the new sky gameObject
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer) {

        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions, new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
