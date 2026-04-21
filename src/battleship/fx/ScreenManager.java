package battleship.fx;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ScreenManager {
    private final Map<String, Pane> screens = new HashMap<>();
    private final StackPane root = new StackPane();

    public StackPane getRoot() {
        return root;
    }

    public void addScreen(String name, Pane screen) {
        screens.put(name, screen);
    }

    public void show(String name) {
        Pane screen = screens.get(name);
        if (screen == null) {
            throw new IllegalArgumentException("Unknown screen: " + name);
        }

        if (root.getChildren().isEmpty()) {
            root.getChildren().add(screen);
        } else {
            Pane oldScreen = (Pane) root.getChildren().get(0);
            if (oldScreen == screen) return;

            // Simple transition: Fade out old, Fade in new
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldScreen);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), screen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), screen);
            slideIn.setFromY(20);
            slideIn.setToY(0);

            fadeOut.setOnFinished(event -> {
                root.getChildren().setAll(screen);
                ParallelTransition parallel = new ParallelTransition(fadeIn, slideIn);
                parallel.play();
            });
            fadeOut.play();
        }
    }
}
