package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.player.Player;

import java.util.Random;

/**
 * Monitors the machine player's hand in the Uno game.
 *
 * <p>This thread continuously checks the number of cards the machine player has,
 * and triggers actions when the machine has one card left and needs to say "UNO".
 */
public class ThreadMonitorMachine extends Thread {
    private Player machinePlayer;
    private volatile boolean isGameActive;
    public boolean didMachineSayUno;
    private Runnable onMachineHasOneCardCallback;  // Callback when the machine has one card
    private Runnable onMachineSaidUnoCallback;     // Callback when the machine says "UNO"

    /**
     * Constructs a ThreadMonitorMachine instance.
     *
     * @param machinePlayer the machine player whose hand is being monitored
     */
    public ThreadMonitorMachine(Player machinePlayer) {
        this.machinePlayer = machinePlayer;
        this.isGameActive = true;
        this.didMachineSayUno = false;
    }

    /**
     * Sets the callback to be triggered when the machine has one card left.
     *
     * @param onMachineHasOneCardCallback the callback to execute
     */
    public void setOnMachineHasOneCardCallback(Runnable onMachineHasOneCardCallback) {
        this.onMachineHasOneCardCallback = onMachineHasOneCardCallback;
    }

    /**
     * Sets the callback to be triggered when the machine says "UNO".
     *
     * @param onMachineSaidUnoCallback the callback to execute
     */
    public void setOnMachineSaidUnoCallback(Runnable onMachineSaidUnoCallback) {
        this.onMachineSaidUnoCallback = onMachineSaidUnoCallback;
    }

    /**
     * Continuously monitors the machine player's hand for game events.
     */
    @Override
    public void run() {
        while (isGameActive) {
            monitorHand();
            sleepForCpuEfficiency();
        }
    }

    /**
     * Checks if the machine has one card and triggers appropriate callbacks.
     */
    private void monitorHand() {
        if (machinePlayer.getCardsPlayer().size() == 1 && !didMachineSayUno) {
            if (onMachineHasOneCardCallback != null) {
                Platform.runLater(onMachineHasOneCardCallback);
            }
            waitToSayUno();
        } else if (machinePlayer.getCardsPlayer().size() > 1) {
            // Reset state if the machine has more than one card
            didMachineSayUno = false;
        }
    }

    /**
     * Simulates a delay before the machine says "UNO".
     */
    private void waitToSayUno() {
        Random random = new Random();
        int delay = 2000 + random.nextInt(2000); // Random delay between 2 and 4 seconds

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        didMachineSayUno = true;
        System.out.println("La máquina cantó UNO!");

        if (onMachineSaidUnoCallback != null) {
            Platform.runLater(onMachineSaidUnoCallback);
        }
    }

    /**
     * Stops monitoring the machine player's hand.
     */
    public void stopMonitoring() {
        isGameActive = false;
    }

    /**
     * Sleeps briefly to reduce CPU usage during monitoring.
     */
    private void sleepForCpuEfficiency() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
