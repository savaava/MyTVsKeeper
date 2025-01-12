package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    TableView<Movie> tableViewMovies;
    @FXML
    public TableColumn<Movie, String>
            titleColumnMovie,
            durationColumnMovie,
            releaseDateColumnMovie,
            directorColumnMovie;
    @FXML
    public TableColumn<Movie, CheckBox> startedColumnMovie, terminatedColumnMovie;
    @FXML
    public TableColumn<Movie, ChoiceBox<String>> ratingColumnMovie;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File dir = new File("bin\\");
        if(!dir.exists() || !dir.isDirectory()) {
            if(!dir.mkdir()){
                new AlertError("Failed bin","Failed to create bin directory in current directory");
            }
        }
        tableViewMovies.setVisible(true);
    }

    @FXML
    public void onExport() {

    }

    @FXML
    public void onConfig() {

    }

    @FXML
    public void onExit() {

    }

    @FXML
    public void onAbout() {

    }

    @FXML
    public void onMovies() {
        tableViewMovies.setVisible(true);
    }

    @FXML
    public void onTVSeries() {
        tableViewMovies.setVisible(false);
    }

    @FXML
    public void onAnimeSeries() {
        tableViewMovies.setVisible(false);
    }
}
