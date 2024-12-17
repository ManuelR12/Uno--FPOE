package org.example.eiscuno.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eiscuno.model.Animations.AnimationAdapter;
import org.example.eiscuno.model.Animations.GameStageAnimations;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.cardRules;
import org.example.eiscuno.model.machine.ThreadMonitorMachine;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;

import java.io.IOException;

/**
 * Controller class for the Uno game.
 *
 * <p>This class manages the game's user interface, player interactions, game rules,
 * and communication between the human player, the machine player, and game threads.
 */
public class GameUnoController {

    @FXML
    private GridPane gridPaneCardsMachine;
    @FXML
    private GridPane gridPaneCardsPlayer;
    @FXML
    private ImageView tableImageView;
    @FXML
    private Button redPicker;
    @FXML
    private Button bluePicker;
    @FXML
    private Button yellowPicker;
    @FXML
    private Button greenPicker;
    @FXML
    private AnchorPane colorPickers;
    @FXML
    private Label drawNumber;
    @FXML
    private AnchorPane cardsDrawedPane;
    @FXML
    private TextField colorTxtField;
    @FXML
    private TextField turnTxtField;
    @FXML
    private Button unoButton;
    @FXML
    private Label gameStatusLabel;
    @FXML
    private ImageView penalizeButtonView;
    @FXML
    private Button penalizeButton;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;
    private cardRules rules;
    private String selectedColor;
    private GameStageAnimations animations;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    private ThreadMonitorMachine threadMonitorMachine;

    /**
     * Initializes the controller.
     *
     * <p>Sets up the game, initializes variables, starts threads for machine actions,
     * and configures user interface elements.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachinePlayer();
        colorTxtField.setEditable(false);
        turnTxtField.setEditable(false);

        rules = new cardRules(gameUno);
        cardsDrawedPane.setOpacity(0);

        Card cardInitial = this.table.firstCard(this.deck);
        this.tableImageView.setImage(cardInitial.getImage());

        colorPickers.setOpacity(0);
        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        threadMonitorMachine = new ThreadMonitorMachine(this.machinePlayer);
        t.start();
        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.humanPlayer, this.tableImageView, this.gameUno, this.rules);

        threadPlayMachine.start();
        threadPlayMachine.setOnMachinePlayedCallback(this::runUpdateMachinePlay);
        threadPlayMachine.setOnCardEatenCallback(this::runUpdateMachineAte);
        threadPlayMachine.setOnEatCardPlacedCallback(this::runAnimationAndUpdate);
        threadPlayMachine.setOnColorChangedByMachineCallback(this::writeOnColorInfo);

        threadMonitorMachine.start();
        threadMonitorMachine.setOnMachineHasOneCardCallback(this::showPenalizeButton);
        threadMonitorMachine.setOnMachineSaidUnoCallback(this::hidePenalizeButton);
        threadSingUNOMachine.setOnUnoSang(this::penalizeHumanUNO);

        writeOnColorInfo();
        writeOnTurnInfo();
        hidePenalizeButton();
    }

    /**
     * Initializes the game variables including players, deck, and table.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
        this.animations = new GameStageAnimations();
        rules = new cardRules(gameUno);
    }


    /**
     * Animates the cards drawn effect for special cards like +2 and +4.
     */
    private void animateDrawedCards() {
        Card actualCard = table.getCurrentCardOnTheTable();
        if (actualCard.getValue().equals("+4")) {
            drawNumber.setText("+4");
        } else if (actualCard.getValue().equals("+2")) {
            drawNumber.setText("+2");
        }

        SequentialTransition sequentialTransition = new SequentialTransition(
                animations.fadeIn(cardsDrawedPane, 1), animations.fadeOut(cardsDrawedPane, 1)
        );

        sequentialTransition.play();
    }


    /**
     * Updates UI after animations and machine actions.
     */
    private void runAnimationAndUpdate(){
        printCardsHumanPlayer();
        printCardsMachinePlayer();
        animateDrawedCards();
    }


