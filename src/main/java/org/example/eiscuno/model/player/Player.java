package org.example.eiscuno.model.player;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;

/**
 * Represents a player in the Uno game.
 *
 * <p>This class implements the {@link IPlayer} interface and manages the player's hand
 * of cards, including adding, removing, and retrieving cards. Each player is identified
 * by a specific type.
 */
public class Player implements IPlayer {
    private ArrayList<Card> cardsPlayer;
    private String typePlayer;

    /**
     * Constructs a new Player object with an empty hand of cards.
     *
     * @param typePlayer the type of the player (e.g., human or machine)
     */
    public Player(String typePlayer){
        this.cardsPlayer = new ArrayList<Card>();
        this.typePlayer = typePlayer;
    };

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to be added to the player's hand
     */
    @Override
    public void addCard(Card card){
        cardsPlayer.add(card);
    }

    /**
     * Retrieves all cards currently held by the player.
     *
     * @return an {@link ArrayList} containing all cards in the player's hand
     */
    @Override
    public ArrayList<Card> getCardsPlayer() {
        return cardsPlayer;
    }

    /**
     * Removes a card from the player's hand based on its index.
     *
     * @param index the index of the card to remove
     */
    @Override
    public void removeCard(int index) {
        cardsPlayer.remove(index);
    }

    /**
     * Retrieves a card from the player's hand based on its index.
     *
     * @param index the index of the card to retrieve
     * @return the card at the specified index in the player's hand
     */
    @Override
    public Card getCard(int index){
        return cardsPlayer.get(index);
    }

    /**
     * Retrieves the type of the player.
     *
     * @return a string representing the player's type (e.g., human or machine)
     */
    public String getTypePlayer() {
        return typePlayer;
    }
}
