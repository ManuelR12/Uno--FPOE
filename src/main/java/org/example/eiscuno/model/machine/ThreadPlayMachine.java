package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.cardRules;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import java.util.ArrayList;
import java.util.Random;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private Player humanPlayer;
    private ImageView tableImageView;
    private GameUno gameUno;
    private volatile boolean hasPlayerPlayed;
    private volatile boolean isColorSelected;
    private Runnable onMachinePlayedCallback;
    private Runnable onCardEatenCallback;
    private Runnable onEatCardPlacedCallBack;
    private Runnable onColorChangedByMachineCallBack;
    ArrayList<String> colorsToPick = new ArrayList<>();
    private cardRules rules;


    public ThreadPlayMachine(Table table, Player machinePlayer, Player humanPlayer, ImageView tableImageView, GameUno gameUno, cardRules rules) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.humanPlayer = humanPlayer;
        this.gameUno = gameUno;
        this.hasPlayerPlayed = false;
        this.rules = rules;
        colorsToPick.add("RED");
        colorsToPick.add("BLUE");
        colorsToPick.add("YELLOW");
        colorsToPick.add("GREEN");
    }

    public void setOnMachinePlayedCallback(Runnable onMachinePlayedCallback) {
        this.onMachinePlayedCallback = onMachinePlayedCallback;
    }

    public void setOnCardEatenCallback(Runnable onCardEatenCallback) {
        this.onCardEatenCallback = onCardEatenCallback;
    }

    public void setOnEatCardPlacedCallBack(Runnable onEatCardPlacedCallBack){
        this.onEatCardPlacedCallBack = onEatCardPlacedCallBack;
    }

    public void setOnColorChangedByMachineCallBack(Runnable onColorChangedByMachineCallBack){
        this.onColorChangedByMachineCallBack = onColorChangedByMachineCallBack;
    }





    public void run() {
        while (true) {
            Card cardTable = table.getCurrentCardOnTheTable();
            // Esperar hasta que sea el turno de la máquina
            if (hasPlayerPlayed) {
                try {
                    // Simular un pequeño retraso en la jugada (por ejemplo, para mostrar animaciones)
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // Terminar el hilo si es interrumpido
                }

                // Verificar si la máquina tiene una carta "WILD"
                if (cardTable.getValue().equals("WILD_CARD") || cardTable.getColor().equals("WILD")){
                    // Esperar a que el color sea seleccionado
                    while (!isColorSelected) {
                        // No hacer nada, solo esperar
                        // Evitar que el hilo consuma CPU innecesariamente
                        try {
                            Thread.sleep(100); // Pausa para evitar que consuma demasiada CPU
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }

                // La máquina realiza su jugada
                putCardOnTheTable();

                // Indicar que la máquina ha terminado su turno
                hasPlayerPlayed = false;
            }

            // Evitar que el hilo consuma CPU innecesariamente
            try {
                Thread.sleep(100); // Breve espera para reducir el uso de CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public boolean getHasPlayerPlayed(){
        return hasPlayerPlayed;
    }

    private void putCardOnTheTable() {
        int index = findIndex();

        if (index == -1) {
            System.out.println("La máquina tiene que comer");
            gameUno.eatCard(this.machinePlayer, 1);
            index = findIndex();
            if (onCardEatenCallback != null) {
                Platform.runLater(onCardEatenCallback);
            }
        }

        if (index == -1) {
            System.out.println("La máquina pasa");
            return;
        }

        Card card = machinePlayer.getCard(index);
        rules.applySpecialCardEffect(card, humanPlayer);
        this.machinePlayer.removeCard(index);
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());

        // Manejar cartas especiales
        if (card.getColor().equals("WILD")) {
            Random random = new Random();
            int randomIndex = random.nextInt(colorsToPick.size());
            card.setColor(colorsToPick.get(randomIndex));
            System.out.println("Color changed to: " + card.getColor());
            if(onColorChangedByMachineCallBack != null){
                Platform.runLater(onColorChangedByMachineCallBack);
            }
            if (card.getValue().equals("+4")) {
                if (onEatCardPlacedCallBack != null) {
                    Platform.runLater(onEatCardPlacedCallBack);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                putCardOnTheTable();
            }else{
                if (onMachinePlayedCallback != null) {
                    Platform.runLater(onMachinePlayedCallback);
                }
                hasPlayerPlayed = false;

            }
        }


        if (card.getValue().equals("REVERSE") || card.getValue().equals("SKIP")||card.getValue().equals("+2")) {
            System.out.println("MachinePlayer played a special card!: " + card.getValue());
            // Permitir que la máquina vuelva a jugar
            if(card.getValue().equals("+2")) {
                if(onEatCardPlacedCallBack != null){
                    Platform.runLater(onEatCardPlacedCallBack);
                }
            }
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            putCardOnTheTable();
        } else {
            // Finalizar turno de la máquina
            if (onMachinePlayedCallback != null) {
                Platform.runLater(onMachinePlayedCallback);
            }
            hasPlayerPlayed = false;
        }
    }

    public int findIndex() {
        int index = -1;
        Card cardTable = table.getCurrentCardOnTheTable();

        for (int i = 0; i < this.machinePlayer.getCardsPlayer().size(); i++) {
            Card cardMachine = this.machinePlayer.getCardsPlayer().get(i);

            // Lógica modificada para tratar la carta "WILD"
            if (cardMachine.getColor().equals("WILD") || cardMachine.getColor().equals(cardTable.getColor()) || cardMachine.getValue().equals(cardTable.getValue())) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    public void setIsColorSelected(boolean isColorSelected) {
        this.isColorSelected = isColorSelected;
    }
}