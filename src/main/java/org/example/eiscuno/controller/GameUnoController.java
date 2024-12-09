package org.example.eiscuno.controller;

import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.Animations.AnimationAdapter;
import org.example.eiscuno.model.Animations.GameStageAnimations;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.game.cardRules;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

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

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachinePlayer();
        rules = new cardRules(gameUno);
        cardsDrawedPane.setOpacity(0);

        Card cardInitial = this.table.firstCard(this.deck);
        this.tableImageView.setImage(cardInitial.getImage());

        colorPickers.setOpacity(0);
        // Actualizar visualmente la mano de la máquina
        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();
        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.humanPlayer, this.tableImageView, this.gameUno, this.rules);
        threadPlayMachine.start();
        threadPlayMachine.setOnMachinePlayedCallback(this::runUpdateMachinePlay);
        threadPlayMachine.setOnCardEatenCallback(this::runUpdateMachineAte);
        threadPlayMachine.setOnEatCardPlacedCallBack(this::runAnimationAndUpdate);
        threadPlayMachine.setOnColorChangedByMachineCallBack(this::writeOnColorInfo);

        writeOnColorInfo();
        writeOnTurnInfo();


    }

    private void runAnimationAndUpdate(){
        printCardsHumanPlayer();
        animateDrawedCards();
    }

    private void runUpdateMachinePlay(){
        printCardsMachinePlayer();
        writeOnColorInfo();
        writeOnTurnInfo();
    }

    private void runUpdateMachineAte(){
        printCardsMachinePlayer();
        writeOnTurnInfo();
        writeOnColorInfo();
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
    }


    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        // Limpiar cartas previas
        this.gridPaneCardsPlayer.getChildren().clear();

        // Obtener las cartas visibles del jugador
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            // Configurar el evento de clic para cada carta
            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                if (threadPlayMachine.getHasPlayerPlayed()) {
                    System.out.println("Por favor, espera.");
                    return;
                }

                Card cardInitial = this.table.getCurrentCardOnTheTable();

                if (cardInitial.getColor().equals(card.getColor())|| cardInitial.getValue().equals(card.getValue())||
                        "WILD_CARD".equals(card.getValue()) || "WILD".equals(card.getColor())) {

                    if ("WILD".equals(card.getColor())) {
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        if(card.getValue().equals("+4")){
                            rules.applySpecialCardEffect(card, machinePlayer);
                            animateDrawedCards();
                            if(rules.getPLayerAte()){
                                printCardsMachinePlayer();
                                rules.setPlayerAte(false);
                            }
                        }
                        threadPlayMachine.setIsColorSelected(false);
                        colorPicker(card);

                    } else{
                        rules.applySpecialCardEffect(card, machinePlayer);
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        if(card.getValue().equals("+2")){
                            if(rules.getPLayerAte()){
                                printCardsMachinePlayer();
                                rules.setPlayerAte(false);
                            }
                        }
                        if(card.getValue().equals("REVERSE")||card.getValue().equals("SKIP")||card.getValue().equals("+2")){
                            if(card.getValue().equals("+2")){
                                animateDrawedCards();
                            }
                            writeOnColorInfo();
                            threadPlayMachine.setIsColorSelected(false);
                            writeOnTurnInfo();
                        }else{
                            writeOnColorInfo();
                            threadPlayMachine.setHasPlayerPlayed(true);
                            writeOnTurnInfo();
                        }

                    }
                } else {
                    System.out.println("No puedes jugar esa carta.");
                }

                debugCardInfo(card);
                printCardsHumanPlayer();
            });

            // Agregar la carta a la interfaz del jugador
            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }


    private void colorPicker(Card card) {
        colorPickers.setOpacity(1);

        redPicker.setOnAction(event -> {
            selectedColor = "RED";
            handleColorPick(card);  // Llamar a handleColorPick solo después de seleccionar el color
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


    private void handleColorPick(Card card){
        String pickedColor = null;

        // Verificar que selectedColor no sea null
        if (selectedColor == null) {
            System.out.println("No se ha seleccionado un color.");
            return; // Salir si no hay un color seleccionado
        }

        switch (selectedColor) {
            case "RED":
                System.out.println("Se seleccionó el color rojo.");
                pickedColor = "RED";
                break;
            case "BLUE":
                System.out.println("Se seleccionó el color azul.");
                pickedColor = "BLUE";
                break;
            case "YELLOW":
                System.out.println("Se seleccionó el color amarillo.");
                pickedColor = "YELLOW";
                break;
            case "GREEN":
                System.out.println("Se seleccionó el color verde.");
                pickedColor = "GREEN";
                break;
            default:
                System.out.println("Color no reconocido.");
                break;
        }

        // Verifica si pickedColor no es null antes de continuar
        if (pickedColor != null) {
            colorPickers.setOpacity(0);
            String finalPickedColor = pickedColor;
            Platform.runLater(() -> {
                table.setCurrentCardColor(finalPickedColor);
                writeOnColorInfo();
                // Realizamos las acciones que corresponden después de seleccionar el color
                threadPlayMachine.setHasPlayerPlayed(!card.getValue().equals("+4"));
                threadPlayMachine.setIsColorSelected(true);
            });
        }
    }


    private void printCardsMachinePlayer() {
        this.gridPaneCardsMachine.getChildren().clear();
        this.gridPaneCardsMachine.getColumnConstraints().clear();

        int totalCards = machinePlayer.getCardsPlayer().size();
        double columnWidthPercentage = 100.0 / totalCards;

        // Ajustar las restricciones de las columnas dinámicamente
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

            // Añadir la carta al grid
            this.gridPaneCardsMachine.add(cardImageView, i, 0);
        }
    }

    private void animateDrawedCards() {
        Card actualCard = table.getCurrentCardOnTheTable();
        if(actualCard.getValue().equals("+4")){
            drawNumber.setText("+4");
        }else if(actualCard.getValue().equals("+2")){
            drawNumber.setText("+2");
        }
        SequentialTransition sequentialTransition = new SequentialTransition(
                animations.fadeIn(cardsDrawedPane, 1), animations.fadeOut(cardsDrawedPane,1)
        );

        sequentialTransition.play();

    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
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
     * Handles the "Back" button action to show the previous set of cards.
     *
     * @param event the action event
     */
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
        if (threadPlayMachine.getHasPlayerPlayed()) {
            System.out.println("Ya jugaste, no puedes robar más cartas.");
            return; // Si ya jugó, no permitir que robe más cartas.
        }

        // Verificar si el jugador tiene cartas válidas para jugar
        if (hasValidCardToPlay()) {
            System.out.println("Tienes cartas válidas para jugar, debes jugar una carta.");
            return;  // Si tiene cartas válidas, no permite robar más cartas
        }

        // El jugador roba una carta si no tiene cartas válidas
        this.gameUno.eatCard(this.humanPlayer, 1);
        printCardsHumanPlayer();

        // Verificar si la última carta del jugador es válida para jugar
        Card lastCard = this.humanPlayer.getCardsPlayer().get(this.humanPlayer.getCardsPlayer().size() - 1);
        Card currentCardOnTable = this.table.getCurrentCardOnTheTable();

        // Verificar si la carta robada es válida
        if (!(lastCard.getColor().equals(currentCardOnTable.getColor()) ||
                lastCard.getValue().equals(currentCardOnTable.getValue()) ||
                "WILD".equals(lastCard.getColor()) || "WILD".equals(lastCard.getValue()))) {
            // La carta no es válida, se salta el turno del jugador
            System.out.println("No puedes jugar la carta robada. Turno saltado.");
            threadPlayMachine.setHasPlayerPlayed(true);
            writeOnTurnInfo();// Saltar el turno del jugador
        } else {
            System.out.println("La carta robada es válida, puedes jugarla.");
        }
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        // Implement logic to handle Uno event here
    }
}
