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
    private Runnable onEatCardPlacedCallback;
    private Runnable onColorChangedByMachineCallback;
    private ArrayList<String> colorsToPick = new ArrayList<>();
    private cardRules rules;
    private boolean isGameActive = true;

    public ThreadPlayMachine(Table table, Player machinePlayer, Player humanPlayer, ImageView tableImageView, GameUno gameUno, cardRules rules) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.humanPlayer = humanPlayer;
        this.tableImageView = tableImageView;
        this.gameUno = gameUno;
        this.rules = rules;
        initializeColorsToPick();
    }

    private void initializeColorsToPick() {
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

    public void setOnEatCardPlacedCallback(Runnable onEatCardPlacedCallback) {
        this.onEatCardPlacedCallback = onEatCardPlacedCallback;
    }

    public void setOnColorChangedByMachineCallback(Runnable onColorChangedByMachineCallback) {
        this.onColorChangedByMachineCallback = onColorChangedByMachineCallback;
    }

    @Override
    public void run() {
        while (isGameActive) {
            if (hasPlayerPlayed) {
                simulateMachineTurn();
            }

            sleepForCpuEfficiency();
        }
    }

    public void stopMachinePlay(){
        isGameActive = false;
    }

    private void simulateMachineTurn() {
        try {
            Thread.sleep(2000); // Simulate delay for animations
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        if (isWildCardOnTable()) {
            waitForColorSelection();
        }

        processCardPlay();
    }

    private boolean isWildCardOnTable() {
        Card cardTable = table.getCurrentCardOnTheTable();
        return cardTable.getValue().equals("WILD_CARD") || cardTable.getColor().equals("WILD");
    }

    private void waitForColorSelection() {
        while (!isColorSelected) {
            sleepForCpuEfficiency();
        }
    }

    private void processCardPlay() {
        int cardIndex = findPlayableCardIndex();

        if (cardIndex == -1) {
            handleNoPlayableCard();
            return;
        }

        playCard(cardIndex);
    }

    private void handleNoPlayableCard() {
        System.out.println("La máquina tiene que comer");
        gameUno.eatCard(machinePlayer, 1);
        if (onCardEatenCallback != null) {
            Platform.runLater(onCardEatenCallback);
        }
        int index = findPlayableCardIndex();
        if (index == -1) {
            System.out.println("La máquina pasa");
            hasPlayerPlayed = false;
        }
    }

    private void playCard(int cardIndex) {
        Card card = machinePlayer.getCard(cardIndex);
        rules.applySpecialCardEffect(card, humanPlayer);

        machinePlayer.removeCard(cardIndex);
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());

        handleSpecialCardEffects(card);
    }

    private void handleSpecialCardEffects(Card card) {
        if (card.getColor().equals("WILD")) {
            handleWildCard(card);
        } else if (card.getValue().equals("REVERSE") || card.getValue().equals("SKIP") || card.getValue().equals("+2")) {
            handleSpecialActionCard(card);
        } else {
            completeTurn();
        }
    }

    private void handleWildCard(Card card) {
        Random random = new Random();
        int randomIndex = random.nextInt(colorsToPick.size());
        card.setColor(colorsToPick.get(randomIndex));

        System.out.println("Color changed to: " + card.getColor());
        if (onColorChangedByMachineCallback != null) {
            Platform.runLater(onColorChangedByMachineCallback);
        }

        if (card.getValue().equals("+4")) {
            if (onEatCardPlacedCallback != null) {
                Platform.runLater(onEatCardPlacedCallback);
            }
            sleepForEffectDelay();
            processCardPlay();
        } else {
            completeTurn();
        }
    }

    private void handleSpecialActionCard(Card card) {
        System.out.println("MachinePlayer played a special card!: " + card.getValue());
        if (card.getValue().equals("+2") && onEatCardPlacedCallback != null) {
            Platform.runLater(onEatCardPlacedCallback);
        } else if(card.getValue().equals("REVERSE") && onMachinePlayedCallback != null || card.getValue().equals("SKIP") && onMachinePlayedCallback != null) {
            Platform.runLater(onMachinePlayedCallback);
        }

        sleepForEffectDelay();
        processCardPlay();
    }

    private void completeTurn() {
        if (onMachinePlayedCallback != null) {
            Platform.runLater(onMachinePlayedCallback);
        }
        hasPlayerPlayed = false;
    }

    private void sleepForEffectDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleepForCpuEfficiency() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int findPlayableCardIndex() {
        Card cardTable = table.getCurrentCardOnTheTable();

        for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
            Card cardMachine = machinePlayer.getCardsPlayer().get(i);

            if (isCardPlayable(cardMachine, cardTable)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isCardPlayable(Card cardMachine, Card cardTable) {
        return cardMachine.getColor().equals("WILD") ||
                cardMachine.getColor().equals(cardTable.getColor()) ||
                cardMachine.getValue().equals(cardTable.getValue());
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    public boolean getHasPlayerPlayed() {
        return hasPlayerPlayed;
    }

    public void setIsColorSelected(boolean isColorSelected) {
        this.isColorSelected = isColorSelected;
    }
}
