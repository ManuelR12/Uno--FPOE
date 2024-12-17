package org.example.eiscuno.model.Animations;

import javafx.animation.Animation;
import javafx.scene.Node;

/**
 * Defines a contract for animation effects applied to JavaFX nodes.
 *
 * <p>This interface provides methods for common animations such as fade-in,
 * fade-out, and scaling effects.
 */
public interface AnimationAdapter {

        /**
         * Creates a fade-in animation for the specified node.
         *
         * @param node     the target node for the animation
         * @param duration the duration of the animation in seconds
         * @return the configured fade-in animation
         */
        Animation fadeIn(Node node, double duration);

        /**
         * Creates a fade-out animation for the specified node.
         *
         * @param node     the target node for the animation
         * @param duration the duration of the animation in seconds
         * @return the configured fade-out animation
         */
        Animation fadeOut(Node node, double duration);

        /**
         * Creates a scaling animation for the specified node.
         *
         * @param node       the target node for the animation
         * @param duration   the duration of the animation in seconds
         * @param fromScale  the initial scale factor
         * @param toScale    the final scale factor
         * @return the configured scaling animation
         */
        Animation scale(Node node, double duration, double fromScale, double toScale);
}
