package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertWarning extends Alert {
    public AlertWarning(String header, String content){
        super(AlertType.WARNING);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        setTitle("WARNING");
        setHeaderText(header);
        setContentText(content);
        showAndWait();
    }

    public AlertWarning(String header){
        this(header, "This is a warning message");
    }

    public AlertWarning(){
        this("WARNING !", "This is a warning message");
    }
}
