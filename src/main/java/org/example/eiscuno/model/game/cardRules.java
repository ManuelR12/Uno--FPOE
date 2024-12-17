package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;

/**
 * Manages the rules and effects of special cards in the Uno game.
 *
 * <p>This class handles the execution of actions triggered by special cards
 * such as REVERSE, SKIP, +2, and +4.
 */
public class cardRules {

    private GameUno game;
    private boolean playerAte = false;

    /**
     * Constructs a CardRules instance linked to the current game.
     *
     * @param game the current Uno game instance
     */
    public cardRules(GameUno game) {
        this.game = game;
    }

    /**
     * Executes the effect of a special card.
     *
     * @param card       the special card to process
     * @param nextPlayer the player affected by the card's effect
     */
    public void applySpecialCardEffect(Card card, Player nextPlayer) {
        switch (card.getValue()) {
            case "REVERSE", "SKIP":
                applyReverseOrSkip(nextPlayer);
                break;
            case "+2":
                applyPlusTwo(nextPlayer);
                break;
            case "+4":
                applyPlusFour(nextPlayer);
                break;
            default:
                break;
        }
    }

    /**
     * Applies the effect of a REVERSE or SKIP card.
     *
     * @param nextPlayer the player affected by the card
     */
    private void applyReverseOrSkip(Player nextPlayer) {
        System.out.println("Turn order reversed or turn skipped!");
    }

    /**
     * Applies the effect of a +2 card.
     *
     * @param nextPlayer the player who must draw 2 cards
     * @return {@code true} if the player drew cards
     */
    private boolean applyPlusTwo(Player nextPlayer) {
        game.eatCard(nextPlayer, 2);
        System.out.println(nextPlayer.getTypePlayer() + " draws 2 cards!");
        return playerAte = true;
    }

    /**
     * Checks if the player has drawn cards as a result of a special card.
     *
     * @return {@code true} if the player drew cards, {@code false} otherwise
     */
    public boolean getPLayerAte() {
        return playerAte;
    }

    /**
     * Applies the effect of a +4 card.
     *
     * @param nextPlayer the player who must draw 4 cards
     * @return {@code true} if the player drew cards
     */
    private boolean applyPlusFour(Player nextPlayer) {
        game.eatCard(nextPlayer, 4);
        System.out.println(nextPlayer.getTypePlayer() + " draws 4 cards!");
        return playerAte = true;
    }

    /**
     * Sets the status indicating whether a player has drawn cards.
     *
     * @param playerAte {@code true} if the player drew cards, {@code false} otherwise
     */
    public void setPlayerAte(boolean playerAte) {
        this.playerAte = playerAte;
    }
}
