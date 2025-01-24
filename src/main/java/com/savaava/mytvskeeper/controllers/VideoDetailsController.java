package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.models.Video;

import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoDetailsController implements Initializable {
    private Video videoSelected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            System.out.println(videoSelected);
        });
    }

    public void setVideoSelected(Video v) {
        videoSelected = v;
    }
}
