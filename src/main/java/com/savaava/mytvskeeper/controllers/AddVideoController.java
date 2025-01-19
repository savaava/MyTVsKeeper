package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertWarning;
import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;
import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;
import com.savaava.mytvskeeper.models.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;

import java.io.IOException;

import java.util.ResourceBundle;

public class AddVideoController implements Initializable {
    private int videoIndex;
    private StringProperty strBinding = new SimpleStringProperty("");
    private ObservableList<Video> list;
    private TMDatabase tmdb;
    private VideoKeeper vk;

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
    public Button searchBtn, insertBtn, cancelBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* method runLater to be able to exit in cathes, otherwise (Stage)table.getScene() would be null, and
        * because the stage is not initialized */
        Platform.runLater(() -> {
            try {
                vk = VideoKeeper.getInstance();
            } catch (Exception ex) {
                new AlertError("Error reading saving data files", "Error's details: " + ex.getMessage());
                onExit();
            }

            try {
                tmdb = TMDatabase.getInstance();
            } catch (ConfigNotExistsException ex) {
                new AlertError("Config file doesn't Exists !", "Please configure the application in File -> Configure application");
                onExit();
            } catch (IOException ex) {
                new AlertError("Error reading config file", "Error's details: " + ex.getMessage());
                onExit();
            }

            list = FXCollections.observableArrayList();

            initTable();

            BooleanBinding searchDisableCond = tfd.textProperty().isEmpty().or(tfd.textProperty().isEqualTo(strBinding));
            searchBtn.disableProperty().bind(searchDisableCond);

            BooleanBinding insertDisableCond = table.getSelectionModel().selectedItemProperty().isNull();
            insertBtn.disableProperty().bind(insertDisableCond);

            cancelBtn.setStyle("-fx-background-color: #ff7a7a");
        });
    }

    private void initTable() {
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
                    int titleLines = video.getTitle().split("\n").length;
                    int descriptionLines = video.getDescription().split("\n").length;
                    int maxLines = Math.max(titleLines, descriptionLines);
                    if(maxLines == 2)
                        setPrefHeight(maxLines * 26);
                    else
                        setPrefHeight(maxLines * 20);
                }
            }
        });
    }

    public void setVideoToAdd(int videoIndex){
        this.videoIndex = videoIndex;
    }

    @FXML
    public void onSearch() {
        String nameVideo = tfd.getText();
        strBinding.setValue(nameVideo);

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
        boolean flagAlreadyExists = false;

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
                flagAlreadyExists = true;
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
                flagAlreadyExists = true;
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
                flagAlreadyExists = true;
            }catch(IOException ex) {new AlertError("Error saving data","Error saving The selected Anime - Error's details: "+ex.getMessage());}

        }

        if(!flagAlreadyExists)
            onExit();
    }

    @FXML
    public void onExit() {
        Stage stage = (Stage)table.getScene().getWindow();
        stage.close();
    }
}
