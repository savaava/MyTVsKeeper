package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertConfirmation;
import com.savaava.mytvskeeper.models.Video;
import com.savaava.mytvskeeper.models.VideoKeeper;
import com.savaava.mytvskeeper.alerts.AlertError;
import com.savaava.mytvskeeper.models.Movie;
import com.savaava.mytvskeeper.models.TVSerie;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.Event;
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

import java.net.URL;

import java.io.IOException;
import java.io.File;

import java.util.ResourceBundle;

public class MainController implements Initializable {
    private VideoKeeper vk;

    private boolean isInitialized = false;

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

    @FXML
    public TextField searchTfd;
    @FXML
    public Button detailsBtn, deleteBtn;
    @FXML
    public Tab moviesTab, tvsTab, animesTab;

    private String promptTfdSearch[] = {
            "Search a Movie",
            "Search a TV Serie",
            "Search an Anime Serie"
    };
    private String promptBtnDetails[] = {
            "Movie details",
            "TV Serie details",
            "Anime details"
    };


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            File dir = new File("bin");
            if (!dir.exists() || !dir.isDirectory()) {
                if (!dir.mkdir()) {
                    new AlertError("No directory bin", "Failed to create bin directory in current directory");
                    onExit();
                }
            }

            try {
                vk = VideoKeeper.getInstance();
            } catch (Exception ex) {
                new AlertError("Error reading saving data files", "Error's details: " + ex.getMessage());
                onExit();
            }

            searchTfd.setPromptText(promptTfdSearch[0]);

            initMoviesTable();
            initTVsTable();
            initAnimesTable();

            setHeightsCells();

            btnBinding();

            initDoubleClick();

