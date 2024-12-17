package org.example.eiscuno.model.table;

import javafx.application.Platform;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Table} class.
 *
 * <p>Contains unit tests to verify the functionality of the Table class, including adding
 * cards, retrieving the current card, initializing the first card, and updating the current card color.
 */
class TableTest {
    private final Table table = new Table();
    private final Deck deck = new Deck();
    private static final String[] COLORS = {"GREEN", "YELLOW", "BLUE", "RED", "WILD"};

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
     * Tests that a card can be added to the table and becomes the current card.
     */
    @Test
    void testAddCardOnTheTable() {
        Card card = deck.takeCardInit();
        table.addCardOnTheTable(card);

        assertNotNull(table.getCurrentCardOnTheTable(), "Current card on the table should not be null.");
        assertEquals(card.getValue(), table.getCurrentCardOnTheTable().getValue(),
                "The value of the current card on the table should match the added card.");
    }

    /**
     * Tests that the current card on the table updates correctly when a new card is added.
     */
    @Test
    void testGetCurrentCardOnTheTable() {
        Card firstCard = deck.takeCardInit();
        Card secondCard = deck.takeCardInit();

        table.addCardOnTheTable(firstCard);
        table.addCardOnTheTable(secondCard);

        assertNotNull(table.getCurrentCardOnTheTable(), "Current card on the table should not be null.");
        assertEquals(secondCard.getValue(), table.getCurrentCardOnTheTable().getValue(),
                "The value of the current card on the table should match the last added card.");
    }

    /**
     * Tests that the first card placed on the table is within a valid numeric range.
     */
    @Test
    void testFirstCard() {
        Card card = table.firstCard(deck);

        assertNotNull(card, "First card on the table should not be null.");
        assertDoesNotThrow(() -> Integer.parseInt(card.getValue()), "Card value should be a valid integer.");
        int value = Integer.parseInt(card.getValue());
        assertTrue(value >= 0 && value <= 9, "First card value should be between 0 and 9.");
    }

    /**
     * Tests that the current card's color on the table can be updated.
     */
    @Test
    void testSetCurrentCardColor() {
        String randomColor = getRandomColor();
        Card card = deck.takeCardInit();

        table.addCardOnTheTable(card);
        table.setCurrentCardColor(randomColor);

        assertNotNull(table.getCurrentCardOnTheTable(), "Current card on the table should not be null.");
        assertEquals(randomColor, table.getCurrentCardOnTheTable().getColor(),
                "The current card color on the table should match the set color.");
    }

    /**
     * Helper method to get a random color from the predefined COLORS array.
     *
     * @return a randomly selected color string
     */
    private String getRandomColor() {
        Random random = new Random();
        return COLORS[random.nextInt(COLORS.length)];
    }
}
