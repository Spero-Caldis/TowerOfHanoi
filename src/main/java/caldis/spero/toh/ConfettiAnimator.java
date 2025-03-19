package caldis.spero.toh;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class ConfettiAnimator {
    public void animateConfetti(Pane confettiPane, Pane gamePane) {
        confettiPane.setVisible(true);
        Random random = new Random();
        Timeline timeline = new Timeline();

        for (int i = 0; i < 200; i++) { // Double the amount of confetti
            Rectangle confetti = new Rectangle(5, 10);
            confetti.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
            confetti.setX(random.nextInt((int) gamePane.getWidth()));
            confetti.setY(0);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(2 + random.nextDouble()), confetti);
            transition.setByY(gamePane.getHeight());
            transition.setCycleCount(1);
            transition.setOnFinished(event -> confettiPane.getChildren().remove(confetti)); // Remove confetti after animation

            // Use a linear interval for confetti generation
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 0.05), e -> {
                confettiPane.getChildren().add(confetti);
                transition.play();
            }));
        }

        timeline.play();
    }
}