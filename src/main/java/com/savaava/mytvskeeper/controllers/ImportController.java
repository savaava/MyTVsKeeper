package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertEasterEgg;
import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertWarning;
import com.savaava.mytvskeeper.exceptions.NotMatchingVideoTypeException;
import com.savaava.mytvskeeper.models.VideoKeeper;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ImportController implements Initializable {
    private VideoKeeper.CsvHandler vkCsvHandler;

    private final BooleanProperty fileSelectedBoolean = new SimpleBooleanProperty(false);
    private File lastFileSource;
    private FileChooser fileChooser;

    @FXML
    public RadioButton moviesBtn, tvBtn, animeBtn;
    @FXML
    public ToggleGroup videosGroup;
    @FXML
    public Button browseBtn, importBtn;
    @FXML
    public Label sourceLbl, numVideoLbl;
    @FXML
    public ImageView imageDragAndDrop;

    @FXML
    public AnchorPane anchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                vkCsvHandler = VideoKeeper.getInstance().new CsvHandler();
            } catch (Exception ex) {
                new AlertError("Error reading saving data files", "Error's details: " + ex.getMessage());
                onExit();
            }

            bindingBtn();

            initFileChooser();

            setDefaultPaneStyle();
        });
    }

    private void bindingBtn() {
        BooleanBinding noToggleSelection = videosGroup.selectedToggleProperty().isNull();
        BooleanBinding condDisableImport = fileSelectedBoolean.not().or(noToggleSelection);
        importBtn.disableProperty().bind(condDisableImport);
    }

    private void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
    }

    private void setDefaultPaneStyle() {
        anchorPane.setStyle("-fx-background-color: rgba(0,0,0,0)");
    }

    private void clearSelection() {
        videosGroup.getSelectedToggle().setSelected(false);
    }

    private void handleNewFileSelected(File fileSource) {
        if(fileSource == null)
            return;

        lastFileSource = fileSource;

        fileSelectedBoolean.setValue(true);
        sourceLbl.setText(fileSource.getAbsolutePath());
    }    
    @FXML
    public void onBrowse() {
        if(lastFileSource != null && lastFileSource.exists())
            fileChooser.setInitialDirectory(lastFileSource.getParentFile());

        handleNewFileSelected(
                fileChooser.showOpenDialog(importBtn.getScene().getWindow())
        );
    }
    @FXML
    public void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();

        if (db.hasFiles()) {
            List<File> files = db.getFiles();

            if(files.size()==1 && files.getFirst().getName().endsWith(".csv")) {
                event.acceptTransferModes(TransferMode.COPY);
                anchorPane.setStyle("-fx-background-radius: 20;-fx-background-color: #87CEFA;");
                System.out.println(files.getFirst().getAbsolutePath());
            }else {
                anchorPane.setStyle("-fx-background-radius: 20;-fx-background-color: #FFDEAD;");
                System.err.println(files.getFirst().getAbsolutePath());
            }
        }
        event.consume();
    }
    @FXML
    public void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();

        if (db.hasFiles()) {
            List<File> files = db.getFiles();
            File fileSource = files.getFirst();

            if(files.size()==1 && fileSource.getName().endsWith(".csv")){
                event.setDropCompleted(true);
                handleNewFileSelected(fileSource);
                imageDragAndDrop.setImage(new Image("/images/DragAndDrop.png"));
            }else{
                event.setDropCompleted(false);
            }
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }
    @FXML
    public void onDragExited() {
        setDefaultPaneStyle();
    }

    @FXML
    public void onPathClicked() {
        if(sourceLbl.getText().isEmpty()) {
            new AlertEasterEgg();
            return;
        }

        if(! Desktop.isDesktopSupported()) {
            new AlertError("Error opening directory","Desktop is not supported on the current platform");
            return;
        }

        File directory = new File(sourceLbl.getText()).getParentFile();

        if (directory.exists()) {
            try{ Desktop.getDesktop().open(directory); }
            catch(IOException ex){ new AlertError("Cannot open the directory","dir: "+directory); }
        }else{
            new AlertError("Directory does not exist");
        }
    }

    @FXML
    public void onImport() {
        if(lastFileSource == null)
            return; /* for more strength principle */

        try {
            int numVideo;
            if (moviesBtn.isSelected()) {
                numVideo = vkCsvHandler.csvImportMovies(lastFileSource);
            } else if (tvBtn.isSelected()) {
                numVideo = vkCsvHandler.csvImportTVSerie(lastFileSource);
            } else {
                numVideo = vkCsvHandler.csvImportAnimeSerie(lastFileSource);
            }

            numVideoLbl.setText(Integer.toString(numVideo));

            clearSelection();
        }catch(NotMatchingVideoTypeException ex){
            new AlertWarning("Video type selected doesn't match the file one",
                    ex.getMessage()+"\n\nPlease change the Video type in the selection, or choose a file suitable for your selection");
        }catch(Exception ex){
            new AlertError("Error Importing file: "+lastFileSource.getName(), "Error's details: "+ex.getMessage());
        }
    }

    private void onExit() {
        Stage stage = (Stage)importBtn.getScene().getWindow();
        stage.close();
    }
}
