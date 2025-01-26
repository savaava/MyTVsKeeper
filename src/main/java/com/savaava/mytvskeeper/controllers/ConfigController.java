package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertConfirmation;
import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertInfo;
import com.savaava.mytvskeeper.models.TMDatabase;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {
    private final StringProperty strBindingCheck = new SimpleStringProperty("");
    private final StringProperty strBindingConfirm = new SimpleStringProperty("");
    private TMDatabase tmdb;

    @FXML
    public TextField tfd;
    @FXML
    public Label linkLbl, currentKeyLbl;
    @FXML
    public Button checkBtn, saveBtn;

    private final String noKeyStr = "No API Key provided";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                tmdb = TMDatabase.getInstance();
            }catch(IOException ex) {
                new AlertError("Error reading config file", "Error's details: " + ex.getMessage());
                onExit();
            }

            try {
                if(tmdb.hasConfiguration()) {
                    currentKeyLbl.setText(tmdb.getApiKey());

                    if (tmdb.verifyConfig(tmdb.getApiKey())) {
                        new AlertInfo("Application already configured",
                                "No need to configure it again.\nYour working API Key: "+tmdb.getApiKey());
                    }else{
                        new AlertError("Existing Configuration doesn't work !",
                                "Your current API Key is not valid: "+tmdb.getApiKey());
                    }
                }else{
                    currentKeyLbl.setText(noKeyStr);
                }
            }catch(Exception ex){System.out.println(ex.getMessage());}
        });

        btnBindings();


    }

    private void btnBindings() {
        BooleanBinding condCheck = tfd.textProperty().isEmpty().or(tfd.textProperty().isEqualTo(strBindingCheck));
        checkBtn.disableProperty().bind(condCheck);

        BooleanBinding condConfirm = tfd.textProperty().isEmpty().or(tfd.textProperty().isNotEqualTo(strBindingConfirm));
        saveBtn.disableProperty().bind(condConfirm);
    }

    @FXML
    private void onCopyLink() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(linkLbl.getText());
        clipboard.setContent(content);
    }

    @FXML
    public void handleLinkLbl() {
        if(! Desktop.isDesktopSupported())
            return;

        try {
            URI uri = new URI(linkLbl.getText());

            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }

        }catch(IOException | URISyntaxException ex) {
            new AlertError("Error browsing to link",
                    "browse manually at link \nError's details: "+ex.getMessage());
        }
    }

    @FXML
    public void onCheckKey() throws IOException, InterruptedException {
        strBindingCheck.setValue(tfd.getText());

        if(tmdb.verifyConfig(tfd.getText()))
            strBindingConfirm.setValue(tfd.getText());
        else
            strBindingConfirm.setValue("");
    }

    @FXML
    public void onSave() {
        tmdb.setApiKey(tfd.getText());

        try{
            tmdb.saveConfig();
            new AlertInfo("Application is now configured", "Your working API Key: "+tmdb.getApiKey());
        }catch(IOException ex){
            new AlertError("Error saving config file", "Error's details: "+ex.getMessage());
        }

        currentKeyLbl.setText(tmdb.getApiKey());
    }

    @FXML
    public void onDeleteKey() {
        if(new AlertConfirmation("Are you sure to delete your configuration ?",
                "You are about to delete the Key: "+tmdb.getApiKey()).getResultConfirmation()) {
            tmdb.deleteConfig();
            currentKeyLbl.setText(noKeyStr);
        }
    }

    private void onExit() {
        Stage stage = (Stage)tfd.getScene().getWindow();
        stage.close();
    }
}
