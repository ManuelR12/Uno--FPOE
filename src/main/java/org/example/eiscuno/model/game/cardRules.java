package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.player.Player;

public class cardRules {

    private GameUno game;
    private boolean playerAte = false;

    /**
     * Constructs a CardRules instance linked to the current game.
     *
     * @param game The current Uno game instance.
     */
    public cardRules(GameUno game) {
        this.game = game;
    }

    /**
     * Executes the effect of a special card.
     *
     * @param card   The card to process.
     * @param nextPlayer The player who played the card.
     */
    public void applySpecialCardEffect(Card card, Player nextPlayer) {
        switch (card.getValue()) {
            case "REVERSE":
                applyReverse();
                break;
            case "SKIP":
                applySkip();
                break;
            case "+2":
                applyPlusTwo(nextPlayer);
                break;
            case "WILD":
                applyWild(card);
                break;
            case "+4":
                applyPlusFour(nextPlayer);
                break;
            default:
                break;
        }
    }

    private void applyReverse() {
        System.out.println("Turn order reversed!");
        // TODO: Add logic to reverse turn order in GameUno.
    }

    private void applySkip() {
        System.out.println("Next player's turn skipped!");
    }

    private boolean applyPlusTwo(Player nextPlayer) {
        game.eatCard(nextPlayer, 2);
        System.out.println(nextPlayer.getTypePlayer() + " draws 2 cards!");
        return playerAte = true;

    }

    public boolean getPLayerAte() {
        return playerAte;
    }

    private boolean applyPlusFour(Player nextPlayer) {
        game.eatCard(nextPlayer, 4);
        System.out.println(nextPlayer.getTypePlayer() + " draws 4 cards!");
        return playerAte = true;
    }

    public void setPlayerAte(boolean playerAte) {
        this.playerAte = playerAte;
    }

    private void applyWild(Card card) {
        System.out.println("Wild card played! Color changed to: ");
    }


}