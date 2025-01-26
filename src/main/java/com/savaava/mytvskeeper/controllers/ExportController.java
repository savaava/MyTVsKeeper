package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertEasterEgg;
import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertInfo;
import com.savaava.mytvskeeper.models.VideoKeeper;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
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

public class ExportController implements Initializable {
    private VideoKeeper vk;

    private FileChooser fileChooser;

    @FXML
    public RadioButton moviesBtn, tvBtn, animeBtn;
    @FXML
    private ToggleGroup videosGroup;

    @FXML
    public Button exportBtn;
    @FXML
    public Label numberLbl1, numberLbl2, destLbl, sizeLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                vk = VideoKeeper.getInstance();
            } catch (Exception ex) {
                new AlertError("Error reading saving data files", "Error's details: " + ex.getMessage());
                onExit();
            }

            btnBinding();

            initFileChooser();
        });
    }

    private void btnBinding() {
        BooleanBinding exportCond = videosGroup.selectedToggleProperty().isNull();
        exportBtn.disableProperty().bind(exportCond);
    }

    private void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Save csv file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
    }

    private void clearSelection() {
        videosGroup.getSelectedToggle().setSelected(false);
    }

    @FXML
    public void onExport() {
        File fileOut = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());

        if(fileOut == null)
            return;

        String destPath = fileOut.getAbsolutePath();

        destLbl.setText(destPath);

        if(moviesBtn.isSelected()){

            numberLbl1.setText("• Number of Movies:");
            numberLbl2.setText(" "+vk.moviesNumber());
            try{ vk.csvExportMovies(destPath); }
            catch (IOException ex) { new AlertError("Error exporting Movies", "Error's details: "+ex.getMessage()); }

        }else if(tvBtn.isSelected()){

            numberLbl1.setText("• Number of TV Series:");
            numberLbl2.setText(" "+vk.tvsNumber());
            try{ vk.csvExportTVSeries(destPath); }
            catch (IOException ex) { new AlertError("Error exporting TV Series", "Error's details: "+ex.getMessage()); }

        }else if(animeBtn.isSelected()){

            numberLbl1.setText("• Number of Anime Series:");
            numberLbl2.setText(" "+vk.animesNumber());
            try{ vk.csvExportAnimeSeries(destPath); }
            catch (IOException ex) { new AlertError("Error exporting Anime Series", "Error's details: "+ex.getMessage()); }

        }else{
            new AlertError("Error exporting");
        }

        /* modularize in future also for images */
        double sizeB = fileOut.length();
        double sizeKb = sizeB/1024.0;
        double sizeMb = sizeKb/1024.0;

        String sizeStr;
        if(sizeMb >= 1)
            sizeStr = String.format("%.2f MB", sizeMb);
        else if(sizeKb >= 1)
            sizeStr = String.format("%.2f KB", sizeKb);
        else
            sizeStr = String.format("%.0f Bytes", sizeB);

        sizeLbl.setText(sizeStr);

        clearSelection();
    }

    @FXML
    public void onPathClicked() {
        if(destLbl.getText().isEmpty()) {
            new AlertEasterEgg();
            return;
        }

        if(! Desktop.isDesktopSupported()) {
            new AlertError("Error opening directory","Desktop is not supported on the current platform");
            return;
        }

        File directory = new File(destLbl.getText()).getParentFile();

        if (directory.exists()) {
            try{ Desktop.getDesktop().open(directory); }
            catch(IOException ex){ new AlertError("Cannot open the directory","dir: "+directory); }
        }else{
            new AlertError("Directory does not exist");
        }
    }

    public void onExit() {
        Stage stage = (Stage)exportBtn.getScene().getWindow();
        stage.close();
    }
}
