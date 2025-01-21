package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertInfo extends Alert {
    public AlertInfo(String header, String content) {
        super(AlertType.INFORMATION);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        setTitle("INFORMATION");
        setHeaderText(header);
        setContentText(content);
        showAndWait();
    }

    public AlertInfo(String header) {
        this(header, "");
    }

    public AlertInfo() {
        this("INFORMATION", "");
    }
}
