package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.TMDatabase;
import com.savaava.mytvskeeper.utility.Converter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VideoDetailsAddController implements Initializable {
    private String title;
    private String pathImage;
    private TMDatabase tmdb;

    @FXML
    public Label titleLbl;
    @FXML
    public ImageView videoImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                tmdb = TMDatabase.getInstance();
            } catch (IOException ex) {
                new AlertError("Error reading config file", "Error's details: " + ex.getMessage());
                onExit();
            }

            initValues();
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    private void initValues() {
        if(title != null) {
            titleLbl.setText(title);
        }

        if(pathImage == null) {
            /* Reaching this block means there's an error because this controller and
            his scene doesn't start if there is no image to show, thanks to the controls made in AddVideoController */
            return;
        }

        Image image = null;
        try {
            image = Converter.bytesToImage(tmdb.getBackdrop(pathImage));
        } catch (IOException | InterruptedException ex) {
            /* Reaching this block means an exception captured from tmdb.getBackdrop and not in bytesToImage */
            new AlertError("Error searching video's image", "Check the connection\nError's details: " + ex.getMessage());
        }

        if (image != null) {
            videoImage.setImage(image);
            videoImage.setFitWidth(800);
            videoImage.setFitHeight(500);
        }else{
            videoImage.setFitWidth(200);
            videoImage.setFitHeight(200);
        }
    }

    private void onExit() {
        Stage stage = (Stage)titleLbl.getScene().getWindow();
        stage.close();
    }
}