    /**
     * Updates UI after the machine plays a card.
     */
    private void runUpdateMachinePlay(){
        printCardsMachinePlayer();
        writeOnColorInfo();
        writeOnTurnInfo();
        checkGameEnd();
    }

    /**
     * Updates UI after the machine draws a card.
     */
    private void runUpdateMachineAte(){
        printCardsMachinePlayer();
        writeOnTurnInfo();
        writeOnColorInfo();
        checkGameEnd();
    }


    /**
     * Updates the color display field with the current card color.
     */
    private void writeOnColorInfo(){
        colorTxtField.setText(table.getCurrentCardOnTheTable().getColor());
    }

    /**
     * Updates the turn display field to indicate the current turn.
     */
    private void writeOnTurnInfo(){
        if(threadPlayMachine.getHasPlayerPlayed()){
            turnTxtField.setText("Machine");
        } else{
            turnTxtField.setText("Human");
        }
    }


    /**
     * Displays the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();
            setCardClickHandler(card, cardImageView);
            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }

    /**
     * Sets a click handler on a card that allows the player to attempt playing the card.
     *
     * @param card          the card to set the handler on
     * @param cardImageView the ImageView representation of the card
     */
    private void setCardClickHandler(Card card, ImageView cardImageView) {
        cardImageView.setOnMouseClicked((MouseEvent event) -> {
            if (threadPlayMachine.getHasPlayerPlayed()) {
                System.out.println("Por favor, espera.");
                return;
            }

            Card cardInitial = this.table.getCurrentCardOnTheTable();
            if (isCardPlayable(card, cardInitial)) {
                playCard(card);
                checkUNO();
            } else {
                System.out.println("No puedes jugar esa carta.");
            }

            debugCardInfo(card);
            printCardsHumanPlayer();
        });
    }

    /**
     * Determines if a card can be played based on the current card on the table.
     *
     * @param card        the card the player wants to play
     * @param cardInitial the current card on the table
     * @return {@code true} if the card can be played, otherwise {@code false}
     */
    private boolean isCardPlayable(Card card, Card cardInitial) {
        return cardInitial.getColor().equals(card.getColor()) || cardInitial.getValue().equals(card.getValue()) ||
                "WILD_CARD".equals(card.getValue()) || "WILD".equals(card.getColor());
    }

    /**
     * Plays the selected card and processes its effects.
     *
     * @param card the card to be played
     */
    private void playCard(Card card) {
        if ("WILD".equals(card.getColor())) {
            playWildCard(card);
        } else {
            playRegularCard(card);
        }
        checkGameEnd();
    }

    /**
     * Handles the action of playing a wild card, including color selection and effects.
     *
     * @param card the wild card played
     */
    private void playWildCard(Card card) {
        gameUno.playCard(card);
        tableImageView.setImage(card.getImage());
        humanPlayer.removeCard(findPosCardsHumanPlayer(card));

        if (card.getValue().equals("+4")) {
            rules.applySpecialCardEffect(card, machinePlayer);
            animateDrawedCards();
            if (rules.getPLayerAte()) {
                printCardsMachinePlayer();
                rules.setPlayerAte(false);
            }
        }

        threadPlayMachine.setIsColorSelected(false);
        colorPicker(card);
    }

    /**
     * Handles the action of playing a regular card and processes its effects.
     *
     * @param card the regular card played
     */
    private void playRegularCard(Card card) {
        rules.applySpecialCardEffect(card, machinePlayer);
        gameUno.playCard(card);
        tableImageView.setImage(card.getImage());
        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
        checkUNO();

        if (card.getValue().equals("+2") && rules.getPLayerAte()) {
            printCardsMachinePlayer();
            rules.setPlayerAte(false);
        }

        handleSpecialCardEffects(card);
    }


