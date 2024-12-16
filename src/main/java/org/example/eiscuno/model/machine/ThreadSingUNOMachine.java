package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable {
    private ArrayList<Card> cardsPlayer;
    private Runnable onUnoSangCallBack;
    private boolean unoPending; // Bandera para evitar múltiples callbacks innecesarios
    private boolean isGameActive = true;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer) {
        this.cardsPlayer = cardsPlayer;
        this.unoPending = false; // Inicialmente no hay "UNO" pendiente
    }

    public void setOnUnoSang(Runnable onUnoSangCallBack) {
        this.onUnoSangCallBack = onUnoSangCallBack;
    }

    @Override
    public void run() {
        while (isGameActive) {
            hasOneCardTheHumanPlayer();

            try {
                // Pequeña pausa para evitar consumir demasiados recursos (1ms)
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMonitoringUnoSang(){
        isGameActive = false;
    }

    private void hasOneCardTheHumanPlayer() {
        if (cardsPlayer.size() == 1 && !unoPending) {
            // Marca como pendiente para evitar múltiples ejecuciones
            unoPending = true;

            // Ejecuta el callback después de 3 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Espera 3 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Ejecuta el callback en el hilo de la interfaz gráfica
                if (onUnoSangCallBack != null) {
                    Platform.runLater(onUnoSangCallBack);
                }

                // Permitir nuevos callbacks si el estado cambia
                unoPending = false;
            }).start();
        } else if (cardsPlayer.size() != 1) {
            // Si el jugador ya no tiene una sola carta, reinicia la bandera
            unoPending = false;
        }
    }
}