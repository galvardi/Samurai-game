package pepse.ui;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * this class represents a numeric counter for displaying the score on the top left of the screen
 */
public class NumericScoreCounter extends GameObject {

    private final String scoreText;
    private final TextRenderable textRenderable;

    private final Counter scoreCounter;

    /**
     * Construct a new numeric counter
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public NumericScoreCounter(Counter scoreCounter, Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions, null);
        this.scoreCounter = scoreCounter;
        this.scoreText = "Score: %d";
        this.textRenderable = new TextRenderable(String.format(scoreText, scoreCounter.value()));
        this.renderer().setRenderable(textRenderable);
    }

    /**
     * updates the score with the counter value
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
        textRenderable.setString(String.format(scoreText, scoreCounter.value()));
    }
}