    /**
     * Handles the special effects of cards like REVERSE, SKIP, and +2.
     *
     * @param card the card with a special effect
     */
    private void handleSpecialCardEffects(Card card) {
        if (card.getValue().equals("REVERSE") || card.getValue().equals("SKIP") || card.getValue().equals("+2")) {
            if (card.getValue().equals("+2")) {
                animateDrawedCards();
            }
            writeOnColorInfo();
            threadPlayMachine.setIsColorSelected(false);
            writeOnTurnInfo();
        } else {
            writeOnColorInfo();
            threadPlayMachine.setHasPlayerPlayed(true);
            writeOnTurnInfo();
        }
    }


    /**
     * Opens the color picker when a wild card is played.
     *
     * @param card the wild card triggering the color picker
     */
    private void colorPicker(Card card) {
        colorPickers.setOpacity(1);

        redPicker.setOnAction(event -> {
            selectedColor = "RED";
            handleColorPick(card);
        });
        yellowPicker.setOnAction(event -> {
            selectedColor = "YELLOW";
            handleColorPick(card);
        });
        bluePicker.setOnAction(event -> {
            selectedColor = "BLUE";
            handleColorPick(card);
        });
        greenPicker.setOnAction(event -> {
            selectedColor = "GREEN";
            handleColorPick(card);
        });
    }


    /**
     * Exception thrown when an invalid move is attempted.
     */
    public class InvalidMoveException extends Exception {
        /**
         * Constructs an InvalidMoveException with the specified message.
         *
         * @param message the detail message of the exception
         */
        public InvalidMoveException(String message) {
            super(message);
        }
    }


    /**
     * Handles the color selection for wild cards.
     *
     * @param card the wild card being played
     */
    private void handleColorPick(Card card) {
        if (selectedColor == null) {
            System.out.println("No se ha seleccionado un color.");
            return;
        }

        String pickedColor = getPickedColor(selectedColor);
        if (pickedColor != null) {
            colorPickers.setOpacity(0);
            String finalPickedColor = pickedColor;
            Platform.runLater(() -> {
                table.setCurrentCardColor(finalPickedColor);
                writeOnColorInfo();
                threadPlayMachine.setHasPlayerPlayed(!card.getValue().equals("+4"));
                threadPlayMachine.setIsColorSelected(true);
            });
        }
    }

    /**
     * Converts the selected color string to a valid color.
     *
     * @param selectedColor the color string selected by the player
     * @return the valid color string or null if invalid
     */
    private String getPickedColor(String selectedColor) {
        switch (selectedColor) {
            case "RED": return "RED";
            case "BLUE": return "BLUE";
            case "YELLOW": return "YELLOW";
            case "GREEN": return "GREEN";
            default: return null;
        }
    }


