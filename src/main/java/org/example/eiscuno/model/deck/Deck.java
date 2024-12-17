package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.card.Card;

import java.util.Collections;
import java.util.Stack;

/**
 * Represents a deck of Uno cards.
 *
 * <p>This class manages the initialization, shuffling, and drawing of cards
 * for the Uno game. It uses a {@link Stack} to simulate a deck of cards.
 */
public class Deck {
    private Stack<Card> deckOfCards;

    /**
     * Constructs a new deck of Uno cards and initializes it.
     */
    public Deck() {
        deckOfCards = new Stack<>();
        initializeDeck();
    }

    /**
     * Initializes the deck with cards based on the EISCUnoEnum values.
     *
     * <p>Only valid Uno cards (e.g., color cards, special cards, and wild cards)
     * are added to the deck.
     */
    private void initializeDeck() {
        for (EISCUnoEnum cardEnum : EISCUnoEnum.values()) {
            if (cardEnum.name().startsWith("GREEN_") ||
                    cardEnum.name().startsWith("YELLOW_") ||
                    cardEnum.name().startsWith("BLUE_") ||
                    cardEnum.name().startsWith("RED_") ||
                    cardEnum.name().startsWith("SKIP_") ||
                    cardEnum.name().startsWith("REVERSE_") ||
                    cardEnum.name().startsWith("TWO_DRAW_") ||
                    cardEnum.name().equals("FOUR_WILD_DRAW") ||
                    cardEnum.name().equals("WILD")) {

                Card card = new Card(cardEnum.getFilePath(), getCardValue(cardEnum.name()), getCardColor(cardEnum.name()));
                deckOfCards.push(card);
            }
        }
        Collections.shuffle(deckOfCards);
    }

    /**
     * Determines the value of a card based on its name.
     *
     * @param name the name of the card
     * @return the value of the card as a string
     */
    private String getCardValue(String name) {
        if (name.endsWith("0")){
            return "0";
        } else if (name.endsWith("1")){
            return "1";
        } else if (name.endsWith("2")){
            return "2";
        } else if (name.endsWith("3")){
            return "3";
        } else if (name.endsWith("4")){
            return "4";
        } else if (name.endsWith("5")){
            return "5";
        } else if (name.endsWith("6")){
            return "6";
        } else if (name.endsWith("7")){
            return "7";
        } else if (name.endsWith("8")){
            return "8";
        } else if (name.endsWith("9")){
            return "9";
        } else if (name.startsWith("TWO")){
            return "+2";
        } else if (name.startsWith("FOUR")){
            return "+4";
        } else if (name.startsWith("SKIP")){
            return "SKIP";
        } else if (name.startsWith("REVERSE")){
            return "REVERSE";
        } else if (name.equals("WILD")){
            return "WILD_CARD";
        }
        else {
            return null;
        }
    }

    /**
     * Determines the color of a card based on its name.
     *
     * @param name the name of the card
     * @return the color of the card as a string
     */
    private String getCardColor(String name) {
        if (name.contains("GREEN")) {
            return "GREEN";
        } else if (name.contains("YELLOW")) {
            return "YELLOW";
        } else if (name.contains("BLUE")) {
            return "BLUE";
        } else if (name.contains("RED")) {
            return "RED";
        } else if(name.contains("WILD")){
            return "WILD";
        } else {
            return null;
        }
    }

    /**
     * Takes a card from the top of the deck.
     *
     * @return the card from the top of the deck
     * @throws IllegalStateException if the deck is empty
     */
    public Card takeCard() {
        if (deckOfCards.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }
        return deckOfCards.pop();
    }

    /**
     * Takes a numeric card from the deck to serve as the initial card.
     *
     * @return the first numeric card from the deck
     * @throws IllegalStateException if no numeric cards are available
     */
    public Card takeCardInit() {
        if (deckOfCards.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }

        Stack<Card> tempStack = new Stack<>();
        Card selectedCard = null;

        while (!deckOfCards.isEmpty()) {
            Card card = deckOfCards.pop();

            String cardValue = card.getValue();
            if (cardValue != null && cardValue.matches("\\d")) {
                selectedCard = card;
                break;
            }

            tempStack.push(card);
        }

        while (!tempStack.isEmpty()) {
            deckOfCards.push(tempStack.pop());
        }

        if (selectedCard == null) {
            throw new IllegalStateException("No hay más cartas numéricas en el mazo.");
        }

        return selectedCard;
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }
}
