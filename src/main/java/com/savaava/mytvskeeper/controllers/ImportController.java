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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
    public Label sourceLbl;

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

    private void clearSelection() {
        videosGroup.getSelectedToggle().setSelected(false);
    }

    @FXML
    public void onBrowse() {
        if(lastFileSource != null && lastFileSource.exists())
            fileChooser.setInitialDirectory(lastFileSource.getParentFile());

        File fileSource = fileChooser.showOpenDialog(importBtn.getScene().getWindow());

        if(fileSource == null)
            return;

        lastFileSource = fileSource;

        fileSelectedBoolean.setValue(true);
        sourceLbl.setText(fileSource.getAbsolutePath());
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
            if (moviesBtn.isSelected()) {
                vkCsvHandler.csvImportMovies(lastFileSource);
            } else if (tvBtn.isSelected()) {
                vkCsvHandler.csvImportTVSerie(lastFileSource);
            } else {
                vkCsvHandler.csvImportAnimeSerie(lastFileSource);
            }

            clearSelection();
        }catch(NotMatchingVideoTypeException ex){
            new AlertWarning("Video type selected doesn't match the file one",
                    ex.getMessage()+"\n\nPlease change the Video type in the selection, or choose a file suitable for your selection");
        }catch(IOException ex){
            new AlertError("Error Importing file: "+lastFileSource.getName(), "Error's details: "+ex.getMessage());
        }
    }

    private void onExit() {
        Stage stage = (Stage)importBtn.getScene().getWindow();
        stage.close();
    }
}
