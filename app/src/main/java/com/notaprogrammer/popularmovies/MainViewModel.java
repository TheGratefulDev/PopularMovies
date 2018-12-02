package com.notaprogrammer.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.notaprogrammer.popularmovies.database.AppDatabase;
import com.notaprogrammer.popularmovies.database.FavoriteEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavoriteEntry>> favorites;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        favorites = database.favoriteDao().loadAllFavorites();
    }

    LiveData<List<FavoriteEntry>> getFavorites() {
        return favorites;
    }
}
