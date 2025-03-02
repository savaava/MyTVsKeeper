# Functionalities to Add

## 1.3
- ~~Settare gli eventi per catturare il tasto della tastiera premuto dall'utente:~~
    - ~~ENTER e ESC in MainController~~
    - ~~CANC in MainController~~
    - ~~ENTER in AddVideoController per la ricerca (lente d'ingrandimento)~~
    - ~~ESC in DetailsVideoController~~
    - ENTER in DetailsVideoController per salvare (se Save è disabilitato non fa nulla)
    - ENTER in ConfigContrller → onSave (solo se save button non è disabilitato)
- ~~tipo primitivo per la duration (int duration) per i Movies~~
- ~~Binding ricerca anche per regista e release date~~
- ~~Video type menù a tendina in AddVideoController~~
- In Export il path non entra tutto nella schermata
- Prompt quando si passa col cursore sopra un'immagine bottone (come il cestino)
- Diagramma delle classi per vedere che interfacce mettere → meno accoppiamento
- Enum per Video type → tutti i controller ora possono funzionare senza setVideoIndex
- ~~note su ogni video (dakunto)~~
- ~~quando si aggiunge un video -> aprire la schermata di details~~
- ~~nessun rating se il video non è almeno started~~
- correggere TV e Anime Series al singolare
- ~~Image superfluo nei details~~
- ~~Aggiustare i details~~
    - ~~a sinistra l'immagine~~
    - ~~a destra: descrizione, generi, minutaggio, release, director, notes~~
- preview delle notes nelle 3 table view nel Main 
- attori in primo piano di ogni video
- preferenza sull'ordine (per release date in fase di inizializzazione) (ordine default: per title)
- ~~in Import → Or drag the .csv file here~~
- bottone in programma da vedere 
- ordinare correttamente i video per rating (rating come float)
- colorare la cella del rating in base al punteggio scelto
- studio che si è occupato dell'anime (da vedere se è presente nel db)
- creatore manga per anime (da vedere se è presente nel db)
- pulsante aggiorna per le serie o anime
- disclaimer quando si aggiunge tv series o anime series: non c'è la distinzione tra Tv series e anime
- in help -> shortcuts mostro cosa succede on key pressed nelle diverse scene
- cliccando sull'immagine nei dettagli si apre la scheda di google del relativo video utilizzando l'id (https://www.themoviedb.org/movie/19995-avatar)

## 1.4
- Statistiche: schermata per ogni video type (mockup)
    - rating
    - started/ terminated
    - generi
    - minuti
- Rewatch per migliore sincronizzazione con google sheet gisandro
- Export per file exel con le sole informazioni utili all'utente
- CSS stili da aggiungere
    - sfono colorato sfumato tipo intellij
    - font Lora

## 1.4
- Imbuto per i Tab → filtri
- Se il video è solo started -> checkpoint dove è stato interrotto (Lippy)
- Readme da migliorare