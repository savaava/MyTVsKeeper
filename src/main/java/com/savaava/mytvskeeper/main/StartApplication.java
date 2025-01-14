package com.savaava.mytvskeeper.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StartApplication extends Application {
    public static void startApp(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("/views/Main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(400);
        stage.setMinWidth(1000);
        stage.setTitle("MyTVsKeeper");
        stage.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}