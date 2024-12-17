package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Monitors the machine player's hand and triggers a callback when the machine shouts "UNO".
 *
 * <p>This class continuously checks if the machine player has one card left and executes
 * a callback after a delay to simulate the machine shouting "UNO".
 */
public class ThreadSingUNOMachine implements Runnable {
    private ArrayList<Card> cardsPlayer;
    private Runnable onUnoSangCallBack;
    private boolean unoPending; // Flag to prevent multiple unnecessary callbacks
    private boolean isGameActive = true;

    /**
     * Constructs a ThreadSingUNOMachine instance to monitor the player's hand.
     *
     * @param cardsPlayer the list of cards in the machine player's hand
     */
    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer) {
        this.cardsPlayer = cardsPlayer;
        this.unoPending = false; // Initially, no "UNO" shout is pending
    }

    /**
     * Sets the callback to be executed when the machine shouts "UNO".
     *
     * @param onUnoSangCallBack the callback to execute when "UNO" is triggered
     */
    public void setOnUnoSang(Runnable onUnoSangCallBack) {
        this.onUnoSangCallBack = onUnoSangCallBack;
    }

    /**
     * Runs the monitoring loop to check for a single card condition.
     */
    @Override
    public void run() {
        while (isGameActive) {
            hasOneCardTheHumanPlayer();

            try {
                // Small pause to reduce resource consumption (1ms)
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops monitoring the machine player's hand.
     */
    public void stopMonitoringUnoSang(){
        isGameActive = false;
    }

    /**
     * Checks if the machine player has one card left and triggers the "UNO" callback.
     */
    private void hasOneCardTheHumanPlayer() {
        if (cardsPlayer.size() == 1 && !unoPending) {
            // Set flag to avoid multiple executions
            unoPending = true;

            // Execute the callback after a 3-second delay
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Wait for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Execute the callback on the UI thread
                if (onUnoSangCallBack != null) {
                    Platform.runLater(onUnoSangCallBack);
                }

                // Reset flag to allow future callbacks if the state changes
                unoPending = false;
            }).start();
        } else if (cardsPlayer.size() != 1) {
            // Reset the flag if the player no longer has one card
            unoPending = false;
        }
    }
}
