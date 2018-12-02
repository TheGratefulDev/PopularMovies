package com.notaprogrammer.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    LiveData<List<FavoriteEntry>> loadAllFavorites();

    @Insert
    void insertTask(FavoriteEntry favoriteEntry);

    @Delete
    void deleteTask(FavoriteEntry taskEntry);

    @Query("SELECT * FROM favorite WHERE movieId = :movieId")
    LiveData<FavoriteEntry> loadFavoriteByMovieId(long movieId);
}
