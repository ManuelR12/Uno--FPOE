package org.example.eiscuno.model.deck;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private final Deck deck = new Deck();

    /**
     * Ensures JavaFX toolkit is initialized before tests run.
     */
    @BeforeAll
    static void initJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            Platform.startup(() -> {});
        }
    }

    /**
     * Test to verify that taking a card returns a valid Card object with a non-null value.
     */
    @Test
    void testTakeCard() {
        Card card = deck.takeCard();

        assertNotNull(card, "Card should not be null.");
        assertNotNull(card.getValue(), "Card value should not be null.");
        assertFalse(card.getValue().isEmpty(), "Card value should not be empty.");
    }

    /**
     * Test to verify that the initial card value is between 1 and 10 when taking a card for initialization.
     */
    @Test
    void testTakeCardInit() {
        Card card = deck.takeCardInit();

        assertNotNull(card, "Card should not be null.");
        assertDoesNotThrow(() -> Integer.parseInt(card.getValue()), "Card value should be a valid integer.");

        int value = Integer.parseInt(card.getValue());
        assertTrue(value >= 0 && value <= 9, "Initial card value should be between 0 and 9.");
    }

    /**
     * Test to verify that the deck's isEmpty method returns the correct boolean state.
     */
    @Test
    void testIsEmpty() {
        boolean isEmpty = deck.isEmpty();

        assertFalse(isEmpty, "Deck should not be empty at the start.");
        assertInstanceOf(Boolean.class, isEmpty, "isEmpty should return a Boolean.");
    }
}
