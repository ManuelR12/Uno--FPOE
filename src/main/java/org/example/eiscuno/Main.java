package org.example.eiscuno;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.eiscuno.view.GameUnoStage;

import java.io.IOException;

/**
 * Main class for the EISC Uno application.
 *
 * <p>Initializes and launches the JavaFX application.
 *
 * @author Juan Pablo Charry Ramirez
 * @author Juan Manuel Rodriguez
 * @author Stefania Bolaños Perdomo
 */
public class Main extends Application {

    /**
     * Entry point of the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the primary stage of the application.
     *
     * @param primaryStage the main stage
     * @throws IOException if an error occurs during initialization
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        GameUnoStage.getInstance();
    }
}
