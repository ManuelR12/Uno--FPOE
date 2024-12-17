package org.example.eiscuno.model.card;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.deck.Deck;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
     * Test to verify that the card's graphical representation is an ImageView.
     */
    @Test
    void testGetCard() {
        assertInstanceOf(ImageView.class, card.getCard(), "Card should return an ImageView.");
    }

    /**
     * Test to verify that the card's image is of type Image.
     */
    @Test
    void testGetImage() {
        assertInstanceOf(Image.class, card.getImage(), "Card image should be an instance of Image.");
    }

    /**
     * Test to verify the card's value.
     */
    @Test
    void testGetValue() {
        assertEquals("0", card.getValue(), "Card value should match the expected value.");
    }

    /**
     * Test to verify the card's color.
     */
    @Test
    void testGetColor() {
        assertEquals("BLUE", card.getColor(), "Card color should match the expected color.");
    }

    /**
     * Test to verify that the card's color can be updated correctly.
     */
    @Test
    void testSetColor() {
        String newColor = getRandomColor();
        card.setColor(newColor);
        assertEquals(newColor, card.getColor(), "Card color should be updated to the new color.");
    }

    /**
     * Returns a random color from the available COLORS array.
     */
    private String getRandomColor() {
        Random random = new Random();
        return COLORS[random.nextInt(COLORS.length)];
    }
}
