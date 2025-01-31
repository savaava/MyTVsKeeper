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
    private VideoKeeper.CsvHandler vkCsvHandler;

    private FileChooser fileChooser;
    private File lastPath;

    @FXML
    public RadioButton moviesBtn, tvBtn, animeBtn;
    @FXML
    public ToggleGroup videosGroup;

    @FXML
    public Button exportBtn;
    @FXML
    public Label numberLbl1, numberLbl2, destLbl, sizeLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                vkCsvHandler = VideoKeeper.getInstance().new CsvHandler();
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
        File fileOut;

        /* ensures to user the last path he used is the default one */
        if(lastPath != null && lastPath.exists())
            fileChooser.setInitialDirectory(lastPath);

        int videosNumber;

        try {
            if (moviesBtn.isSelected()) {
                fileChooser.setInitialFileName("MyTvsKeeper" + VideoKeeper.CURRENT_VERSION + "_Movies");
                fileOut = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());

                if (fileOut == null)
                    return;

                videosNumber = vkCsvHandler.csvExportMovies(fileOut);

                numberLbl1.setText("• Number of Movies:");
                numberLbl2.setText(" " + videosNumber);
            } else if (tvBtn.isSelected()) {
                fileChooser.setInitialFileName("MyTvsKeeper" + VideoKeeper.CURRENT_VERSION + "_TVSeries");
                fileOut = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());

                if (fileOut == null)
                    return;

                videosNumber = vkCsvHandler.csvExportTVSeries(fileOut);

                numberLbl1.setText("• Number of TV Series:");
                numberLbl2.setText(" " + videosNumber);
            } else { /* animeBtn.isSelected */
                fileChooser.setInitialFileName("MyTvsKeeper" + VideoKeeper.CURRENT_VERSION + "_AnimeSeries");
                fileOut = fileChooser.showSaveDialog(exportBtn.getScene().getWindow());

                if (fileOut == null)
                    return;

                videosNumber = vkCsvHandler.csvExportAnimeSeries(fileOut);

                numberLbl1.setText("• Number of Anime Series:");
                numberLbl2.setText(" " + videosNumber);
            }
        } catch (IOException ex) {
            new AlertError("Error exporting Videos", "Error's details: " + ex.getMessage());
            return;
        }

        destLbl.setText(fileOut.getAbsolutePath());

        /* modularize in future */
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

        /* For next export */
        lastPath = new File(fileOut.getParent());
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
