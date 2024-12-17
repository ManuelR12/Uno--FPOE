package org.example.eiscuno.model.card;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.deck.Deck;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Card} class.
 *
 * <p>Contains unit tests to verify the functionality of the Card class,
 * including its graphical representation, image handling, value, and color management.
 */
class CardTest {
    private static final String[] COLORS = {"GREEN", "YELLOW", "BLUE", "RED", "WILD"};
    private final Deck deck = new Deck();
    private final Card card = new Card("/org/example/eiscuno/cards-uno/0_blue.png", "0", "BLUE");

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
     * Tests that the card's graphical representation is an instance of {@link ImageView}.
     */
    @Test
    void testGetCard() {
        assertInstanceOf(ImageView.class, card.getCard(), "Card should return an ImageView.");
    }

    /**
     * Tests that the card's image is an instance of {@link Image}.
     */
    @Test
    void testGetImage() {
        assertInstanceOf(Image.class, card.getImage(), "Card image should be an instance of Image.");
    }

    /**
     * Tests that the card's value matches the expected value.
     */
    @Test
    void testGetValue() {
        assertEquals("0", card.getValue(), "Card value should match the expected value.");
    }

    /**
     * Tests that the card's color matches the expected color.
     */
    @Test
    void testGetColor() {
        assertEquals("BLUE", card.getColor(), "Card color should match the expected color.");
    }

    /**
     * Tests that the card's color can be updated correctly using {@link Card#setColor(String)}.
     */
    @Test
    void testSetColor() {
        String newColor = getRandomColor();
        card.setColor(newColor);
        assertEquals(newColor, card.getColor(), "Card color should be updated to the new color.");
    }

    /**
     * Returns a random color from the available COLORS array.
     *
     * @return a randomly selected color
     */
    private String getRandomColor() {
        Random random = new Random();
        return COLORS[random.nextInt(COLORS.length)];
    }
}
