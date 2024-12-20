package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;

/**
 * Interface representing the core functionality of the Uno game.
 *
 * <p>This interface defines methods for starting the game, managing card actions,
 * and checking game status.
 */
public interface IGameUno {

    /**
     * Starts the Uno game by initializing the game setup.
     */
    void startGame();

    /**
     * Makes a player draw a specified number of cards from the deck.
     *
     * @param player        the player who will draw the cards
     * @param numberOfCards the number of cards to be drawn
     */
    void eatCard(Player player, int numberOfCards);

    /**
     * Plays a card in the game, adding it to the table.
     *
     * @param card the card to be played
     */
    void playCard(Card card);

    /**
     * Handles the action when a player shouts "Uno" to prevent penalties.
     *
     * @param playerWhoSang the identifier of the player who shouted "Uno"
     */
    void haveSungOne(String playerWhoSang);

    /**
     * Retrieves the current visible cards of the human player starting from a specific position.
     *
     * @param posInitCardToShow the starting position of the cards to be shown
     * @return an array of cards that are currently visible to the human player
     */
    Card[] getCurrentVisibleCardsHumanPlayer(int posInitCardToShow);

    /**
     * Checks if the game is over.
     *
     * @return {@code true} if the game is over, otherwise {@code false}
     */
    Boolean isGameOver();
}
