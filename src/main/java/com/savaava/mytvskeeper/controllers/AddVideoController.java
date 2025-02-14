package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertWarning;
import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;
import com.savaava.mytvskeeper.main.StartApplication;
import com.savaava.mytvskeeper.models.*;

import com.savaava.mytvskeeper.services.TMDatabase;
import com.savaava.mytvskeeper.utility.Converter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

import java.io.IOException;

import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

public class AddVideoController implements Initializable {
    private VideoKeeper vk;
    private TMDatabase tmdb;

    private ObservableList<Video> list;
    private int videoIndex;
    private final StringProperty strBinding = new SimpleStringProperty("");
    /* will contain at most 20 images
    * The implementation chosen is HashTable (Thread safe) to make the concurrently insertions in synchronized way */
    private Map<String, Image> imagesCache;

    @FXML
    public TextField tfd;
    @FXML
    public TableView<Video> table;

    @FXML
    public TableColumn<Video,String>
            titleColumn,
            previewColumn,
            dateColumn,
            descriptionColumn;

    @FXML
    public Button searchBtn;
    @FXML
    public ImageView insertImageBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                vk = VideoKeeper.getInstance();
            } catch (Exception ex) {
                new AlertError("Error reading saving data files", "Error's details: " + ex.getMessage());
                onExit();
            }

            try {
                tmdb = TMDatabase.getInstance();
            } catch (IOException ex) {
                new AlertError("Error reading config file", "Error's details: " + ex.getMessage());
                onExit();
            }

            if(! tmdb.hasConfiguration()) {
                new AlertWarning("Config file doesn't Exists !",
                        "Please configure the application clicking: \nFile -> Configure application");
                onExit();
            }

            list = FXCollections.observableArrayList();
            imagesCache = new Hashtable<>();

            bindingBtn();

            initTable();

            initDoubleClick();
        });
    }

    public void setVideoToAdd(int videoIndex){
        this.videoIndex = videoIndex;
    }

    private void bindingBtn() {
        BooleanBinding searchDisableCond = tfd.textProperty().isEmpty().or(tfd.textProperty().isEqualTo(strBinding));
        searchBtn.disableProperty().bind(searchDisableCond);

        BooleanBinding  insertDisableCond = table.getSelectionModel().selectedItemProperty().isNull();
        insertImageBtn.disableProperty().bind(insertDisableCond);
        insertImageBtn.opacityProperty().bind(
                Bindings.when(insertDisableCond).then(0.3).otherwise(1.0)
        );
    }

    /**
     * Opens details scene to show the image of the video selected when the user clicks two times on a video
     * and the video has the image.
     */
    public void initDoubleClick()  {
        table.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2 && table.getSelectionModel().getSelectedItem().getPathImage() != null) {
                onInsertClicked();
            }
        });
    }

    private void centerCells(TableColumn<Video, String> column) {
        column.setCellFactory(cell -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    /**
     * Continuously updates the cells of the previewColumn column to display images of the videos found.
     * Since the TableView continuously (for spatial efficiecy) updates the cells I use caching with a {@code imagesCache}
     * to store the images to be inserted into the cells with updateItem, instead of continuously requesting the image to TMDB.
     * <p>
     * Furthermore, when the {@code imagesCache} doesn't have the current image I use a thread for each https request to not have bad
     * performance at the initialization phase. Just like for {@code imagesCache} I can start a maximum of only 20 simultaneous threads.
     * Without threads We had waited the sequential loading of all images → HOL blocking 😎.
     * <p>
     * When the {@code imagesCache} has loaded all images it's not necessary any long to make
     * https requests with threads, because I update the cells with the images contained in the {@code imagesCache}
     */
    private void initPreviewColumn() {
        previewColumn.setCellFactory(cell -> new TableCell<>() {
            private final ImageView imageToSet = new ImageView();

            @Override
            protected void updateItem(String pathImage, boolean empty) {
                super.updateItem(pathImage, empty);

                //System.out.println(imagesCache.size());

                if (pathImage == null || empty) {
                    setGraphic(null);
                }else{
                    if(imagesCache.containsKey(pathImage)){
                        imageToSet.setImage(imagesCache.get(pathImage));
                    }else{
                        new Thread(() ->{
                            try {
                                Image imageTmp = Converter.bytesToImage(
                                        tmdb.getBackdrop(pathImage)
                                );
                                imageToSet.setImage(imageTmp);
                                imagesCache.put(pathImage, imageTmp);
                            }catch(Exception ex){System.err.println(ex.getMessage());}
                        }).start();
                    }

                    imageToSet.setFitWidth(300);
                    imageToSet.setFitHeight(300);
                    imageToSet.setPreserveRatio(true);

                    setGraphic(imageToSet);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    private void initTable() {
        table.setItems(list);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        previewColumn.setCellValueFactory(new PropertyValueFactory<>("pathImage")); /* I'll not insert the path, but the corresponding image */
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        centerCells(titleColumn);
        initPreviewColumn();
        centerCells(dateColumn);
    }

    @FXML
    public void onSearch() {
        imagesCache.clear();

        String nameVideo = tfd.getText();
        strBinding.setValue(nameVideo);

        try{
            if(videoIndex == 1)
                list.setAll(tmdb.getMoviesByName(nameVideo));
            else
                list.setAll(tmdb.getTVSeriesByName(nameVideo));
            /* It's not possible to have videoIndex != 1,2,3 */
        }catch(IOException | InterruptedException ex){
            new AlertError("Error searching video in TMDB","Error's details: "+ex.getMessage());
        }
    }

    @FXML
    public void onInsertClicked() {
        Video videoToAdd = table.getSelectionModel().getSelectedItem();

        /* reaching this block means an error, because the user cannot insert the video if he hadn't selected it for the btn binding */
        if(videoToAdd == null)
            return;

        boolean flagNotExists = false;

        if(videoIndex == 1){
            try {
                Movie movieToAdd = tmdb.getMovieById(videoToAdd.getId());
                vk.addMovie(movieToAdd);
                flagNotExists = true;
            }catch(InterruptedException ex){
                new AlertError("Error searching Movie in TMDB","Error's details: "+ex.getMessage());
            }catch(VideoAlreadyExistsException ex){
                new AlertWarning("Movie already exists","The selected movie already exists in your movie list");
            }catch(Exception ex){
                new AlertError("Error adding Movie","Error's details: "+ex.getMessage());
            }
        }else if(videoIndex == 2){
            try {
                TVSerie tvToAdd = tmdb.getTVSerieById(videoToAdd.getId());
                vk.addTVSerie(tvToAdd);
                flagNotExists = true;
            }catch(InterruptedException ex){
                new AlertError("Error searching TV Serie in TMDB","Error's details: "+ex.getMessage());
            }catch(VideoAlreadyExistsException ex){
                new AlertWarning("TV Serie already exists","The selected TV Serie already exists in your TV Serie list");
            }catch(Exception ex){
                new AlertError("Error adding TV Serie","Error's details: "+ex.getMessage());
            }
        }else{
            try {
                TVSerie animeToAdd = tmdb.getTVSerieById(videoToAdd.getId());
                vk.addAnimeSerie(animeToAdd);
                flagNotExists = true;
            }catch(InterruptedException ex){
                new AlertError("Error searching Anime in TMDB","Error's details: "+ex.getMessage());
            }catch(VideoAlreadyExistsException ex){
                new AlertWarning("Anime already exists","The selected Anime already exists in your Anime list");
            }catch(Exception ex){
                new AlertError("Error adding Anime","Error's details: "+ex.getMessage());
            }
        }

        /* Added the video: */
        if(flagNotExists)
            onExit();
    }

    @FXML
    public void handleEnterPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER && !searchBtn.isDisable())
            onSearch();
    }

    private void onExit() {
        Stage stage = (Stage)table.getScene().getWindow();
        stage.close();
    }
}