    /**
     * Displays the machine player's cards as back-facing cards on the grid pane.
     */
    private void printCardsMachinePlayer() {
        this.gridPaneCardsMachine.getChildren().clear();
        this.gridPaneCardsMachine.getColumnConstraints().clear();

        int totalCards = machinePlayer.getCardsPlayer().size();
        double columnWidthPercentage = 100.0 / totalCards;

        for (int i = 0; i < totalCards; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(columnWidthPercentage);
            this.gridPaneCardsMachine.getColumnConstraints().add(column);
        }

        for (int i = 0; i < totalCards; i++) {
            Image img = new Image(getClass().getResource("/org/example/eiscuno/cards-uno/card_uno.png").toExternalForm());
            ImageView cardImageView = new ImageView(img);
            cardImageView.setY(16);
            cardImageView.setFitHeight(90);
            cardImageView.setFitWidth(70);
            this.gridPaneCardsMachine.add(cardImageView, i, 0);
        }
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find in the player's hand
     * @return the index of the card if found, or {@code -1} if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Handles the "Back" button action to navigate to the previous set of cards.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to navigate to the next set of cards.
     *
     * @param event the action event triggered by the user
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Checks if the human player has a valid card to play.
     *
     * @return {@code true} if the player has a valid card to play
     * @throws InvalidMoveException if the player has no valid cards to play
     */
    private boolean hasValidCardToPlay() throws InvalidMoveException {
        // Get the last card on the table
        Card currentCardOnTable = this.table.getCurrentCardOnTheTable();

        // Check if the player has a valid card to play
        for (Card card : this.humanPlayer.getCardsPlayer()) {
            if (card.getColor().equals(currentCardOnTable.getColor()) ||
                    card.getValue().equals(currentCardOnTable.getValue()) ||
                    "WILD".equals(card.getColor())) {
                return true;  // The player has a valid card to play
            }
        }
        // Throw an exception if no valid card is available
        throw new InvalidMoveException("No tienes cartas válidas para jugar.");
    }


    /**
     * Prints debugging information about a selected card.
     *
     * @param card the card to display information for
     */
    private void debugCardInfo(Card card) {
        if (card != null) {
            String cardInfo = "Carta seleccionada: ";
            if ("WILD".equals(card.getValue())) {
                cardInfo += "Tipo: WILD, ";
            } else {
                cardInfo += "Tipo: " + card.getColor() + ", ";
            }
            cardInfo += "Número: " + card.getValue();
            System.out.println(cardInfo); // Print the card's details to the console
        }
    }

    /**
     * Handles the action of taking a card when the player has no valid moves.
     *
     * @param event the action event triggered when the player clicks the "Take Card" button
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        // Check if it's already the player's turn
        if (isPlayerTurnOver()) {
            return; // If the player has already played, they cannot take more cards.
        }

        if (unoButton.isVisible()) {
            System.out.println("TIENES QUE CANTAR UNO!!!");
            return;
        }

        try {
            // Verify if the player has any valid cards to play
            if (hasValidCardToPlay()) {
                System.out.println("Tienes cartas válidas para jugar, debes jugar una carta.");
                return; // Do not allow the player to draw a card if they have a valid move.
            }
        } catch (InvalidMoveException e) {
            takeCardAndPrint(); // If no valid moves exist, draw a card and print the updated hand.
        }

        // Verify if the last drawn card can be played
        if (isLastCardPlayable()) {
            System.out.println("La carta robada es válida, puedes jugarla.");
        } else {
            System.out.println("No puedes jugar la carta robada. Turno saltado.");
            skipTurn();
        }
    }

    /**
     * Checks if the player's turn is already over.
     *
     * @return {@code true} if the player has already played their turn, otherwise {@code false}
     */
    private boolean isPlayerTurnOver() {
        if (threadPlayMachine.getHasPlayerPlayed()) {
            System.out.println("Ya jugaste, no puedes robar más cartas.");
            return true;
        }
        return false;
    }

    /**
     * Makes the player draw one card from the deck and updates the UI.
     */
    private void takeCardAndPrint() {
        this.gameUno.eatCard(this.humanPlayer, 1);
        printCardsHumanPlayer();
        checkGameEnd();
    }

    /**
     * Determines if the last card in the player's hand can be played.
     *
     * @return {@code true} if the last card is playable, otherwise {@code false}
     */
    private boolean isLastCardPlayable() {
        Card lastCard = this.humanPlayer.getCardsPlayer().get(this.humanPlayer.getCardsPlayer().size() - 1);
        Card currentCardOnTable = this.table.getCurrentCardOnTheTable();

        return lastCard.getColor().equals(currentCardOnTable.getColor()) ||
                lastCard.getValue().equals(currentCardOnTable.getValue()) ||
                "WILD".equals(lastCard.getColor()) || "WILD".equals(lastCard.getValue());
    }


    /**
     * Skips the player's turn if they cannot play a valid card.
     */
    private void skipTurn() {
        threadPlayMachine.setHasPlayerPlayed(true);
        writeOnTurnInfo(); // Update the UI to reflect that the player's turn is skipped.
    }

    /**
     * Checks if the human player has only one card left and displays the UNO button.
     */
    private void checkUNO() {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            unoButton.setVisible(true);
        }
    }

