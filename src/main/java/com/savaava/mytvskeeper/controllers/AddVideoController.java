package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertWarning;
import com.savaava.mytvskeeper.exceptions.VideoAlreadyExistsException;
import com.savaava.mytvskeeper.main.StartApplication;
import com.savaava.mytvskeeper.models.*;

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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

import java.io.IOException;

import java.util.ResourceBundle;

public class AddVideoController implements Initializable {
    private int videoIndex;
    private final StringProperty strBinding = new SimpleStringProperty("");
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
    public Button searchBtn;
    @FXML
    public ImageView detailsImageBtn, insertImageBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* runLater method to be able to exit in cathes, otherwise (Stage)table.getScene() would be null, and
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

        /* when the scene starts */
        detailsImageBtn.setDisable(true);
        detailsImageBtn.setOpacity(0.3);
        /* Binding to make detailsImageBtn disabled and opaque when there's no item selected from the list
           and when the video selected has no image to show in details */
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.getPathImage() == null){
                detailsImageBtn.setDisable(true);
                detailsImageBtn.setOpacity(0.3);
            }else{
                detailsImageBtn.setDisable(false);
                detailsImageBtn.setOpacity(1);
            }
        });

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
                try {
                    onDetailsClicked();
                } catch (IOException ex) {
                    new AlertError("Error showing details","Error's details: "+ex.getMessage());
                }
            }
        });
    }

    private void initTable() {
        table.setItems(list);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        centerCells(titleColumn);
        centerCells(dateColumn);
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
                    setAlignment(Pos.CENTER); // Centra il testo
                }
            }
        });
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
            /* It's not possible to have videoIndex != 1,2,3 */
        }catch(IOException | InterruptedException ex){
            new AlertError("Error searching video in TMDB","Error's details: "+ex.getMessage());
        }
    }

    @FXML
    public void onDetailsClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/VideoDetailsAdd.fxml"));
        Parent root = loader.load();
        VideoDetailsAddController vdController = loader.getController();

        Video v = table.getSelectionModel().getSelectedItem();
        if(v == null) /* Reaching this block means there's an error */
            return;

        vdController.setTitle(v.getTitle());
        vdController.setPathImage(v.getPathImage());

        String sceneTitle;
        if(videoIndex == 1){
            sceneTitle = "Movie details";
        } else if (videoIndex == 2) {
            sceneTitle = "TV Serie details";
        } else {
            sceneTitle = "Anime Serie details";
        }

        Scene scene = new Scene(root, 850, 530);
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(sceneTitle);
        popup.getIcons().add(new Image(StartApplication.APPLICATION_ICON_PATH));
        popup.setResizable(false);
        popup.setScene(scene);
        popup.showAndWait();
    }

    @FXML
    public void onInsertClicked() {
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

    private void onExit() {
        Stage stage = (Stage)table.getScene().getWindow();
        stage.close();
    }
}

