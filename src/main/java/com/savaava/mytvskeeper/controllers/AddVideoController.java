package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;
import com.savaava.mytvskeeper.models.Movie;
import com.savaava.mytvskeeper.models.TMDatabase;
import com.savaava.mytvskeeper.models.Video;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddVideoController implements Initializable {
    @FXML
    public Label lbl;
    @FXML
    public TextField tfd;
    @FXML
    public TableView<Video> table;

    private int videoToAdd;
    private ObservableList<Video> list;
    private TMDatabase tmdb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(videoToAdd == 1)
            lbl.setText("Search a Movie");
        else if(videoToAdd == 2)
            lbl.setText("Search a TV Serie");
        else
            lbl.setText("Search a Anime Serie");

        try {
            tmdb = TMDatabase.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ConfigNotExistsException e) {
            throw new RuntimeException(e);
        }
        list = FXCollections.observableArrayList();
        table.setItems(list);

//        try {
//            list.setAll(tmdb.getMoviesByName("Harry Potter"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }


    public void setVideoToAdd(int videoToAdd){
        this.videoToAdd = videoToAdd;
    }
}
