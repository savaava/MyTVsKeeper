package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;
import com.savaava.mytvskeeper.models.TMDatabase;
import com.savaava.mytvskeeper.models.Video;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML
    public TableColumn<Video,String>
            titleColumn,
            dateColumn,
            descriptionColumn;
    @FXML
    public Button searchBtn, insertBtn;

    private int videoToAdd;
    private StringProperty str = new SimpleStringProperty("");
    private ObservableList<Video> list;
    private TMDatabase tmdb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            tmdb = TMDatabase.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ConfigNotExistsException e) {
            throw new RuntimeException(e);
        }

        list = FXCollections.observableArrayList();

        table.setItems(list);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        titleColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        table.setRowFactory(video -> new TableRow<>() {
            @Override
            protected void updateItem(Video video, boolean empty) {
                super.updateItem(video, empty);

                if (empty || video == null) {
                    setPrefHeight(24);
                } else {
                    int titleLines = video.getTitle()!=null ? video.getTitle().split("\n").length : 0;
                    int descriptionLines = video.getDescription()!=null ? video.getDescription().split("\n").length : 0;
                    int maxLines = Math.max(titleLines, descriptionLines);
                    if(maxLines == 2)
                        setPrefHeight(maxLines * 24);
                    else
                        setPrefHeight(maxLines * 20);
                }
            }
        });


        try {
            list.setAll(tmdb.getMoviesByName("Harry Potter"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        BooleanBinding searchDisableCond = tfd.textProperty().isEmpty().or(tfd.textProperty().isEqualTo(str));
        searchBtn.disableProperty().bind(searchDisableCond);
    }


    public void setVideoToAdd(int videoToAdd){
        this.videoToAdd = videoToAdd;
    }
}
