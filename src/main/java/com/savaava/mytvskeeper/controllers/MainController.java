package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public Button b;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void testIOExceptionMethod() {
        try(ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream("data.bin")))){

        } catch (IOException ex) {
            new AlertError("Error in testIOExceptionMethod", "Error's details: "+ex.getMessage());
        }
    }
}
