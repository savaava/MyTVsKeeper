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
    - ```ImportController.java```
    - ```Import.fxml```
    - drag and drop funtionality 
- Fixing bugs
- Enhanced design quality 
- **initPreviewColumn** method to add image in each cell of preview column in addVideo scene 
    - Enhanced the performance of this functionality 
- Caching of video images for Main
- Controlled the keys pressing (ENTER or ESC) by the user

## 1.3 - Current Version
- Added the possibility to associate some notes to your videos 
- Totally modified the Scene of Video Details

## 2.0 - Coming soon
- **Total Refactor** to improve **UI - UX experience**
    - Add **CSS styles**
    - **New design** for Main, AddVideo, VideoDetails, Config, Export, Import
- Adding **DB using SQLite** to save user's videos
    - Adding a new class **```VideosDAO.java```** to manage the database
    - Improving the **portability** of the application (no need to save the videos in a file)
    - When the user changes the application version, the **videos will be saved in the database** in the same way
- Adding **interaction with other applications** using **Network API**
    - The application is able to **send and receive data** from other applications
    - The application is always **listening to the network** for incoming data from other applications at the port **50005**
    - The user can **send a connection request** (**client**) to another application (**server**) specifying the **IP address:50005**
    - There's a **key exchange** to ensure the **security** of the connection
    - The user can **send data (videos) as a JSON object** containing the video information
    - The user server **receives the data** and chooses to **accept all the videos or not or just some of them**

## 2.1 - Coming soon
- Adding statistics 
    - Scene fxml and controller
    - rating (pie)
    - started/terminated (pie)
    - genres ()
