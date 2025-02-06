package com.savaava.mytvskeeper.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertConfirmation extends Alert {
    private final Optional<ButtonType> result;
    private final ButtonType yesButton;
    private final ButtonType noButton;

    public AlertConfirmation(String header, String content) {
        super(AlertType.CONFIRMATION);

        Stage stage = (Stage) getDialogPane().getScene().getWindow();

        /* I'm forced to create personalized buttons because the default ones are italian for the SO */
        yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        setTitle("CONFIRMATION");
        setHeaderText(header);
        setContentText(content);
        getButtonTypes().setAll(yesButton, noButton);
        result = showAndWait();
    }

    public AlertConfirmation(String header) {
        this(header, "");
    }

    public AlertConfirmation() {
        this("CONFIRMATION");
    }

    public boolean getResultConfirmation() {
        return result.isPresent() && result.get()==yesButton;
    }
}
