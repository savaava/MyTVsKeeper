package com.savaava.mytvskeeper.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoDetailsAddController implements Initializable {
    private String title;
    private Image videoImage;

    @FXML
    public Label titleLbl;
    @FXML
    public ImageView videoImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            initValues();
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setVideoImage(Image i) {
        videoImage = i;
    }

    private void initValues() {
        if(title != null) {
            titleLbl.setText(title);
        }

        if(videoImage == null) {
            /* Reaching this block means there's an error because this controller and
            his scene doesn't start if there is no image to show, thanks to the controls made in AddVideoController */
            videoImageView.setImage(new Image("/images/noImageFound.png"));
        }else{
            videoImageView.setImage(videoImage);
            videoImageView.setFitWidth(800);
            videoImageView.setFitHeight(500);
        }
    }

    private void onExit() {
        Stage stage = (Stage)titleLbl.getScene().getWindow();
        stage.close();
    }
}
