package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a card in the Uno game.
 *
 * <p>Each card has an image, a value, and a color, and it can be visually represented
 * using an {@link ImageView}.
 */
public class Card {
    private String url;
    private String value;
    private String color;
    private Image image;
    private ImageView cardImageView;

    /**
     * Constructs a Card with the specified image URL, value, and color.
     *
     * @param url   the URL of the card image
     * @param value the value of the card (e.g., number or special action)
     * @param color the color of the card (e.g., red, blue, green, yellow)
     */
    public Card(String url, String value, String color) {
        this.url = url;
        this.value = value;
        this.color = color;
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }

    /**
     * Creates and configures the ImageView for the card.
     *
     * @return the configured ImageView of the card
     */
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(90);
        card.setFitWidth(70);
        return card;
    }

    /**
     * Gets the ImageView representation of the card.
     *
     * @return the ImageView of the card
     */
    public ImageView getCard() {
        return cardImageView;
    }

    /**
     * Gets the image of the card.
     *
     * @return the Image of the card
     */
    public Image getImage() {
        return image;
    }

    /**
     * Gets the value of the card.
     *
     * @return the value of the card
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the color of the card.
     *
     * @return the color of the card
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the card.
     *
     * @param color the new color of the card
     */
    public void setColor(String color) {
        this.color = color;
    }
}
