package com.savaava.mytvskeeper.models;

import com.savaava.mytvskeeper.utility.FormatString;

public abstract class Video implements java.io.Serializable {
    private final String title;
    private final String description;
    private final String releaseDate;
    private final boolean started,terminated;
    private final String rating;
    private final String id;
    private final String pathImage;

    public Video(String title, String description, String releaseDate, boolean started, boolean terminated, String rating, String id, String pathImage) {
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.started = started;
        this.terminated = terminated;
        this.rating = rating;
        this.id = id;
        this.pathImage = pathImage;
    }

    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public String getReleaseDate() {return releaseDate;}
    public boolean isStarted() {return started;}
    public boolean isTerminated() {return terminated;}
    public String getRating() {return rating;}
    public String getId(){return id;}
    /**
     * @return the path of Video's image in the format: /xxxxxxxx.jpg, null if it doesn't have it.
     */
    public String getPathImage() { return pathImage; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + id.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if(!(obj instanceof Video)) return false;

        Video objVideo = (Video)obj;
        return id.equals(objVideo.id);
    }

    @Override
    public String toString() {
        return "Title="+ FormatString.stringNormalize(title)+
                " | Description(length)"+description.length()+
                " | Release Date="+releaseDate+
                (started?" | Started":" | NotStarted")+
                (terminated?" | Terminated":" | NotTerminated")+
                " | Rating="+rating+
                " | ID="+id+
                " | path Image="+pathImage;
    }
}
