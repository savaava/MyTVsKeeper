package com.savaava.mytvskeeper.controllers;

import com.savaava.mytvskeeper.alerts.AlertConfirmation;
import com.savaava.mytvskeeper.main.StartApplication;
import com.savaava.mytvskeeper.models.*;
import com.savaava.mytvskeeper.alerts.AlertError;

import com.savaava.mytvskeeper.utility.FormatString;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

import java.io.IOException;
import java.io.File;

import java.time.Month;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private VideoKeeper vk;

    private boolean isInitialized = false;

    private final ImagesCache imagesCache = new ImagesCache();

    @FXML
    public TableView<Movie> tableViewMovies;
    @FXML
    public TableColumn<Movie, String>
            titleColumnMovie,
            releaseDateColumnMovie,
            directorColumnMovie;
    @FXML
    public TableColumn<Movie, Integer> durationColumnMovie;
    @FXML
    public TableColumn<Movie, Boolean>
            startedColumnMovie,
            terminatedColumnMovie;
    @FXML
    public TableColumn<Movie, Double> ratingColumnMovie;

    @FXML
    public TableView<TVSerie> tableViewTvs, tableViewAnimes;
    @FXML
    public TableColumn<TVSerie, String>
            titleColumnTv, releaseDateColumnTv,
            titleColumnAnime, releaseDateColumnAnime;
    @FXML
    public TableColumn<TVSerie, Integer>
            seasonsColumnTv, episodesColumnTv,
            seasonsColumnAnime, episodesColumnAnime;
    @FXML
    public TableColumn<TVSerie, Boolean>
            startedColumnTv, terminatedColumnTv,
            startedColumnAnime, terminatedColumnAnime;
    @FXML
    public TableColumn<Movie, Double> ratingColumnTv, ratingColumnAnime;

    @FXML
    public TextField searchTfd;
    @FXML
    public ImageView detailsBtnImage, deleteBtnImage;
    @FXML
    public TabPane videosTabPane;
    @FXML
    public Tab moviesTab, tvsTab, animesTab;
    @FXML
    public Label videoNumLbl;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /* runLater method to be able to exit in cathes, otherwise (Stage)table.getScene() would be null because the stage is not initialized */
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

            updateVideoNumberLbl();

            initMoviesTable();
            initTVsTable();
            initAnimesTable();

            customizeStyle();

            bindingSearchField();

            //setHeightsCells();

            bindingBtn();

            initDoubleClick();

            /* Flag to track the end of initialize */
            isInitialized = true;
        });
    }

    private void initMoviesTable() {
        /* this is unecessary because of the tableViewMovies.setItems(movieFilteredList) */
//        tableViewMovies.setItems(vk.getMovies());

        titleColumnMovie.setCellValueFactory(new PropertyValueFactory<>("title"));
        durationColumnMovie.setCellValueFactory(new PropertyValueFactory<>("duration"));
        releaseDateColumnMovie.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        directorColumnMovie.setCellValueFactory(new PropertyValueFactory<>("director"));
        startedColumnMovie.setCellValueFactory(new PropertyValueFactory<>("started"));
        terminatedColumnMovie.setCellValueFactory(new PropertyValueFactory<>("terminated"));
        ratingColumnMovie.setCellValueFactory(new PropertyValueFactory<>("rating"));

        titleCellsUpdate(titleColumnMovie);

        movieDurationCellsUpdate();

        releaseDateCellsUpdate(releaseDateColumnMovie);

        movieDirectorCellsUpdate(directorColumnMovie);

        stateCellsUpdate(startedColumnMovie);
        stateCellsUpdate(terminatedColumnMovie);

        ratingCellsUpdate(ratingColumnMovie);
    }
    private void initTVsTable() {
        //tableViewTvs.setItems(vk.getTvSeries());

        titleColumnTv.setCellValueFactory(new PropertyValueFactory<>("title"));
        seasonsColumnTv.setCellValueFactory(new PropertyValueFactory<>("numSeasons"));
        episodesColumnTv.setCellValueFactory(new PropertyValueFactory<>("numEpisodes"));
        releaseDateColumnTv.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        startedColumnTv.setCellValueFactory(new PropertyValueFactory<>("started"));
        terminatedColumnTv.setCellValueFactory(new PropertyValueFactory<>("terminated"));
        ratingColumnTv.setCellValueFactory(new PropertyValueFactory<>("rating"));

        titleCellsUpdate(titleColumnTv);

        durationSeriesCellsUpdate(seasonsColumnTv);
        durationSeriesCellsUpdate(episodesColumnTv);

        releaseDateCellsUpdate(releaseDateColumnTv);

        stateCellsUpdate(startedColumnTv);
        stateCellsUpdate(terminatedColumnTv);

        ratingCellsUpdate(ratingColumnTv);
    }
    private void initAnimesTable() {
        //tableViewAnimes.setItems(vk.getAnimeSeries());

        titleColumnAnime.setCellValueFactory(new PropertyValueFactory<>("title"));
        seasonsColumnAnime.setCellValueFactory(new PropertyValueFactory<>("numSeasons"));
        episodesColumnAnime.setCellValueFactory(new PropertyValueFactory<>("numEpisodes"));
        releaseDateColumnAnime.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        startedColumnAnime.setCellValueFactory(new PropertyValueFactory<>("started"));
        terminatedColumnAnime.setCellValueFactory(new PropertyValueFactory<>("terminated"));
        ratingColumnAnime.setCellValueFactory(new PropertyValueFactory<>("rating"));

        titleCellsUpdate(titleColumnAnime);

        durationSeriesCellsUpdate(seasonsColumnAnime);
        durationSeriesCellsUpdate(episodesColumnAnime);

        releaseDateCellsUpdate(releaseDateColumnAnime);

        stateCellsUpdate(startedColumnAnime);
        stateCellsUpdate(terminatedColumnAnime);

        ratingCellsUpdate(ratingColumnAnime);
    }

    private <T extends Video> void titleCellsUpdate(TableColumn<T, String> titleColumn) {
        titleColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String titleCurr, boolean empty) {
                super.updateItem(titleCurr, empty);

                if (empty || titleCurr == null || titleCurr.isEmpty()) {
                    setText(null);
                } else {
                    setText(FormatString.compactTitle(titleCurr));
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    private void movieDurationCellsUpdate() {
        durationColumnMovie.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer duration, boolean empty) {
                super.updateItem(duration, empty);

                if (empty || duration == null) {
                    setText(null);
                } else {
                    int hours = duration / 60;
                    int minutes = duration % 60;
                    setText(hours > 0 ? hours + "h " + minutes + "m" : minutes + "m");
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    private <T extends Video> void durationSeriesCellsUpdate(TableColumn<T, Integer> durationColumn) {
        durationColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer num, boolean empty) { /* num can be #seasons or #episods */
                super.updateItem(num, empty);

                if (empty || num == null) {
                    setText(null);
                } else {
                    setText(num.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }
    private <T extends Video> void releaseDateCellsUpdate(TableColumn<T, String> releaseDateColumn) {
        releaseDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String releaseDateCurr, boolean empty) {
                super.updateItem(releaseDateCurr, empty);

                if (empty || releaseDateCurr == null || releaseDateCurr.isEmpty()) {
                    setText("");
                } else {
                    /* releaseDateCurr format: yyyy-MM-dd */
                    String[] date = releaseDateCurr.split("-");

                    if (date.length == 3) {
                        String day = date[2].startsWith("0") ? date[2].substring(1) : date[2];
                        String month = Month.of(Integer.parseInt(date[1])).toString().toLowerCase();
                        month = month.substring(0, 1).toUpperCase() + month.substring(1);
                        String year = date[0];

                        setText(day + " " + month + " " + year);
                    } else {
                        setText(releaseDateCurr); /* If the format is wrong */
                    }
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
    }
    private <T extends Video> void movieDirectorCellsUpdate(TableColumn<T, String> directorColumn) {
        directorColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String directorCurr, boolean empty) {
                super.updateItem(directorCurr, empty);

                if (empty || directorCurr == null || directorCurr.isEmpty()) {
                    setText(null);
                }else{
                    setText(directorCurr);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
    }
    private <T extends Video> void stateCellsUpdate(TableColumn<T, Boolean> stateColumn) {
        stateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean state, boolean empty) {
                super.updateItem(state, empty);

                if (empty || state == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (state) {
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
    private <T extends Video> void ratingCellsUpdate(TableColumn<T, Double> ratingColumn) {
        String star = "🌟";

        ratingColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double currRating, boolean empty) {
                super.updateItem(currRating, empty);

                if (empty || currRating == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (currRating == 10)
                        setText(star + " 10 " + star);
                    else if (currRating.intValue() == currRating) {
                        setText(currRating.intValue() + " " + star);
                    } else {
                        setText(currRating + " " + star);
                    }

                    setAlignment(Pos.CENTER);

                    double normalizedRating = (currRating - 0.5) / (10 - 0.5);
                    int red = (int) (200 * (1 - normalizedRating));
                    int green = (int) (255 * normalizedRating * 0.7);
                    double transparency = 0.3 + 0.3 * Math.abs(normalizedRating - 0.5) * 2;

                    setStyle(
                            "-fx-background-color: rgba("+red+", "+green+", 0, "+transparency+");"
                    );
                }
            }
        });
    }


    private void customizeStyle() {
    }

    private void bindingSearchField() {
        FilteredList<Movie> moviesFilteredList = new FilteredList<>(vk.getMovies(), b -> true);
        FilteredList<TVSerie> tvsFilteredList = new FilteredList<>(vk.getTvSeries(), b -> true);
        FilteredList<TVSerie> animesFilteredList = new FilteredList<>(vk.getAnimeSeries(), b -> true);

        searchTfd.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            String strSearched = newValue.toLowerCase().trim();

            moviesFilteredList.setPredicate(movie -> {/* here I set a filter=predicate */
                if (newValue.isEmpty())
                    return true;

                String year = "";
                if (movie.getReleaseDate().split("-").length == 3)
                    year = movie.getReleaseDate().split("-")[0];

                return movie.getTitle().toLowerCase().contains(strSearched) ||
                        movie.getDirector().toLowerCase().contains(strSearched) || /* if user search by director */
                        year.equals(strSearched); /* if user search by release date */
            });

            tvsFilteredList.setPredicate(tv -> {
                if (newValue.isEmpty())
                    return true;

                String year = "";
                if (tv.getReleaseDate().split("-").length == 3)
                    year = tv.getReleaseDate().split("-")[0];

                return tv.getTitle().toLowerCase().contains(strSearched) ||
                        year.equals(strSearched);
            });

            animesFilteredList.setPredicate(anime -> {
                if (newValue.isEmpty())
                    return true;

                String year = "";
                if (anime.getReleaseDate().split("-").length == 3)
                    year = anime.getReleaseDate().split("-")[0];

                return anime.getTitle().toLowerCase().contains(strSearched) ||
                        year.equals(strSearched);
            });
        });

        /* without the sortedLists it's impossible to exploit the sorting of TableView clicking the columns */
        SortedList<Movie> sortedMoviesList = new SortedList<>(moviesFilteredList);
        sortedMoviesList.comparatorProperty().bind(tableViewMovies.comparatorProperty());
        tableViewMovies.setItems(sortedMoviesList);

        SortedList<TVSerie> sortedTvsList = new SortedList<>(tvsFilteredList);
        sortedTvsList.comparatorProperty().bind(tableViewTvs.comparatorProperty());
        tableViewTvs.setItems(sortedTvsList);

        SortedList<TVSerie> sortedAnimesList = new SortedList<>(animesFilteredList);
        sortedAnimesList.comparatorProperty().bind(tableViewAnimes.comparatorProperty());
        tableViewAnimes.setItems(sortedAnimesList);
    }

    private void updatePromptSearchField() {
        String prompt = "";

        if (moviesTab.isSelected())
            prompt = "Search your Movie title";
        else if (tvsTab.isSelected())
            prompt = "Search your TV Series title";
        else if (animesTab.isSelected())
            prompt = "Search your Anime Series title";

        searchTfd.setPromptText(prompt);
    }

    private void updateVideoNumberLbl() {
        if (moviesTab.isSelected())
            videoNumLbl.setText("Number of Movies: " + vk.moviesNumber());
        else if (tvsTab.isSelected())
            videoNumLbl.setText("Number of TV Series: " + vk.tvsNumber());
        else if (animesTab.isSelected())
            videoNumLbl.setText("Number of Anime Series: " + vk.animesNumber());
    }

    /* To improve the cells' height, but it's obviously onerous */
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

        tableViewTvs.setRowFactory(tv -> new TableRow<>() {
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

        tableViewAnimes.setRowFactory(anime -> new TableRow<>() {
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

    private void bindingBtn() {
        BooleanBinding btnDisableCond1 = tableViewMovies.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding btnDisableCond2 = tableViewTvs.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding btnDisableCond3 = tableViewAnimes.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding btnDisableCond = btnDisableCond1.and(btnDisableCond2).and(btnDisableCond3);

        detailsBtnImage.disableProperty().bind(btnDisableCond);
        detailsBtnImage.opacityProperty().bind(
                Bindings.when(btnDisableCond).then(0.3).otherwise(1.0)
        );

        deleteBtnImage.disableProperty().bind(btnDisableCond);
        deleteBtnImage.opacityProperty().bind(
                Bindings.when(btnDisableCond).then(0.3).otherwise(1.0)
        );
    }

    private void clearAllSelection() {
        tableViewMovies.getSelectionModel().clearSelection();
        tableViewTvs.getSelectionModel().clearSelection();
        tableViewAnimes.getSelectionModel().clearSelection();
    }

    @FXML
    public void onTabViewSelection() {
        /* Necessary condition because when the application starts a tab is automatically selected */
        if (!isInitialized)
            return;

        updatePromptSearchField();
        updateVideoNumberLbl();

        clearAllSelection();
    }

    private void onNewVideo(int videoIndex, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddVideo.fxml"));
        Parent root = loader.load();
        AddVideoController addController = loader.getController();
        addController.setVideoIndex(videoIndex);

        showPopup(root, title);

        clearAllSelection();

        Video videoAdded = addController.getVideoAdded();
        if (videoAdded == null)
            return;

        if (addController.getVideoIndex() == 0) {
            tableViewMovies.getSelectionModel().select((Movie) videoAdded);
        } else if (addController.getVideoIndex() == 1) {
            tableViewTvs.getSelectionModel().select((TVSerie) videoAdded);
        } else {
            tableViewAnimes.getSelectionModel().select((TVSerie) videoAdded);
        }

        updateVideoNumberLbl();

        onDetailsClicked();
    }

    @FXML
    public void onNewMovie() throws IOException {
        onNewVideo(0, "New Movie");
    }

    @FXML
    public void onNewTv() throws IOException {
        onNewVideo(1, "New TV Series");
    }

    @FXML
    public void onNewAnime() throws IOException {
        onNewVideo(2, "New Anime Series");
    }

    private boolean noVideoSelected() {
        return tableViewMovies.getSelectionModel().getSelectedItem() == null &&
                tableViewTvs.getSelectionModel().getSelectedItem() == null &&
                tableViewAnimes.getSelectionModel().getSelectedItem() == null;
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        if (noVideoSelected())
            return;

        if (keyEvent.getCode() == KeyCode.ENTER) {
            try {
                onDetailsClicked();
            } catch (IOException ex) {
                new AlertError("Error showing details", "Error's details: " + ex.getMessage());
            }
        } else if (keyEvent.getCode() == KeyCode.DELETE) {
            onDeleteClicked();
        }
    }

    @FXML
    public void onDetailsClicked() throws IOException {
        if (noVideoSelected())
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/VideoDetails.fxml"));
        Parent root = loader.load();
        VideoDetailsController vdController = loader.getController();

        String title;
        Video videoSelected;
        int index;

        if (tableViewMovies.getSelectionModel().getSelectedItem() != null) {
            videoSelected = tableViewMovies.getSelectionModel().getSelectedItem();
            title = "Movie details";
            index = 0;
        } else if (tableViewTvs.getSelectionModel().getSelectedItem() != null) {
            videoSelected = tableViewTvs.getSelectionModel().getSelectedItem();
            title = "TV Serie details";
            index = 1;
        } else { /* tableViewAnimes.getSelectionModel().getSelectedItem() != null */
            videoSelected = tableViewAnimes.getSelectionModel().getSelectedItem();
            title = "Anime details";
            index = 2;
        }
//        System.out.println(videoSelected);
        vdController.setVideoSelected(videoSelected);
        vdController.setVideoSelectedIndex(index);

        vdController.setImagesCache(imagesCache);

        showPopup(root, title);

        if (index == 0) {
            /* It's necessary to reinitialize the tables after changes because the TableView doesn't update itself */
            initMoviesTable();
            tableViewMovies.getSelectionModel().select((Movie) videoSelected);
        } else if (index == 1) {
            initTVsTable();
            tableViewTvs.getSelectionModel().select((TVSerie) videoSelected);
        } else {
            initAnimesTable();
            tableViewAnimes.getSelectionModel().select((TVSerie) videoSelected);
        }
    }

    private void setDoubleClickHandler(TableView<? extends Video> tableView) {
        tableView.setOnMousePressed((MouseEvent e) -> {
            if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {
                try {
                    onDetailsClicked();
                } catch (IOException ex) {
                    new AlertError("Error showing details", "Error's details: " + ex.getMessage());
                }
            }
        });
    }

    private void initDoubleClick() {
        setDoubleClickHandler(tableViewMovies);
        setDoubleClickHandler(tableViewTvs);
        setDoubleClickHandler(tableViewAnimes);
    }

    @FXML
    public void onDeleteClicked() {
        if (noVideoSelected())
            return;

        Video videoToDelete;

        String header = "Are you sure to delete ";
        try {
            if (tableViewMovies.getSelectionModel().getSelectedItem() != null) {

                videoToDelete = tableViewMovies.getSelectionModel().getSelectedItem();
                if (new AlertConfirmation(header + videoToDelete.getTitle() + " ?").getResultConfirmation()) {
                    vk.removeMovie(videoToDelete.getId());
                    clearAllSelection();
                }

            } else if (tableViewTvs.getSelectionModel().getSelectedItem() != null) {

                videoToDelete = tableViewTvs.getSelectionModel().getSelectedItem();
                if (new AlertConfirmation(header + videoToDelete.getTitle() + " ?").getResultConfirmation()) {
                    vk.removeTVSerie(videoToDelete.getId());
                    clearAllSelection();
                }

            } else if (tableViewAnimes.getSelectionModel().getSelectedItem() != null) {

                videoToDelete = tableViewAnimes.getSelectionModel().getSelectedItem();
                if (new AlertConfirmation(header + videoToDelete.getTitle() + " ?").getResultConfirmation()) {
                    vk.removeAnimeSerie(videoToDelete.getId());
                    clearAllSelection();
                }

            }

            updateVideoNumberLbl();
        } catch (Exception ex) {
            new AlertError("Error deleting the Video", "Error's details: " + ex.getMessage());
        }
    }

    @FXML
    public void onConfig() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Config.fxml"));
        Parent root = loader.load();

        showPopup(root, "Configuration");
    }

    @FXML
    public void onExport() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Export.fxml"));
        Parent root = loader.load();

        showPopup(root, "Export Videos");
    }

    @FXML
    public void onImport() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Import.fxml"));
        Parent root = loader.load();

        showPopup(root, "Import Videos");

        updateVideoNumberLbl();
    }

    @FXML
    public void onMovieStats() {

    }

    @FXML
    public void onSerieStats() {

    }

    @FXML
    public void onAnimeStats() {

    }

    @FXML
    public void onShortcuts() {

    }

    @FXML
    public void onAbout() {

    }

    @FXML
    public void handleEscPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ESCAPE)
            onExit();
    }

    @FXML
    public void onExit() {
        AlertConfirmation exitConfirmation = new AlertConfirmation("Sure to exit from the application ?");
        if (!exitConfirmation.getResultConfirmation())
            return;

        Stage stage = (Stage) tableViewMovies.getScene().getWindow();
        stage.close();
    }


    private void showPopup(Scene scene, String title) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);
        popup.getIcons().add(new Image(StartApplication.APPLICATION_ICON_PATH));
        popup.setResizable(false);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showPopup(Parent root, String title) {
        Scene scene = new Scene(root);
        showPopup(scene, title);
    }
}
