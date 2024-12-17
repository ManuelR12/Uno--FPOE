package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;

import java.util.ArrayList;

/**
 * Represents the table in the Uno game where cards are played.
 *
 * <p>This class manages the cards currently on the table, including adding new cards,
 * retrieving the current card, and initializing the first card of the game.
 */
public class Table {
    private ArrayList<Card> cardsTable;

    /**
     * Constructs a new Table object with no cards on it.
     */
    public Table(){
        this.cardsTable = new ArrayList<Card>();
    }

    /**
     * Adds a card to the table.
     *
     * @param card the card to be added to the table
     */
    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
    }

    /**
     * Retrieves the current card on the table.
     *
     * @return the card currently on the table
     * @throws IndexOutOfBoundsException if there are no cards on the table
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
        return this.cardsTable.get(this.cardsTable.size()-1);
    }

    /**
     * Initializes the first card on the table by drawing a card from the deck.
     *
     * @param deck the deck from which the initial card is drawn
     * @return the first card placed on the table
     */
    public Card firstCard(Deck deck) {
        Card cardInitial = deck.takeCardInit();
        this.addCardOnTheTable(cardInitial);
        return cardInitial;
    }

    /**
     * Sets the color of the current card on the table.
     *
     * @param color the new color to set on the current card
     * @throws IndexOutOfBoundsException if there are no cards on the table
     */
    public void setCurrentCardColor(String color) {
        // Ensure there is a card on the table
        if (!cardsTable.isEmpty()) {
            Card currentCard = cardsTable.get(cardsTable.size() - 1);
            currentCard.setColor(color);  // Change the color of the card
        } else {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
    }
}
