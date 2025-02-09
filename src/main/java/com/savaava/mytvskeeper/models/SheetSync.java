package com.savaava.mytvskeeper.models;

import java.util.Collection;

/**
 * methods to be invoked only if the user has synchronized the application with Google sheet
 */
public interface SheetSync {
    void addMovie(Movie movie);

    void removeMovie(Movie movie);

    void updateMovie(Movie movie);

    /**
     * Method invoked at the application starting
     * @param c is the movies collection of application
     * @return a collection empty if gs has all the movies of collection input {@code c}, otherwise returns a collection with
     * movies added in gs.
     */
    Collection<Movie> checkSynchronization(Collection<Movie> c);
}
