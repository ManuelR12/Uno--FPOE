package org.example.eiscuno.model.Animations;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class GameStageAnimations implements AnimationAdapter{

        @Override
        public Animation fadeIn(Node node, double duration) {
            FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
            return fade;
        }

        @Override
        public Animation fadeOut(Node node, double duration) {
            FadeTransition fade = new FadeTransition(Duration.seconds(duration), node);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.play();
            return fade;
        }

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

