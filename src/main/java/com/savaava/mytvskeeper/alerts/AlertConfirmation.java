package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertConfirmation extends Alert {
    private final Optional<ButtonType> result;

    public AlertConfirmation(String header, String content) {
        super(AlertType.CONFIRMATION);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        setTitle("CONFIRMATION");
        setHeaderText(header);
        setContentText(content);
        getButtonTypes().removeAll(ButtonType.OK,ButtonType.CANCEL);
        getButtonTypes().addAll(ButtonType.YES,ButtonType.NO);
        result = showAndWait();
    }

    public AlertConfirmation(String header) {
        this(header, "");
    }

    public AlertConfirmation() {
        this("CONFIRMATION");
    }

    public boolean getResultConfirmation() {
        return result.isPresent() && result.get()==ButtonType.YES;
    }
}
