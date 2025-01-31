# Application Evolution
AFEV (Application Funtionalities Evolution based on Versions):

## 1.0
- Model of project, based on MVC style (Model View Controller)
    - 3 Video types: Movie | TV Serie | Anime Serie
    - ```Video```, ```Movie```, ```TVSerie```
    - ```VideoKeeper.java```
- Main view and its controller
    - ```Main.fxml```
    - ```MainController.java```
    - TabPane with 3 Tabs for all three Video types, with a ListView respectively
- Application **configuration**
    - ```Config.fxml```
    - ```ConfigController.java```
- **Searching** and **Adding** a Video from TMDB, using its API
    - ```AddVideo.fxml```
    - ```AddVideoController.java```
- **Export** methods
    - ```Export.fxml```
    - ```ExportController.java```
- More video **Details**
    - ```VideoDetails.fxml```
    - ```VideoDetails.java```
- **Deleting** the Video from the list
- **Searching** a Video in the list with FilteredList

## 1.1 
- Adding **image** to each video, when it's available
    - New attribute ```String pathImage``` in ```Video.java```
    - Export method update for this version

## 1.2
- **Import methods** for each version up to this one, ensuring retrocompatibilty


## 1.3
- Adding statistics for rating,
