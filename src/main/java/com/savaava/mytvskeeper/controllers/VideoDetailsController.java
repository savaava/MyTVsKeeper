package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertConfirmation;
import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.utility.Converter;
import com.savaava.mytvskeeper.utility.FormatString;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoDetailsController implements Initializable {
    private VideoKeeper vk;

    private Video videoSelected;
    private int videoSelectedIndex;

    private ImagesCache imagesCache;

    @FXML
    public Label nameLbl, overviewLbl, genresLbl, rateLbl;

    @FXML
    public CheckBox startedCheck, terminatedCheck;
    @FXML
    public ChoiceBox<String> choiceBoxRating;
    @FXML
    public Button saveBtn;

    @FXML
    public ImageView videoImageView;

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

    public void setVideoSelected(Video v) { videoSelected = v; }
    public void setVideoSelectedIndex(int i){ videoSelectedIndex = i; }
    public void setImagesCache(ImagesCache imagesCache){ this.imagesCache = imagesCache; }

    private void setImage() {
        String pathImage = videoSelected.getPathImage();

        if(pathImage!=null) { /* Video has an image to search */
            if(imagesCache.containsImage(pathImage)) { /* The image has already been saved in cache */
                System.out.println("image already in cache -> "+imagesCache.getImageFromPath(pathImage));
                videoImageView.setImage(imagesCache.getImageFromPath(pathImage));
            }else{ /* The image has not been saved in cache yet */
                try {
                    Image videoImage = Converter.bytesToImage(TMDatabase.getBackdrop(pathImage));
                    videoImageView.setImage(videoImage);
                    System.out.println("image not in cache yet -> "+videoImage);
                    imagesCache.addImage(pathImage,videoImage);
                }catch(Exception ex){ new AlertError("Error getting video image from database","Error's details: "+ex.getMessage()); return; }
            }
            videoImageView.setFitWidth(800);
            videoImageView.setFitHeight(500);
        }else{ /* Video has no image to search */
            videoImageView.setFitWidth(200);
            videoImageView.setFitHeight(200);
        }

        /* initially is not visible to not show the default image before of the effectively one */
        videoImageView.setVisible(true);
    }
    private void initValues() {
        nameLbl.setText(FormatString.stringNormalize(videoSelected.getTitle()));

        if(videoSelected.getDescription().isEmpty())
            overviewLbl.setText("No overview found");
        else
            overviewLbl.setText(videoSelected.getDescription());

        startedCheck.setSelected(videoSelected.isStarted());
        terminatedCheck.setSelected(videoSelected.isTerminated());

        choiceBoxRating.setValue(videoSelected.getRating());

        StringBuilder genres = new StringBuilder();
        if(videoSelected instanceof Movie) {
            Movie movieSelected = (Movie)videoSelected;
            movieSelected.getGenres().forEach(gi -> genres.append(gi.getName()).append("  |  ") );
            if(! genres.isEmpty())
                genres.deleteCharAt(genres.length()-3);
            else
                genres.append("No genre found");
        }else if(videoSelected instanceof TVSerie){
            TVSerie tvSelected = (TVSerie)videoSelected;
            tvSelected.getGenres().forEach(gi -> genres.append(gi.getName()).append("  |  ") );
            if(! genres.isEmpty())
                genres.deleteCharAt(genres.length()-3);
            else
                genres.append("No genre found");
        }
        genresLbl.setText(genres.toString());

        if(videoSelectedIndex == 1){
            rateLbl.setText("Rate the Movie");
        }else if(videoSelectedIndex == 2) {
            rateLbl.setText("Rate the TV Serie");
        }else if(videoSelectedIndex == 3){
            rateLbl.setText("Rate the Anime");
        }

        Platform.runLater(() -> {
            setImage();
        });
    }


    private void checkBoxBinding() {
        BooleanBinding notStartedCond = Bindings.not(startedCheck.selectedProperty());

        terminatedCheck.disableProperty().bind(notStartedCond);

        notStartedCond.addListener((observable, oldValue, newValue) -> {
            if(newValue)
                terminatedCheck.setSelected(false);
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
        saveBtn.setDisable(true);

        startedCheck.selectedProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
        terminatedCheck.selectedProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
        choiceBoxRating.valueProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
    }
    private void onInputChange() {
        saveBtn.setDisable(
                startedCheck.isSelected() == videoSelected.isStarted() &&
                terminatedCheck.isSelected() == videoSelected.isTerminated() &&
                choiceBoxRating.getValue().equals(videoSelected.getRating())
        );
    }


    @FXML
    public void onSave() {
        if(videoSelected instanceof Movie) { /* videoIndex = 1 */

            Movie movieSelected = (Movie)videoSelected;
            Movie movieToAdd = new Movie(
                    movieSelected.getTitle(),
                    movieSelected.getDescription(),
                    movieSelected.getReleaseDate(),
                    startedCheck.isSelected(),
                    terminatedCheck.isSelected(),
                    choiceBoxRating.getValue(),
                    movieSelected.getId(),
                    movieSelected.getPathImage(),
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

        }else if(videoSelected instanceof TVSerie) { /* videoIndex = 2 | 3 */

            TVSerie tvSelected = (TVSerie)videoSelected;
            TVSerie tvToAdd = new TVSerie(
                    tvSelected.getTitle(),
                    tvSelected.getDescription(),
                    tvSelected.getReleaseDate(),
                    startedCheck.isSelected(),
                    terminatedCheck.isSelected(),
                    choiceBoxRating.getValue(),
                    tvSelected.getId(),
                    tvSelected.getPathImage(),
                    tvSelected.getNumSeasons(),
                    tvSelected.getNumEpisodes()
            );
            tvSelected.getGenres().forEach(gi -> tvToAdd.addGenre(gi.getId()) );

            try{
                if(videoSelectedIndex == 2) {
                    vk.removeTVSerie(tvSelected.getId());
                    vk.addTVSerie(tvToAdd);
                }else if(videoSelectedIndex == 3){
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
    public void handleEscPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ESCAPE){
            if(! saveBtn.isDisable()){
                if(! new AlertConfirmation("Sure to exit from the Video Details ?","There are changes will not be saved").getResultConfirmation())
                    return;
            }
            onExit();
        }
    }

    public void onExit() {
        Stage stage = (Stage)nameLbl.getScene().getWindow();
        stage.close();
    }
}
