package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public Button b;
    @FXML
    public Button btnProva;
    @FXML
    public Label lbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void testIOExceptionMethod() {
        LocalDateTime date = null;
        try(ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream("./bin/data.bin")))){
            date = (LocalDateTime) oos.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            new AlertError("Error in testIOExceptionMethod", "Error's details: "+ex.getMessage());
        }
        lbl.setText(date!=null?date.toString():"no informations");
    }

    @FXML
    public void onSaveBin() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("./bin/data.bin")))){
            oos.writeObject(LocalDateTime.now());
        }catch (IOException ex) {
            new AlertError("Error in onSaveBin", "Error's details: "+ex.getMessage());
        }
    }
}
