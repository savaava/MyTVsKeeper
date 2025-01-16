package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;

public class AlertWarning extends Alert {
    public AlertWarning(String header, String content){
        super(AlertType.WARNING);

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
