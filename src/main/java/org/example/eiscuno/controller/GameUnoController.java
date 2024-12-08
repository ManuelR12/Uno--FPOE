package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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

    private volatile boolean isPickingColor; // Controla si el jugador está eligiendo un color

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;
    private cardRules rules;
    private String selectedColor;

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

        Card cardInitial = this.table.firstCard(this.deck);
        this.tableImageView.setImage(cardInitial.getImage());

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.humanPlayer, this.tableImageView, this.gameUno, this.rules);
        threadPlayMachine.start();

        colorPickers.setOpacity(0);
        // Actualizar visualmente la mano de la máquina
        threadPlayMachine.setOnMachinePlayedCallback(this::removeLastCardFromMachinePlayerGrid);
        threadPlayMachine.setOnCardEatenCallback(this::printCardsMachinePlayer);

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

                if (cardInitial.getColor() == card.getColor() || cardInitial.getValue() == card.getValue() ||
                        "WILD_CARD".equals(card.getValue()) || "WILD".equals(card.getColor())) {

                    if ("WILD".equals(card.getColor())) {
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        if(card.getValue().equals("+4")){
                            rules.applySpecialCardEffect(card, machinePlayer);
                            if(rules.getPLayerAte()){
                                printCardsMachinePlayer();
                                rules.setPlayerAte(false);
                            }
                        }
                        threadPlayMachine.setIsColorSelected(false);
                        colorPicker(card, cardInitial, gameUno);
                        for (Card item : humanPlayer.getCardsPlayer()) {
                            System.out.println(item);
                        }
                        System.out.println("//////");
                        System.out.println("CARTAS MAQUINA");
                        for (Card item : machinePlayer.getCardsPlayer()) {
                            System.out.println(item);
                        }

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
                        threadPlayMachine.setHasPlayerPlayed(true);
                        for (Card item : humanPlayer.getCardsPlayer()) {
                            System.out.println(item);
                        }
                        System.out.println("//////");
                        System.out.println("CARTAS MAQUINA");
                        for (Card item : machinePlayer.getCardsPlayer()) {
                            System.out.println(item);
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


    private void colorPicker(Card card, Card initialCard, GameUno gameUno) {
        colorPickers.setOpacity(1);

        redPicker.setOnAction(event -> {
            selectedColor = "RED";
            handleColorPick(card, initialCard, gameUno);  // Llamar a handleColorPick solo después de seleccionar el color
        });

        yellowPicker.setOnAction(event -> {
            selectedColor = "YELLOW";
            handleColorPick(card, initialCard, gameUno);
        });

        bluePicker.setOnAction(event -> {
            selectedColor = "BLUE";
            handleColorPick(card, initialCard,gameUno);
        });

        greenPicker.setOnAction(event -> {
            selectedColor = "GREEN";
            handleColorPick(card, initialCard, gameUno);
        });
    }


    private void handleColorPick(Card card, Card initialCard, GameUno gameUno){
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
                // Realizamos las acciones que corresponden después de seleccionar el color
                threadPlayMachine.setHasPlayerPlayed(true);
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


    private void removeLastCardFromMachinePlayerGrid() {
        // Verifica si hay hijos en el GridPane antes de intentar eliminar
        if (!this.gridPaneCardsMachine.getChildren().isEmpty()) {
            // Encuentra el nodo en la última columna
            Node lastNode = null;
            int maxColumn = -1;

            for (Node node : this.gridPaneCardsMachine.getChildren()) {
                Integer columnIndex = GridPane.getColumnIndex(node);
                if (columnIndex != null && columnIndex > maxColumn) {
                    maxColumn = columnIndex;
                    lastNode = node;
                }
            }

            // Si se encuentra el último nodo, eliminarlo
            if (lastNode != null) {
                this.gridPaneCardsMachine.getChildren().remove(lastNode);
            }
        }
    }

    private void addCardToMachinePlayerGrid() {
        for (int i = 0; i < threadPlayMachine.getCardsToEat(); i++){
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
            if (card.getColor() == currentCardOnTable.getColor() ||
                    card.getValue() == currentCardOnTable.getValue() ||
                    "WILD".equals(card.getValue())) {
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

        if (!(lastCard.getColor() == currentCardOnTable.getColor() ||
                lastCard.getValue() == currentCardOnTable.getValue() ||
                "WILD".equals(lastCard.getValue()))) {
            // La carta no es válida, se salta el turno del jugador
            System.out.println("No puedes jugar la carta robada. Turno saltado.");
            threadPlayMachine.setHasPlayerPlayed(true);  // Saltar el turno del jugador
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
