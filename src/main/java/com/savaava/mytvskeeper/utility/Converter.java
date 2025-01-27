package com.savaava.mytvskeeper.utility;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Converter {
    /**
     * @brief Converts the byte array into his corresponding Image
     * @param bytesImage are the bytes of an image, for example once taken from TMDB
     * @return the Image corresponding the bytes as the JavaFx Object: Image, useful for ImageView,
     * null if the method captures an IOException converting bytes to Image
     * @see com.savaava.mytvskeeper.models.TMDatabase getBackdrop
     */
    public static Image bytesToImage(byte[] bytesImage) {
        if (bytesImage == null || bytesImage.length == 0) {
            return null;
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytesImage)) {
            return new Image(bais);
        } catch (IOException ex) {
            return null;
        }
    }
}
