package org.example.eiscuno.model.Animations;

import javafx.animation.Animation;
import javafx.scene.Node;

public interface AnimationAdapter {

        Animation fadeIn(Node node, double duration);
        Animation fadeOut(Node node, double duration);
        Animation scale(Node node, double duration, double fromScale, double toScale);

}
