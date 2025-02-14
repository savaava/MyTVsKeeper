package com.savaava.mytvskeeper.services;

import com.savaava.mytvskeeper.models.Movie;

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
     * @return the collection of all the movies saved in gs until now
     */
    Collection<Movie> checkSynchronization();
}
