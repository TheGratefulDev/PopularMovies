package com.notaprogrammer.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.notaprogrammer.popularmovies.database.AppDatabase;
import com.notaprogrammer.popularmovies.database.FavoriteEntry;

public class AddFavoriteViewModel extends ViewModel {

    private LiveData<FavoriteEntry> favoriteEntryLiveData;

    public AddFavoriteViewModel(AppDatabase appDatabase, long movieId) {
        favoriteEntryLiveData = appDatabase.favoriteDao().loadFavoriteByMovieId(movieId);
    }

    public LiveData<FavoriteEntry> getFavoriteEntryLiveData() {
        return favoriteEntryLiveData;
    }
}
