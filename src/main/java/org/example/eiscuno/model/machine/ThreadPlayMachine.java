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

/**
 * Represents the machine player's logic and actions during the Uno game.
 *
 * <p>This thread simulates the machine player's turn, including playing cards,
 * drawing cards when no valid plays are available, and handling special card effects.
 */
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

    /**
     * Constructs a ThreadPlayMachine instance to simulate the machine player's actions.
     *
     * @param table          the table where cards are placed
     * @param machinePlayer  the machine player
     * @param humanPlayer    the human player
     * @param tableImageView the ImageView representing the table's current card
     * @param gameUno        the Uno game logic instance
     * @param rules          the game rules manager
     */
    public ThreadPlayMachine(Table table, Player machinePlayer, Player humanPlayer, ImageView tableImageView, GameUno gameUno, cardRules rules) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.humanPlayer = humanPlayer;
        this.tableImageView = tableImageView;
        this.gameUno = gameUno;
        this.rules = rules;
        initializeColorsToPick();
    }

    /**
     * Initializes the list of colors the machine can choose for wild cards.
     */
    private void initializeColorsToPick() {
        colorsToPick.add("RED");
        colorsToPick.add("BLUE");
        colorsToPick.add("YELLOW");
        colorsToPick.add("GREEN");
    }

    /**
     * Sets a callback to execute when the machine finishes its turn.
     *
     * @param onMachinePlayedCallback the callback to execute
     */
    public void setOnMachinePlayedCallback(Runnable onMachinePlayedCallback) {
        this.onMachinePlayedCallback = onMachinePlayedCallback;
    }

    /**
     * Sets a callback to execute when the machine draws a card.
     *
     * @param onCardEatenCallback the callback to execute
     */
    public void setOnCardEatenCallback(Runnable onCardEatenCallback) {
        this.onCardEatenCallback = onCardEatenCallback;
    }

    /**
     * Sets a callback to execute when the machine places a draw card.
     *
     * @param onEatCardPlacedCallback the callback to execute
     */
    public void setOnEatCardPlacedCallback(Runnable onEatCardPlacedCallback) {
        this.onEatCardPlacedCallback = onEatCardPlacedCallback;
    }

    /**
     * Sets a callback to execute when the machine changes the card color.
     *
     * @param onColorChangedByMachineCallback the callback to execute
     */
    public void setOnColorChangedByMachineCallback(Runnable onColorChangedByMachineCallback) {
        this.onColorChangedByMachineCallback = onColorChangedByMachineCallback;
    }

    /**
     * Runs the machine player's game loop.
     *
     * <p>The loop waits for the player's turn, simulates the machine's actions,
     * and reduces CPU usage with efficient sleeping.
     */
    @Override
    public void run() {
        while (isGameActive) {
            if (hasPlayerPlayed) {
                simulateMachineTurn();
            }
            sleepForCpuEfficiency();
        }
    }

    /**
     * Stops the machine player's game loop.
     */
    public void stopMachinePlay(){
        isGameActive = false;
    }

    /**
     * Simulates the machine player's turn with delays and actions.
     */
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

    /**
     * Checks if the current card on the table is a wild card.
     *
     * @return {@code true} if the card is wild, otherwise {@code false}
     */
    private boolean isWildCardOnTable() {
        Card cardTable = table.getCurrentCardOnTheTable();
        return cardTable.getValue().equals("WILD_CARD") || cardTable.getColor().equals("WILD");
    }

    /**
     * Waits for the machine to select a color when a wild card is played.
     */
    private void waitForColorSelection() {
        while (!isColorSelected) {
            sleepForCpuEfficiency();
        }
    }

    /**
     * Processes the logic for the machine to play a card.
     */
    private void processCardPlay() {
        int cardIndex = findPlayableCardIndex();

        if (cardIndex == -1) {
            handleNoPlayableCard();
            return;
        }

        playCard(cardIndex);
    }

    /**
     * Handles the case where the machine has no playable cards.
     */
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

    /**
     * Plays a valid card from the machine's hand.
     *
     * @param cardIndex the index of the card to play
     */
    private void playCard(int cardIndex) {
        Card card = machinePlayer.getCard(cardIndex);
        rules.applySpecialCardEffect(card, humanPlayer);

        machinePlayer.removeCard(cardIndex);
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());

        handleSpecialCardEffects(card);
    }

    /**
     * Handles the effects of special cards like wild, reverse, and skip.
     *
     * @param card the card being played
     */
    private void handleSpecialCardEffects(Card card) {
        if (card.getColor().equals("WILD")) {
            handleWildCard(card);
        } else if (card.getValue().equals("REVERSE") || card.getValue().equals("SKIP") || card.getValue().equals("+2")) {
            handleSpecialActionCard(card);
        } else {
            completeTurn();
        }
    }

    /**
     * Handles the action of playing a wild card, including color selection and special effects.
     *
     * @param card the wild card played
     */
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

    /**
     * Handles special action cards such as REVERSE, SKIP, or +2.
     *
     * @param card the special action card played
     */
    private void handleSpecialActionCard(Card card) {
        System.out.println("MachinePlayer played a special card!: " + card.getValue());
        if (card.getValue().equals("+2") && onEatCardPlacedCallback != null) {
            Platform.runLater(onEatCardPlacedCallback);
        } else if (card.getValue().equals("REVERSE") && onMachinePlayedCallback != null ||
                card.getValue().equals("SKIP") && onMachinePlayedCallback != null) {
            Platform.runLater(onMachinePlayedCallback);
        }

        sleepForEffectDelay();
        processCardPlay();
    }

    /**
     * Completes the machine player's turn and resets the turn status.
     */
    private void completeTurn() {
        if (onMachinePlayedCallback != null) {
            Platform.runLater(onMachinePlayedCallback);
        }
        hasPlayerPlayed = false;
    }

    /**
     * Simulates a delay for visual effect when playing cards.
     */
    private void sleepForEffectDelay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sleeps briefly to reduce CPU usage while monitoring the game state.
     */
    private void sleepForCpuEfficiency() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the index of a playable card in the machine's hand.
     *
     * @return the index of a playable card, or -1 if no card can be played
     */
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

    /**
     * Determines if a given card can be played based on the current card on the table.
     *
     * @param cardMachine the card in the machine's hand
     * @param cardTable   the current card on the table
     * @return {@code true} if the card can be played, otherwise {@code false}
     */
    private boolean isCardPlayable(Card cardMachine, Card cardTable) {
        return cardMachine.getColor().equals("WILD") ||
                cardMachine.getColor().equals(cardTable.getColor()) ||
                cardMachine.getValue().equals(cardTable.getValue());
    }

    /**
     * Sets whether the human player has completed their turn.
     *
     * @param hasPlayerPlayed {@code true} if the human player has played their turn
     */
    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    /**
     * Gets whether the human player has completed their turn.
     *
     * @return {@code true} if the human player has played their turn, otherwise {@code false}
     */
    public boolean getHasPlayerPlayed() {
        return hasPlayerPlayed;
    }

    /**
     * Sets whether the machine has selected a color when playing a wild card.
     *
     * @param isColorSelected {@code true} if the color has been selected
     */
    public void setIsColorSelected(boolean isColorSelected) {
        this.isColorSelected = isColorSelected;
    }
}
