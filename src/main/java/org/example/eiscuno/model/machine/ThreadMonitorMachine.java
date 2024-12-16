package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.player.Player;

import java.util.Random;

public class ThreadMonitorMachine extends Thread {
    private Player machinePlayer;
    private volatile boolean isGameActive;
    public boolean didMachineSayUno;
    private Runnable onMachineHasOneCardCallback;  // Callback cuando la máquina tiene una sola carta
    private Runnable onMachineSaidUnoCallback;     // Callback cuando la máquina canta "UNO"

    public ThreadMonitorMachine (Player machinePlayer) {
        this.machinePlayer = machinePlayer;
        this.isGameActive = true;
        this.didMachineSayUno = false;
    }

    public void setOnMachineHasOneCardCallback(Runnable onMachineHasOneCardCallback) {
        this.onMachineHasOneCardCallback = onMachineHasOneCardCallback;
    }

    public void setOnMachineSaidUnoCallback(Runnable onMachineSaidUnoCallback) {
        this.onMachineSaidUnoCallback = onMachineSaidUnoCallback;
    }

    @Override
    public void run() {
        while (isGameActive) {
            monitorHand();
            sleepForCpuEfficiency();
        }
    }

    private void monitorHand() {
        if (machinePlayer.getCardsPlayer().size() == 1 && !didMachineSayUno) {
            if (onMachineHasOneCardCallback != null) {
                Platform.runLater(onMachineHasOneCardCallback);
            }
            waitToSayUno();
        } else if (machinePlayer.getCardsPlayer().size() > 1) {
            // Reinicia el estado si la máquina tiene más de una carta
            didMachineSayUno = false;
        }
    }


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

    public void stopMonitoring() {
        isGameActive = false;
    }

    private void sleepForCpuEfficiency() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
