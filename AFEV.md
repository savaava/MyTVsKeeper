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

## 1.2 - Current Version
- **Import methods** for each version up to this one, ensuring retrocompatibilty
    - ```ImportController.java```
    - ```Import.fxml```
    - drag and drop funtionality 
- Fixing bugs
- Enhanced design quality 
- **initPreviewColumn** method to add image in each cell of preview column in addVideo scene 
    - Enhanced the performance of this functionality 
- Caching of video images for Main
- Controlled the keys pressing (ENTER or ESC) by the user

## 1.3 - Coming soon
- Added the possibility to associate some notes to your videos 
- Totally modified the Scene of Video Details

## 1.4 - Coming soon
- Adding statistics 
    - Scene fxml and controller
    - rating (pie)
    - started/terminated (pie)
    - genres ()
