package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertError extends Alert {
    public AlertError(String header, String content){
        super(Alert.AlertType.ERROR);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        setTitle("ERROR");
        setHeaderText(header);
        setContentText(content);
        showAndWait();
    }

    public AlertError(String header){
        this(header, "");
    }

    public AlertError(){
        this("ERROR !", "");
    }
}