            /* Flag to track the end of initialize */
            isInitialized = true;
        });
    }


    private void initMoviesTable() {
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

    }
    private void initTVsTable() {
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
    }
    private void initAnimesTable() {
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
    }

    private void setHeightsCells() {
        tableViewMovies.setRowFactory(movie -> new TableRow<>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);

                if (empty || movie == null) {
                    setPrefHeight(26);
                } else {
                    int titleLines = movie.getTitle().split("\n").length;
                    setPrefHeight(titleLines * 26);
                }
            }
        });

        tableViewTvs.setRowFactory(tv -> new TableRow<>(){
            @Override
            protected void updateItem(TVSerie tv, boolean empty) {
                super.updateItem(tv, empty);

                if (empty || tv == null) {
                    setPrefHeight(26);
                } else {
                    int titleLines = tv.getTitle().split("\n").length;
                    setPrefHeight(titleLines * 26);
                }
            }
        });

        tableViewAnimes.setRowFactory(anime -> new TableRow<>(){
            @Override
            protected void updateItem(TVSerie anime, boolean empty) {
                super.updateItem(anime, empty);

                if (empty || anime == null) {
                    setPrefHeight(26);
                } else {
                    int titleLines = anime.getTitle().split("\n").length;
                    setPrefHeight(titleLines * 26);
                }
            }
        });
    }

    private void btnBinding() {
        BooleanBinding btnDisableCond1 = tableViewMovies.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding btnDisableCond2 = tableViewTvs.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding btnDisableCond3 = tableViewAnimes.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding btnDisableCond = btnDisableCond1.and(btnDisableCond2).and(btnDisableCond3);

        detailsBtn.disableProperty().bind(btnDisableCond);
        deleteBtn.disableProperty().bind(btnDisableCond);
    }

    private void initDoubleClick() {
        tableViewMovies.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                try {
                    onDetailsClicked();
                } catch (IOException ex) {
                    new AlertError("Error showing details","Error's details: "+ex.getMessage());
                }
            }
        });

        tableViewTvs.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                try {
                    onDetailsClicked();
                } catch (IOException ex) {
                    new AlertError("Error showing details","Error's details: "+ex.getMessage());
                }
            }
        });

        tableViewAnimes.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                try {
                    onDetailsClicked();
                } catch (IOException ex) {
                    new AlertError("Error showing details","Error's details: "+ex.getMessage());
                }
            }
        });
    }

    private void clearAllSelection() {
        tableViewMovies.getSelectionModel().clearSelection();
        tableViewTvs.getSelectionModel().clearSelection();
        tableViewAnimes.getSelectionModel().clearSelection();
    }


    @FXML
    public void onTabViewSelection(Event event) {
        /* Necessary condition because when the application starts a tab is automatically selected */
        if(! isInitialized)
            return;

        Tab selectedTab = (Tab) event.getSource();
        if (! selectedTab.isSelected())
            return;

        if(selectedTab == moviesTab){
            searchTfd.setPromptText(promptTfdSearch[0]);
        }else if(selectedTab == tvsTab){
            searchTfd.setPromptText(promptTfdSearch[1]);
        }else if(selectedTab == animesTab){
            searchTfd.setPromptText(promptTfdSearch[2]);
        }else /* caso non previsto */
            System.err.println("Unexpected tab selected: "+selectedTab.getText());

        clearAllSelection();
    }


    @FXML
    public void onNewMovie() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoToAdd(1);

        showPopup(root, "New Movie");

        clearAllSelection();
    }
    @FXML
    public void onNewTv() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoToAdd(2);

        showPopup(root, "New TV Serie");

        clearAllSelection();
    }
    @FXML
    public void onNewAnime() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoToAdd(3);

        showPopup(root, "New Anime Serie");

        clearAllSelection();
    }

    @FXML
    public void onDetailsClicked() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/VideoDetails.fxml"));
        Parent root = loader.load();
        VideoDetailsController vdController = loader.getController();

        String title;
        Video v;
        int index;
        if(tableViewMovies.getSelectionModel().getSelectedItem() != null){
            v = tableViewMovies.getSelectionModel().getSelectedItem();
            title = promptBtnDetails[0];
            index = 1;
        }else if(tableViewTvs.getSelectionModel().getSelectedItem() != null){
            v = tableViewTvs.getSelectionModel().getSelectedItem();
            title = promptBtnDetails[1];
            index = 2;
        }else if(tableViewAnimes.getSelectionModel().getSelectedItem() != null){
            v = tableViewAnimes.getSelectionModel().getSelectedItem();
            title = promptBtnDetails[2];
            index = 3;
        }else{
            v = null;
            title = "";
            index = 0;
        }

        vdController.setVideoSelected(v);
        vdController.setVideoSelectedIndex(index);

        showPopup(root, title, 800, 550);

        clearAllSelection();

        if(index == 1)
            initMoviesTable();
        else if(index == 2)
            initTVsTable();
        else if(index == 3)
            initAnimesTable();
    }

    @FXML
    public void onDeleteClicked() {
        Video videoToDelete;

        String header = "Are you sure to delete the ";
        try {
            if (tableViewMovies.getSelectionModel().getSelectedItem() != null) {

                videoToDelete = tableViewMovies.getSelectionModel().getSelectedItem();
                if (new AlertConfirmation(header+" "+videoToDelete.getTitle()+" ?").getResultConfirmation())
                    vk.removeMovie(videoToDelete.getId());

            } else if (tableViewTvs.getSelectionModel().getSelectedItem() != null) {

                videoToDelete = tableViewTvs.getSelectionModel().getSelectedItem();
                if (new AlertConfirmation(header+" "+videoToDelete.getTitle()+" ?").getResultConfirmation())
                    vk.removeTVSerie(videoToDelete.getId());

            } else if (tableViewAnimes.getSelectionModel().getSelectedItem() != null) {

                videoToDelete = tableViewAnimes.getSelectionModel().getSelectedItem();
                if (new AlertConfirmation(header+" "+videoToDelete.getTitle()+" ?").getResultConfirmation())
                    vk.removeAnimeSerie(videoToDelete.getId());

            }
        } catch (Exception ex) {
            new AlertError("Error deleting the Video","Error's details: "+ex.getMessage());
        }
    }


    @FXML
    public void onConfig() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Config.fxml"));
        Parent root = loader.load();

        showPopup(root, "Configuration", 500, 300);
    }


    @FXML
    public void onExport() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Export.fxml"));
        Parent root = loader.load();

        showPopup(root, "Export Videos", 480, 235);
    }

    @FXML
    public void onAbout() {

    }

    @FXML
    public void onExit() {
        Stage stage = (Stage)tableViewMovies.getScene().getWindow();
        stage.close();
    }


    private void showPopup(Parent root, String title, int w, int h) {
        Scene scene = new Scene(root, w, h);
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);
        popup.getIcons().add(new Image("/images/MyTVsKeeper.png"));
        popup.setResizable(false);
        popup.setScene(scene);
        popup.showAndWait();
    }
    private void showPopup(Parent root, String title) {
        showPopup(root, title, 950, 600);
    }
}
