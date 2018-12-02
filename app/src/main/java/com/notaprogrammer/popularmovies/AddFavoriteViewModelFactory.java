package com.notaprogrammer.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.notaprogrammer.popularmovies.database.AppDatabase;


public class AddFavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase appDatabase;
    private final long movieId;

    public AddFavoriteViewModelFactory(AppDatabase appDatabase, long movieId) {
        this.appDatabase = appDatabase;
        this.movieId = movieId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddFavoriteViewModel(appDatabase, movieId);
    }
}
