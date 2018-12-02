package com.notaprogrammer.popularmovies.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorite")
public class FavoriteEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long movieId;
    private String movieJson;

    @Ignore
    public FavoriteEntry(long movieId, String movieJson) {
        this.movieId = movieId;
        this.movieJson = movieJson;
    }

    public FavoriteEntry(int id, long movieId, String movieJson) {
        this.id = id;
        this.movieId = movieId;
        this.movieJson = movieJson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getMovieJson() {
        return movieJson;
    }

    public void setMovieJson(String movieJson) {
        this.movieJson = movieJson;
    }
}
