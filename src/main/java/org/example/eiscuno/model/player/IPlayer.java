package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Interface representing a player in the Uno game.
 *
 * <p>This interface provides methods to manage the player's hand of cards,
 * including adding, retrieving, and removing cards.
 */
public interface IPlayer {

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to be added to the player's hand
     */
    void addCard(Card card);

    /**
     * Retrieves a card from the player's hand based on its index.
     *
     * @param index the index of the card to retrieve
     * @return the card at the specified index in the player's hand
     */
    Card getCard(int index);

    /**
     * Retrieves all cards currently held by the player.
     *
     * @return an {@link ArrayList} containing all cards in the player's hand
     */
    ArrayList<Card> getCardsPlayer();

    /**
     * Removes a card from the player's hand based on its index.
     *
     * @param index the index of the card to remove
     */
    void removeCard(int index);
}
