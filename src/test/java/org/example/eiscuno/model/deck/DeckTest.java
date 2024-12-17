package org.example.eiscuno.model.deck;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Deck} class.
 *
 * <p>Contains unit tests to verify the functionality of the Deck class, including
 * card retrieval, initialization, and the deck's empty state validation.
 */
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
     * Tests that taking a card from the deck returns a valid {@link Card} object.
     *
     * <p>The card should have a non-null and non-empty value.
     */
    @Test
    void testTakeCard() {
        Card card = deck.takeCard();

        assertNotNull(card, "Card should not be null.");
        assertNotNull(card.getValue(), "Card value should not be null.");
        assertFalse(card.getValue().isEmpty(), "Card value should not be empty.");
    }

    /**
     * Tests that the initial card taken for game setup has a value between 0 and 9.
     *
     * <p>This verifies that the initial card is a numeric card.
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
     * Tests the {@link Deck#isEmpty()} method to ensure it correctly indicates
     * whether the deck contains cards.
     */
    @Test
    void testIsEmpty() {
        boolean isEmpty = deck.isEmpty();

        assertFalse(isEmpty, "Deck should not be empty at the start.");
        assertInstanceOf(Boolean.class, isEmpty, "isEmpty should return a Boolean.");
    }
}
