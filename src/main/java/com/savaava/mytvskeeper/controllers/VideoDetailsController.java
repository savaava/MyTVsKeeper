package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertConfirmation;
import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.services.TMDatabase;
import com.savaava.mytvskeeper.utility.Converter;
import com.savaava.mytvskeeper.utility.FormatString;

import javafx.animation.ScaleTransition;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoDetailsController implements Initializable {
    private VideoKeeper vk;

    private Video videoSelected;
    private int videoSelectedIndex;

    private ImagesCache imagesCache;

    private final String star = "🌟";

    @FXML
    public Label nameLbl, releaseDateLbl, durationLbl,directorAboveLbl, directorLbl, overviewLbl, genresLbl, ratingValueLbl;

    @FXML
    public CheckBox startedCheck, terminatedCheck;
    @FXML
    public ChoiceBox<String> choiceBoxRating;
    @FXML
    public TextArea notesTextArea;

    @FXML
    public Button saveBtn, decreaseRatingBtn, increaseRatingBtn;

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

            choiceBoxRating.setOnAction(e -> ratingValueLbl.setText(choiceBoxRating.getValue()));

            confirmBinding();
            checkBoxBinding();
            decreaseBinding();
            increaseBinding();

//            resizeScene();

            checkBoxBinding();
        });
    }

    public void setVideoSelected(Video v) { videoSelected = v; }
    public void setVideoSelectedIndex(int i){ videoSelectedIndex = i; }
    public void setImagesCache(ImagesCache imagesCache){ this.imagesCache = imagesCache; }

    private void setImage() {
        String pathImage = videoSelected.getPathImage();

        if(pathImage!=null) { /* Video has an image to search */
            if(imagesCache.containsImage(pathImage)) { /* The image has already been saved in cache */
//                System.out.println("image already in cache -> "+imagesCache.getImageFromPath(pathImage));
                videoImageView.setImage(imagesCache.getImageFromPath(pathImage));
            }else{ /* The image has not been saved in cache yet */
                try {
                    Image videoImage = Converter.bytesToImage(TMDatabase.getBackdrop(pathImage));
                    videoImageView.setImage(videoImage);
//                    System.out.println("image not in cache yet -> "+videoImage);
                    imagesCache.addImage(pathImage,videoImage);
                }catch(Exception ex){ new AlertError("Error getting video image from database","Error's details: "+ex.getMessage()); return; }
            }
            videoImageView.setFitWidth(800);
            videoImageView.setFitHeight(500);
            videoImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 25, 0.3, 0, 5);");

            /* initially is not visible to not show the default image before of the effectively one */
            videoImageView.setVisible(true);

            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), videoImageView);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), videoImageView);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);

            ScaleTransition scaleUpStarting = new ScaleTransition(Duration.millis(200), videoImageView);
            scaleUpStarting.setToX(1.05);
            scaleUpStarting.setToY(1.05);
            scaleUpStarting.setOnFinished(event -> scaleDown.play());
            scaleUpStarting.play();

            videoImageView.setOnMouseEntered(event -> scaleUp.playFromStart());
            videoImageView.setOnMouseExited(event -> scaleDown.playFromStart());

        }else{ /* Video has no image to search */
            videoImageView.setFitWidth(800);
            videoImageView.setFitHeight(350);
            videoImageView.setOpacity(0.2);
            videoImageView.setVisible(true);
        }
    }
    private void initValues() {
        nameLbl.setText(FormatString.stringNormalize(videoSelected.getTitle()));

        releaseDateLbl.setText(videoSelected.getReleaseDate());

        String duration;
        if(videoSelectedIndex == 0){
            Movie movieSelected = (Movie)videoSelected;
            duration = movieSelected.getDuration()/60+"h "+movieSelected.getDuration()%60+"min";
            directorLbl.setText(movieSelected.getDirector());
        }else{
            TVSerie tvSelected = (TVSerie)videoSelected;
            duration = tvSelected.getNumSeasons()+" Seasons, "+tvSelected.getNumEpisodes()+" Episodes";
            directorAboveLbl.setVisible(false);
        }
        durationLbl.setText(duration);

        if(videoSelected.getDescription().isEmpty())
            overviewLbl.setText("No overview found");
        else
            overviewLbl.setText(videoSelected.getDescription());

        choiceBoxRating.setValue(videoSelected.getRating());

        ratingValueLbl.setText(videoSelected.getRating());

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

        startedCheck.setSelected(videoSelected.isStarted());
        terminatedCheck.setSelected(videoSelected.isTerminated());

        notesTextArea.setText(videoSelected.getNotes());

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

        Platform.runLater(this::setImage);
    }

    private void checkBoxBinding() {
        BooleanBinding notStartedCond = Bindings.not(startedCheck.selectedProperty());

        terminatedCheck.disableProperty().bind(notStartedCond);
        choiceBoxRating.disableProperty().bind(notStartedCond);

        notStartedCond.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                terminatedCheck.setSelected(false);
                choiceBoxRating.getSelectionModel().select(0);
                ratingValueLbl.setText("");
            }
        });
    }

    private void onInputChange() {
        saveBtn.setDisable(
                startedCheck.isSelected() == videoSelected.isStarted() &&
                terminatedCheck.isSelected() == videoSelected.isTerminated() &&
                ratingValueLbl.getText().equals(videoSelected.getRating()) &&
                notesTextArea.getText().equals(videoSelected.getNotes())
        );
    }
    private void confirmBinding() {
        saveBtn.setDisable(true);

        startedCheck.selectedProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
        terminatedCheck.selectedProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> onInputChange());
        ratingValueLbl.textProperty().addListener((observable, oldValue, newValue) -> onInputChange() );
    }

    private void decreaseBinding() {
        BooleanBinding bingingBtn = ratingValueLbl.textProperty().isEqualTo("0.5 "+star).or(ratingValueLbl.textProperty().isEmpty());
        decreaseRatingBtn.disableProperty().bind(bingingBtn);
    }
    private void increaseBinding() {
        BooleanBinding bingingBtn = ratingValueLbl.textProperty().isEqualTo(star+" 10 "+star).or(ratingValueLbl.textProperty().isEmpty());
        increaseRatingBtn.disableProperty().bind(bingingBtn);
    }

    private void resizeScene() {
        double h = Screen.getPrimary().getBounds().getHeight();
        double w = Screen.getPrimary().getBounds().getWidth();

        Stage stage = (Stage)saveBtn.getScene().getWindow();

        stage.setHeight(0.58 * h);
        stage.setWidth(0.60 * w);
    }

    private float ratingToFloat(String rating) {
        return Float.parseFloat(
                rating.replaceAll(star,"").trim()
        );
    }

    @FXML
    public void onDecreaseRating() {
        float rating = ratingToFloat(ratingValueLbl.getText());
        if((int)rating == rating){
            ratingValueLbl.setText(
                    (rating - 0.5f)+" "+star
            );
        }else{
            ratingValueLbl.setText(
                    (int)(rating - 0.5f)+" "+star
            );
        }

//        System.out.println(rating+" -> "+ratingValueLbl.getText());
//        choiceBoxRating.getSelectionModel().select(0);
    }
    @FXML
    public void onIncreaseRating() {
        float rating = ratingToFloat(ratingValueLbl.getText());

        if(rating == 9.5f){
            ratingValueLbl.setText(star+" 10 "+star);
        }else if((int)rating == rating){
            ratingValueLbl.setText(
                    (rating + 0.5f)+" "+star
            );
        }else{
            ratingValueLbl.setText(
                    (int)(rating + 0.5f)+" "+star
            );
        }

//        System.out.println(rating+" -> "+ratingValueLbl.getText());
//        choiceBoxRating.getSelectionModel().select(0);
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
                    ratingValueLbl.getText(),
                    movieSelected.getId(),
                    movieSelected.getPathImage(),
                    notesTextArea.getText(),
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
                    ratingValueLbl.getText(),
                    tvSelected.getId(),
                    tvSelected.getPathImage(),
                    notesTextArea.getText(),
                    tvSelected.getNumSeasons(),
                    tvSelected.getNumEpisodes()
            );
            tvSelected.getGenres().forEach(gi -> tvToAdd.addGenre(gi.getId()) );

            try{
                if(videoSelectedIndex == 1) {
                    vk.removeTVSerie(tvSelected.getId());
                    vk.addTVSerie(tvToAdd);
                }else if(videoSelectedIndex == 2){
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

    @FXML
    public void onExit() {
        Stage stage = (Stage)nameLbl.getScene().getWindow();
        stage.close();
    }
}
