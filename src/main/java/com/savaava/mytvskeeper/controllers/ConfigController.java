package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.alerts.AlertInfo;
import com.savaava.mytvskeeper.exceptions.ConfigNotExistsException;
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
    private final StringProperty strBinding = new SimpleStringProperty("");
    private TMDatabase tmdb;

    @FXML
    public TextField tfd;
    @FXML
    public Hyperlink hl;
    @FXML
    public Button checkBtn, confirmBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            boolean flag = true;

            try {
                tmdb = TMDatabase.getInstance();
            } catch (ConfigNotExistsException ex) {
                flag = false;
            } catch (IOException ex) {
                new AlertError("Error reading config file", "Error's details: " + ex.getMessage());
                onExit();
            }

            try {
                if(flag) {
                    if (tmdb.verifyConfig(tmdb.getApiKey()))
                        new AlertInfo("Application already configured",
                                "No need to configure it again. Your working API Key:\n" + tmdb.getApiKey());
                    else
                        new AlertError("Existing Configuration doesn't work !",
                                "Your current API Key is not valid:\n"+tmdb.getApiKey());
                }
            }catch(Exception ex){System.out.println(ex.getMessage());}
        });

        BooleanBinding condCheck = tfd.textProperty().isEmpty().or(tfd.textProperty().isEqualTo(strBinding));
        checkBtn.disableProperty().bind(condCheck);

        confirmBtn.setDisable(true);
        tfd.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmBtn.setDisable(true);
        });

        setCopyableLink();
    }

    private void setCopyableLink() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy link");
        copyItem.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(hl.getText());
            clipboard.setContent(content);
        });
        contextMenu.getItems().add(copyItem);

        hl.setOnContextMenuRequested(event ->
                contextMenu.show(hl, event.getScreenX(), event.getScreenY())
        );
    }

    @FXML
    public void handleHl() {
        hl.setVisited(false);

        try {
            URI uri = new URI(hl.getText());
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                }
            }
        }catch(IOException | URISyntaxException ex) {
            new AlertError("Error to browse to link",
                    "browse manually at link "+hl.getText()+"\nError's details: "+ex.getMessage());
        }
    }

    @FXML
    public void onCheckKey() throws IOException, InterruptedException {
        strBinding.setValue(tfd.getText());
        confirmBtn.setDisable(! tmdb.verifyConfig(tfd.getText()));
    }

    @FXML
    public void onConfirmKey() {
        tmdb.setApiKey(tfd.getText());
        try {
            tmdb.saveConfig();
        }catch(IOException ex){
            new AlertError("Error saving config file","Error's details: "+ex.getMessage());
        }
        confirmBtn.setDisable(true);
    }

    @FXML
    public void onExit() {
        Stage stage = (Stage)tfd.getScene().getWindow();
        stage.close();
    }
}
