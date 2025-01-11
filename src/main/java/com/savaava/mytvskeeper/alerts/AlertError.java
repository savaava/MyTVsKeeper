package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;

public class AlertError extends Alert {
    public AlertError(String header, String content){
        super(Alert.AlertType.ERROR);

        setTitle("ERROR");
        setHeaderText(header);
        setContentText(content);
        showAndWait();
    }

    public AlertError(String header){
        this(header, "This is a error message");
    }

    public AlertError(){
        this("ERROR", "This is a error message");
    }
}
