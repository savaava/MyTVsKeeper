package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public Button b;
    @FXML
    public Button btnProva;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void testIOExceptionMethod() {
        LocalDate date = null;
        try(ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream("./executable/bin/data.bin")))){
            date = (LocalDate)oos.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            new AlertError("Error in testIOExceptionMethod", "Error's details: "+ex.getMessage());
        }
        System.out.println(date);
    }

    @FXML
    public void onSaveBin(ActionEvent actionEvent) {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("./executable/bin/data.bin")))){
            oos.writeObject(LocalDate.of(2000,11,15));
        }catch (IOException ex) {
            new AlertError("Error in onSaveBin", "Error's details: "+ex.getMessage());
        }
    }
}
