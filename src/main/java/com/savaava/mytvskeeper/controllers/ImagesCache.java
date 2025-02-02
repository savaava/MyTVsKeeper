package com.savaava.mytvskeeper.controllers;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImagesCache {
    private final Map<String, Image> imagesCache;

    public ImagesCache(){
        imagesCache = new HashMap<>();
    }

    public void addImage(String pathImage, Image image) {
        imagesCache.putIfAbsent(pathImage, image);
    }

    /**
     * @return null if there's no image corresponding to pathImage
     */
    public Image getImageFromPath(String pathImage) {
        return imagesCache.get(pathImage);
    }

    public boolean containsImage(String pathImage) {
        return imagesCache.containsKey(pathImage);
    }

    public void deleteAllImages() {
        imagesCache.clear();
    }

    @Override
    public String toString() {
        StringBuffer strb = new StringBuffer();

        return strb.toString();
    }
}
