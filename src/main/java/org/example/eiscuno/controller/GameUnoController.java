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
 * TODO: ERASE ALL THE DEBUG METHODS IN THIS AND OTHER CLASSES. STILL IN THIS VERSION.
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
        // Actualizar visualmente la mano de la máquina
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

    private void runAnimationAndUpdate(){
        printCardsHumanPlayer();
        printCardsMachinePlayer();
        printCardsMachinePlayer();
        animateDrawedCards();
    }

    private void runUpdateMachinePlay(){
        printCardsMachinePlayer();
        writeOnColorInfo();
        writeOnTurnInfo();
        checkGameEnd();
    }

    private void runUpdateMachineAte(){
        printCardsMachinePlayer();
        writeOnTurnInfo();
        writeOnColorInfo();
        checkGameEnd();
    }

    private void writeOnColorInfo(){
        colorTxtField.setText(table.getCurrentCardOnTheTable().getColor());
    }

    private void writeOnTurnInfo(){
        if(threadPlayMachine.getHasPlayerPlayed()){
            turnTxtField.setText("Machine");
        } else{
            turnTxtField.setText("Human");
        }

    }
    /**
     * Initializes the variables for the game.
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
     * Prints the human player's cards on the grid pane.
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

    private boolean isCardPlayable(Card card, Card cardInitial) {
        return cardInitial.getColor().equals(card.getColor()) || cardInitial.getValue().equals(card.getValue()) ||
                "WILD_CARD".equals(card.getValue()) || "WILD".equals(card.getColor());
    }

    private void playCard(Card card) {
        if ("WILD".equals(card.getColor())) {
            playWildCard(card);
        } else {
            playRegularCard(card);
        }
        checkGameEnd();
    }

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

    private String getPickedColor(String selectedColor) {
        switch (selectedColor) {
            case "RED": return "RED";
            case "BLUE": return "BLUE";
            case "YELLOW": return "YELLOW";
            case "GREEN": return "GREEN";
            default: return null;
        }
    }

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

    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    private boolean hasValidCardToPlay() {
        // Obtener la última carta en la mesa
        Card currentCardOnTable = this.table.getCurrentCardOnTheTable();

        // Verificar si el jugador tiene alguna carta válida
        for (Card card : this.humanPlayer.getCardsPlayer()) {
            if (card.getColor().equals(currentCardOnTable.getColor())||
                    card.getValue().equals(currentCardOnTable.getValue()) ||
                    "WILD".equals(card.getColor())) {
                return true;  // El jugador tiene una carta válida para jugar
            }
        }
        return false;  // El jugador no tiene cartas válidas para jugar
    }

    private void debugCardInfo(Card card) {
        if (card != null) {
            // Si la carta es de tipo WILD o de color normal
            String cardInfo = "Carta seleccionada: ";
            if ("WILD".equals(card.getValue())) {
                cardInfo += "Tipo: WILD, ";
            } else {
                cardInfo += "Tipo: " + card.getColor() + ", ";
            }
            cardInfo += "Número: " + card.getValue();
            System.out.println(cardInfo);  // Imprime la información de la carta
        }
    }

    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        // Verificar si es el turno del jugador
        if (isPlayerTurnOver()) {
            return; // Si ya jugó, no permitir que robe más cartas.
        }

        if(unoButton.isVisible()){
            System.out.println("TIENES QUE CANTAR UNO!!!");
            return;
        }

        // Verificar si el jugador tiene cartas válidas para jugar
        if (hasValidCardToPlay()) {
            System.out.println("Tienes cartas válidas para jugar, debes jugar una carta.");
            return;  // Si tiene cartas válidas, no permite robar más cartas
        }

        // El jugador roba una carta si no tiene cartas válidas
        takeCardAndPrint();

        // Verificar si la última carta del jugador es válida para jugar
        if (isLastCardPlayable()) {
            System.out.println("La carta robada es válida, puedes jugarla.");
        } else {
            System.out.println("No puedes jugar la carta robada. Turno saltado.");
            skipTurn();
        }
    }

    private boolean isPlayerTurnOver() {
        if (threadPlayMachine.getHasPlayerPlayed()) {
            System.out.println("Ya jugaste, no puedes robar más cartas.");
            return true;
        }
        return false;
    }

    private void takeCardAndPrint() {
        this.gameUno.eatCard(this.humanPlayer, 1);
        printCardsHumanPlayer();
        checkGameEnd();
    }

    private boolean isLastCardPlayable() {
        Card lastCard = this.humanPlayer.getCardsPlayer().get(this.humanPlayer.getCardsPlayer().size() - 1);
        Card currentCardOnTable = this.table.getCurrentCardOnTheTable();

        return lastCard.getColor().equals(currentCardOnTable.getColor()) ||
                lastCard.getValue().equals(currentCardOnTable.getValue()) ||
                "WILD".equals(lastCard.getColor()) || "WILD".equals(lastCard.getValue());
    }

    private void skipTurn() {
        threadPlayMachine.setHasPlayerPlayed(true);
        writeOnTurnInfo(); // Saltar el turno del jugador
    }


    /**
     * Handles the action of saying "Uno".
     *
     */
    private void checkUNO() {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            unoButton.setVisible(true);
        }
    }

    @FXML
    public void onHandleUno(ActionEvent event) {
        if (humanPlayer.getCardsPlayer().size() == 1) {
            unoButton.setVisible(false);
            System.out.println("Declaraste UNO correctamente.");
        }
    }


    private void penalizeHumanUNO() {
        if(unoButton.isVisible()){
            gameUno.eatCard(humanPlayer, 2); // Penalización con 2 cartas
            printCardsHumanPlayer();
            unoButton.setVisible(false);
            System.out.println("No declaraste UNO a tiempo. Penalizado con 2 cartas.");
        }

    }

    private void penalizeMachineUNO() {
        gameUno.eatCard(machinePlayer, 2); // Penalización con 2 cartas
        printCardsMachinePlayer();
        System.out.println("La máquina no declaró UNO a tiempo. Penalizada con 2 cartas.");
    }


    /**
     * Verifica si el jugador humano o la máquina han ganado la partida.
     */
    private void checkGameEnd() {
        if (humanPlayer.getCardsPlayer().isEmpty()) {
            killThreads();
            gameStatusLabel.setText("¡GANASTE!");
            showEndGameAlert("¡Victoria!", "¡Has ganado el juego! 🎉", Alert.AlertType.INFORMATION);
        } else if (machinePlayer.getCardsPlayer().isEmpty()) {
            killThreads();
            gameStatusLabel.setText("HAS PERDIDO");
            showEndGameAlert("Derrota", "Lo siento, has perdido el juego. 😞", Alert.AlertType.ERROR);
            killThreads();
        }
    }

    private void showEndGameAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No queremos un encabezado
        alert.setContentText(content);

        // Botones personalizados para la alerta
        ButtonType closeButton = new ButtonType("Cerrar");
        ButtonType replayButton = new ButtonType("Volver a jugar");

        alert.getButtonTypes().setAll(replayButton, closeButton);

        // Mostrar y esperar la acción del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == replayButton) {
                restartGame();
            } else {
                Platform.exit(); // Salir de la aplicación
            }
        });
    }

    private void restartGame() {
        try {
            // Cerrar la instancia actual
            GameUnoStage.deleteInstance();

            // Crear una nueva instancia del juego
            GameUnoStage.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar una alerta si ocurre un error
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("No se pudo reiniciar el juego");
            errorAlert.setContentText("Ocurrió un problema al intentar reiniciar el juego.");
            errorAlert.showAndWait();
        }
    }


    public void showPenalizeButton(){
        penalizeButton.setVisible(true);
        penalizeButtonView.setOpacity(1);
    }

    public void hidePenalizeButton(){
        penalizeButtonView.setOpacity(0);
        penalizeButton.setVisible(false);
    }

    public void handlePenalizeMachine(ActionEvent actionEvent) {
        if(!threadMonitorMachine.didMachineSayUno){
            penalizeMachineUNO();
            hidePenalizeButton();
        }
    }

    public void killThreads(){
        threadMonitorMachine.stopMonitoring();
        threadPlayMachine.stopMachinePlay();
        threadSingUNOMachine.stopMonitoringUnoSang();
    }

    public void handleExitGame(MouseEvent mouseEvent) {
        killThreads();
        GameUnoStage.deleteInstance();
    }
}


