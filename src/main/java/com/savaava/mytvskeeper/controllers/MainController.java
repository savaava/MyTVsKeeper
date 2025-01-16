package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.Movie;

import com.savaava.mytvskeeper.models.TVSerie;
import com.savaava.mytvskeeper.models.VideoKeeper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public TableView<Movie> tableViewMovies;
    @FXML
    public TableColumn<Movie, String>
            titleColumnMovie,
            durationColumnMovie,
            releaseDateColumnMovie,
            directorColumnMovie,
            ratingColumnMovie;
    @FXML
    public TableColumn<Movie, Boolean>
            startedColumnMovie,
            terminatedColumnMovie;

    @FXML
    public TableView<TVSerie> tableViewTvs, tableViewAnimes;
    @FXML
    public TableColumn<TVSerie, String>
            titleColumnTv,releaseDateColumnTv,ratingColumnTv,
            titleColumnAnime,releaseDateColumnAnime,ratingColumnAnime;
    @FXML
    public TableColumn<TVSerie, Integer>
            seasonsColumnTv,episodesColumnTv,
            seasonsColumnAnime,episodesColumnAnime;
    @FXML
    public TableColumn<TVSerie,Boolean>
            startedColumnTv,terminatedColumnTv,
            startedColumnAnime,terminatedColumnAnime;

    private VideoKeeper vk;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File dir = new File("bin\\");
        if(!dir.exists() || !dir.isDirectory()) {
            if(!dir.mkdir()){
                new AlertError("Failed bin","Failed to create bin directory in current directory");
            }
        }

        try{
            vk = VideoKeeper.getInstance();
        }catch(Exception ex){
            new AlertError("Error reading saving data files","Error's details: "+ex.getMessage());
        }

        tableViewMovies.setVisible(true);
        tableViewTvs.setVisible(false);
        tableViewAnimes.setVisible(false);

        tableViewMovies.setItems(vk.getMovies());
        titleColumnMovie.setCellValueFactory(new PropertyValueFactory<>("title"));
        durationColumnMovie.setCellValueFactory(new PropertyValueFactory<>("duration"));
        releaseDateColumnMovie.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        directorColumnMovie.setCellValueFactory(new PropertyValueFactory<>("director"));
        startedColumnMovie.setCellValueFactory(new PropertyValueFactory<>("started"));
        terminatedColumnMovie.setCellValueFactory(new PropertyValueFactory<>("terminated"));
        ratingColumnMovie.setCellValueFactory(new PropertyValueFactory<>("rating"));
        startedColumnMovie.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item) {
                        label.setText("✔");
                        label.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        label.setText("✘");
                        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        terminatedColumnMovie.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item) {
                        label.setText("✔");
                        label.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        label.setText("✘");
                        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });


        tableViewTvs.setItems(vk.getTvSeries());
        titleColumnTv.setCellValueFactory(new PropertyValueFactory<>("title"));
        seasonsColumnTv.setCellValueFactory(new PropertyValueFactory<>("numSeasons"));
        episodesColumnTv.setCellValueFactory(new PropertyValueFactory<>("numEpisodes"));
        releaseDateColumnTv.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        startedColumnTv.setCellValueFactory(new PropertyValueFactory<>("started"));
        terminatedColumnTv.setCellValueFactory(new PropertyValueFactory<>("terminated"));
        ratingColumnTv.setCellValueFactory(new PropertyValueFactory<>("rating"));
        startedColumnTv.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item) {
                        label.setText("✔");
                        label.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        label.setText("✘");
                        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        terminatedColumnTv.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item) {
                        label.setText("✔");
                        label.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        label.setText("✘");
                        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });


        tableViewAnimes.setItems(vk.getAnimeSeries());
        titleColumnAnime.setCellValueFactory(new PropertyValueFactory<>("title"));
        seasonsColumnAnime.setCellValueFactory(new PropertyValueFactory<>("numSeasons"));
        episodesColumnAnime.setCellValueFactory(new PropertyValueFactory<>("numEpisodes"));
        releaseDateColumnAnime.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        startedColumnAnime.setCellValueFactory(new PropertyValueFactory<>("started"));
        terminatedColumnAnime.setCellValueFactory(new PropertyValueFactory<>("terminated"));
        ratingColumnAnime.setCellValueFactory(new PropertyValueFactory<>("rating"));
        startedColumnAnime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item) {
                        label.setText("✔");
                        label.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        label.setText("✘");
                        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        terminatedColumnAnime.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item) {
                        label.setText("✔");
                        label.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        label.setText("✘");
                        label.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });


        tableViewMovies.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown() && e.getClickCount()==2) {
                System.out.println(tableViewMovies.getSelectionModel().getSelectedItem());
            }
        });
    }

    @FXML
    public void onMovies() {
        tableViewMovies.setVisible(true);
        tableViewTvs.setVisible(false);
        tableViewAnimes.setVisible(false);
    }

    @FXML
    public void onTVSeries() {
        tableViewMovies.setVisible(false);
        tableViewTvs.setVisible(true);
        tableViewAnimes.setVisible(false);
    }

    @FXML
    public void onAnimeSeries() {
        tableViewMovies.setVisible(false);
        tableViewTvs.setVisible(false);
        tableViewAnimes.setVisible(true);
    }


    @FXML
    public void onNewMovie() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoToAdd(1);

        showPopup(root, "New Movie");
    }
    @FXML
    public void onNewTv() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoToAdd(2);

        showPopup(root, "New TV Serie");
    }
    @FXML
    public void onNewAnime() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoToAdd(3);

        showPopup(root, "New Anime Serie");
    }

    private void showPopup(Parent root, String title) {
        Scene scene = new Scene(root, 950, 600);
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);
        popup.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        popup.setResizable(false);
        popup.setScene(scene);
        popup.showAndWait();
    }


    @FXML
    public void onExport() {

    }

    @FXML
    public void onConfig() {

    }

    @FXML
    public void onAbout() {

    }

    @FXML
    public void onExit() {

    }
}
