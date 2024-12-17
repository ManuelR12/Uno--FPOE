package org.example.eiscuno.model.Animations;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Implements the {@link AnimationAdapter} interface to provide common animations
 * such as fade-in, fade-out, and scaling effects for JavaFX nodes.
 */
public class GameStageAnimations implements AnimationAdapter {

    /**
     * Creates and plays a fade-in animation for the specified node.
     *
     * @param node     the target node for the animation
     * @param duration the duration of the animation in seconds
     * @return the configured fade-in animation
     */
    @Override
    public Animation fadeIn(Node node, double duration) {
        FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
        return fade;
    }

    /**
     * Creates and plays a fade-out animation for the specified node.
     *
     * @param node     the target node for the animation
     * @param duration the duration of the animation in seconds
     * @return the configured fade-out animation
     */
    @Override
    public Animation fadeOut(Node node, double duration) {
        FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
        return fade;
    }

    /**
     * Creates and plays a scaling animation for the specified node.
     *
     * @param node       the target node for the animation
     * @param duration   the duration of the animation in seconds
     * @param fromScale  the initial scale factor
     * @param toScale    the final scale factor
     * @return the configured scaling animation
     */
    @Override
    public Animation scale(Node node, double duration, double fromScale, double toScale) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(duration), node);
        scale.setFromX(fromScale);
        scale.setFromY(fromScale);
        scale.setToX(toScale);
        scale.setToY(toScale);
        scale.play();
        return scale;
    }
}
