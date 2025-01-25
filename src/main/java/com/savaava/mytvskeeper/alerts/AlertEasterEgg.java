package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertEasterEgg extends Alert {
    public AlertEasterEgg(String header, String content) {
        super(AlertType.INFORMATION);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        setTitle("EASTER EGG");
        setHeaderText(header);
        setContentText(content);
        showAndWait();
    }

    public AlertEasterEgg(String header) {
        this(header, "");
    }

    public AlertEasterEgg() {
        this("You've just found an Easter Egg !!!");
    }
}
