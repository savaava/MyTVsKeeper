package com.savaava.mytvskeeper.utility;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Converter {
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