    /**
     * Handles the action of saying "Uno" when the player has one card left.
     *
     * @param event the action event triggered when the UNO button is clicked
     */
    @FXML
    public void onHandleUno(ActionEvent event) {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            unoButton.setVisible(false);
            System.out.println("Declaraste UNO correctamente.");
        }
    }


    /**
     * Penalizes the human player if they fail to declare UNO on time.
     */
    private void penalizeHumanUNO() {
        if (unoButton.isVisible()) {
            gameUno.eatCard(humanPlayer, 2); // Penalize with 2 cards
            printCardsHumanPlayer();
            unoButton.setVisible(false);
            System.out.println("No declaraste UNO a tiempo. Penalizado con 2 cartas.");
        }
    }

    /**
     * Penalizes the machine player if it fails to declare UNO on time.
     */
    private void penalizeMachineUNO() {
        gameUno.eatCard(machinePlayer, 2); // Penalize with 2 cards
        printCardsMachinePlayer();
        System.out.println("La máquina no declaró UNO a tiempo. Penalizada con 2 cartas.");
    }


    /**
     * Checks if the game has ended and updates the UI accordingly.
     */
    private void checkGameEnd() {
        if (humanPlayer.getCardsPlayer().isEmpty()) {
            killThreads();
            gameStatusLabel.setText("YOU WON!");
            showEndGameAlert("Victory", "You won the UNO game!", Alert.AlertType.INFORMATION);
        } else if (machinePlayer.getCardsPlayer().isEmpty()) {
            killThreads();
            gameStatusLabel.setText("YOU LOST");
            showEndGameAlert("Defeat", "The machine won the game.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an end-of-game alert with options to close or restart the game.
     *
     * @param title     the title of the alert
     * @param content   the message content to display
     * @param alertType the type of alert (e.g., INFORMATION, ERROR)
     */
    private void showEndGameAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(content);

        // Custom buttons for the alert
        ButtonType closeButton = new ButtonType("Close");
        ButtonType replayButton = new ButtonType("Play again");

        alert.getButtonTypes().setAll(replayButton, closeButton);

        // Show and handle user response
        alert.showAndWait().ifPresent(response -> {
            if (response == replayButton) {
                restartGame();
            } else {
                Platform.exit(); // Exit the application
            }
        });
    }

    /**
     * Restarts the game by closing the current instance and creating a new one.
     */
    private void restartGame() {
        try {
            GameUnoStage.deleteInstance(); // Close the current game instance
            GameUnoStage.getInstance();    // Start a new game instance
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("No se pudo reiniciar el juego");
            errorAlert.setContentText("Ocurrió un problema al intentar reiniciar el juego.");
            errorAlert.showAndWait();
        }
    }


    /**
     * Displays the penalize button when the machine has one card left.
     */
    public void showPenalizeButton() {
        penalizeButton.setVisible(true);
        penalizeButtonView.setOpacity(1);
    }


    /**
     * Hides the penalize button when the machine has declared UNO.
     */
    public void hidePenalizeButton() {
        penalizeButtonView.setOpacity(0);
        penalizeButton.setVisible(false);
    }

    /**
     * Handles the action of penalizing the machine if it fails to declare UNO.
     *
     * @param actionEvent the action event triggered when the penalize button is clicked
     */
    public void handlePenalizeMachine(ActionEvent actionEvent) {
        if (!threadMonitorMachine.didMachineSayUno) {
            penalizeMachineUNO();
            hidePenalizeButton();
        }
    }


    /**
     * Stops all active game threads to clean up resources.
     */
    public void killThreads() {
        threadMonitorMachine.stopMonitoring();
        threadPlayMachine.stopMachinePlay();
        threadSingUNOMachine.stopMonitoringUnoSang();
    }

    /**
     * Handles the action of exiting the game.
     *
     * @param mouseEvent the mouse event triggered when the exit option is clicked
     */
    public void handleExitGame(MouseEvent mouseEvent) {
        killThreads();
        GameUnoStage.deleteInstance();
    }
}


