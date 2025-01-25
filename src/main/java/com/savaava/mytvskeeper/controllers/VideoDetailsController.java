package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.Movie;
import com.savaava.mytvskeeper.models.TVSerie;
import com.savaava.mytvskeeper.models.Video;

import com.savaava.mytvskeeper.models.VideoKeeper;
import com.savaava.mytvskeeper.utility.FormatString;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoDetailsController implements Initializable {
    private VideoKeeper vk;

    private Video videoSelected;
    private int videoSelectedIndex;

    @FXML
    public Label nameLbl, overviewLbl, genresLbl, rateLbl;

    @FXML
    public CheckBox startedCheck, terminatedCheck;
    @FXML
    public ChoiceBox<String> choiceBoxRating;
    @FXML
    public Button confirmBtn, deleteBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                vk = VideoKeeper.getInstance();
            } catch (Exception ex) {
                new AlertError("Error reading saving data files", "Error's details: " + ex.getMessage());
                onExit();
            }

            initValues();

            checkBoxBinding();

            initChoiceBoxRating();

            confirmBinding();
        });
    }

    public void setVideoSelected(Video v) {
        videoSelected = v;
    }
    public void setVideoSelectedIndex(int i){ videoSelectedIndex = i; }

    private void initValues() {
        System.out.println(videoSelected);

        nameLbl.setText(FormatString.compactString(videoSelected.getTitle(),30));

        if(videoSelected.getDescription().isEmpty())
            overviewLbl.setText("No overview detected");
        else
            overviewLbl.setText(FormatString.compactString(videoSelected.getDescription(),110));

        startedCheck.setSelected(videoSelected.isStarted());
        terminatedCheck.setSelected(videoSelected.isTerminated());

        choiceBoxRating.setValue(videoSelected.getRating());

        StringBuilder genres = new StringBuilder();
        if(videoSelected instanceof Movie) {
            Movie movieSelected = (Movie)videoSelected;
            movieSelected.getGenres().forEach(gi -> genres.append(gi.getName()).append("\n") );
        }else if(videoSelected instanceof TVSerie){
            TVSerie tvSelected = (TVSerie)videoSelected;
            tvSelected.getGenres().forEach(gi -> genres.append(gi.getName()).append("\n") );
        }else{
            genres.append("No genres detected");
        }
        genresLbl.setText(genres.toString());

        if(videoSelectedIndex == 1){
            deleteBtn.setText("Delete Movie");
            rateLbl.setText("Rate the Movie");
        }else if(videoSelectedIndex == 2) {
            deleteBtn.setText("Delete TV Serie");
            rateLbl.setText("Rate the TV Serie");
        }else if(videoSelectedIndex == 3){
            deleteBtn.setText("Delete Anime");
            rateLbl.setText("Rate the Anime");
        }
    }

    private void checkBoxBinding() {
        BooleanBinding notStartedCond = Bindings.not(startedCheck.selectedProperty());
        terminatedCheck.disableProperty().bind(notStartedCond);
        notStartedCond.addListener((observable, oldValue, newValue) -> {
            if(newValue){ terminatedCheck.setSelected(false); }
        });
    }

    private void initChoiceBoxRating() {
        String star = "🌟";
        choiceBoxRating.getItems().setAll(
                "",
                "1 "+star,
                "2 "+star,
                "3 "+star,
                "4 "+star,
                "5 "+star,
                "6 "+star,
                "7 "+star,
                "8 "+star,
                "9 "+star,
                star+" 10 "+star);
    }

    private void confirmBinding() {
        confirmBtn.setDisable(true);

        startedCheck.selectedProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
        terminatedCheck.selectedProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
        choiceBoxRating.valueProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
    }
    private void onInputChange() {
        confirmBtn.setDisable(
                startedCheck.isSelected() == videoSelected.isStarted() &&
                terminatedCheck.isSelected() == videoSelected.isTerminated() &&
                choiceBoxRating.getValue().equals(videoSelected.getRating())
        );
    }

    @FXML
    public void onDeleteVideo() {

    }

    @FXML
    public void onConfirm() {
        if(videoSelected instanceof Movie) {

            Movie movieSelected = (Movie)videoSelected;
            Movie movieToAdd = new Movie(
                    movieSelected.getTitle(),
                    movieSelected.getDescription(),
                    movieSelected.getReleaseDate(),
                    startedCheck.isSelected(),
                    terminatedCheck.isSelected(),
                    choiceBoxRating.getValue(),
                    movieSelected.getId(),
                    movieSelected.getDuration(),
                    movieSelected.getDirector()
            );
            movieSelected.getGenres().forEach(gi -> movieToAdd.addGenre(gi.getId()) );

            try {
                vk.removeMovie(movieSelected.getId());
                vk.addMovie(movieToAdd);
            }catch(Exception ex) {
                new AlertError("Error saving changes","Error's details: "+ex.getMessage());
            }

        }else if(videoSelected instanceof TVSerie) {

            TVSerie tvSelected = (TVSerie)videoSelected;
            TVSerie tvToAdd = new TVSerie(
                    tvSelected.getTitle(),
                    tvSelected.getDescription(),
                    tvSelected.getReleaseDate(),
                    startedCheck.isSelected(),
                    terminatedCheck.isSelected(),
                    choiceBoxRating.getValue(),
                    tvSelected.getId(),
                    tvSelected.getNumSeasons(),
                    tvSelected.getNumEpisodes()
            );
            tvSelected.getGenres().forEach(gi -> tvToAdd.addGenre(gi.getId()) );

            try{
                if(videoSelectedIndex==2) {
                    vk.removeTVSerie(tvSelected.getId());
                    vk.addTVSerie(tvToAdd);
                }else{
                    vk.removeAnimeSerie(tvSelected.getId());
                    vk.addAnimeSerie(tvToAdd);
                }
            } catch (Exception ex) {
                new AlertError("Error saving changes","Error's details: "+ex.getMessage());
            }

        }

        onExit();
    }

    @FXML
    public void onExit() {
        Stage stage = (Stage)nameLbl.getScene().getWindow();
        stage.close();
    }
}
