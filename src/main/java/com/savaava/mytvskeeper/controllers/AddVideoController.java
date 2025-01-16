package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertWarning;
import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;
import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;
import com.savaava.mytvskeeper.models.*;
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

    private int videoIndex;
    private StringProperty str = new SimpleStringProperty("");
    private ObservableList<Video> list;
    private TMDatabase tmdb;
    private VideoKeeper vk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            vk = VideoKeeper.getInstance();
        }catch(Exception ex){
            new AlertError("Error reading saving data files","Error's details: "+ex.getMessage());
            return ;
        }

        try {
            tmdb = TMDatabase.getInstance();
        } catch(ConfigNotExistsException ex) {
            new AlertError("Config file doesn't Exists !","Please configure the application");
            return ;
        } catch(IOException ex) {
            new AlertError("Error reading config file","Error's details: "+ex.getMessage());
            return ;
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
                    setPrefHeight(26);
                } else {
                    int titleLines = video.getTitle()!=null ? video.getTitle().split("\n").length : 0;
                    int descriptionLines = video.getDescription()!=null ? video.getDescription().split("\n").length : 0;
                    int maxLines = Math.max(titleLines, descriptionLines);
                    if(maxLines == 2)
                        setPrefHeight(maxLines * 26);
                    else
                        setPrefHeight(maxLines * 20);
                }
            }
        });

        BooleanBinding searchDisableCond = tfd.textProperty().isEmpty().or(tfd.textProperty().isEqualTo(str));
        searchBtn.disableProperty().bind(searchDisableCond);

        BooleanBinding insertDisableCond = table.getSelectionModel().selectedItemProperty().isNull();
        insertBtn.disableProperty().bind(insertDisableCond);
    }


    public void setVideoToAdd(int videoIndex){
        this.videoIndex = videoIndex;
    }

    @FXML
    public void onSearch() {
        String nameVideo = tfd.getText();
        str.setValue(nameVideo);

        try{
            if(videoIndex == 1)
                list.setAll(tmdb.getMoviesByName(nameVideo));
            else
                list.setAll(tmdb.getTVSeriesByName(nameVideo));
        }catch(IOException | InterruptedException ex){
            new AlertError("Error searching video in TMDB","Error's details: "+ex.getMessage());
        }
    }

    @FXML
    public void onInsert() {
        Video videoToAdd = table.getSelectionModel().getSelectedItem();

        if(videoIndex == 1){
            Movie movieToAdd;

            try{ movieToAdd = tmdb.getMovieById(videoToAdd.getId()); }
            catch(IOException | InterruptedException ex){
                new AlertError("Error searching Movie in TMDB","Error's details: "+ex.getMessage());
                return;
            }

            try{ vk.addMovie(movieToAdd); }
            catch(VideoAlreadyExistsException ex) {
                new AlertWarning("Movie already exists","The selected movie already exists in your movie list");
            }catch(IOException ex) {new AlertError("Error saving data","Error saving The selected Movie - Error's details: "+ex.getMessage());}

        }else if(videoIndex == 2){
            TVSerie tvToAdd;

            try{ tvToAdd = tmdb.getTVSerieById(videoToAdd.getId()); }
            catch(IOException | InterruptedException ex){
                new AlertError("Error searching TV Serie in TMDB","Error's details: "+ex.getMessage());
                return;
            }

            try{ vk.addTVSerie(tvToAdd); }
            catch(VideoAlreadyExistsException ex) {
                new AlertWarning("TV Serie already exists","The selected TV Serie already exists in your TV Serie list");
            }catch(IOException ex) {new AlertError("Error saving data","Error saving The selected TV Serie - Error's details: "+ex.getMessage());}

        }else{
            TVSerie animeToAdd;

            try{ animeToAdd = tmdb.getTVSerieById(videoToAdd.getId()); }
            catch(IOException | InterruptedException ex){
                new AlertError("Error searching Anime in TMDB","Error's details: "+ex.getMessage());
                return;
            }

            try{ vk.addAnimeSerie(animeToAdd); }
            catch(VideoAlreadyExistsException ex) {
                new AlertWarning("Anime already exists","The selected Anime already exists in your Anime list");
            }catch(IOException ex) {new AlertError("Error saving data","Error saving The selected Anime - Error's details: "+ex.getMessage());}

        }
    }
}
