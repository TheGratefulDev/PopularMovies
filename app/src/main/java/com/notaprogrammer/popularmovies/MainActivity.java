package com.notaprogrammer.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notaprogrammer.popularmovies.adapter.MoviesAdapter;
import com.notaprogrammer.popularmovies.database.FavoriteEntry;
import com.notaprogrammer.popularmovies.object.Movie;
import com.notaprogrammer.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String SORT_BY_MY_FAVORITE = "my_favorite";
    private static final String SAVE_INSTANCE_MOVIE_LIST = "SAVE_INSTANCE_MOVIE_LIST";
    private static final String SAVE_INSTANCE_ACTION_BAR_TEXT = "SAVE_INSTANCE_ACTION_BAR_TEXT";
    private static final String SAVE_INSTANCE_CURRENT_SORT_TYPE = "SAVE_INSTANCE_CURRENT_SORT_TYPE";
    private List<Movie> currentMovieList;
    private MoviesAdapter moviesAdapter;
    private String selectedSortType;
    private String selectedActionBarTitle;

    Gson gson = new Gson();

    //Can not be private
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_empty_message)
    protected TextView emptyViewTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rv_movie_choices)
    protected RecyclerView moviesRv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spl_movie_choices)
    protected SwipeRefreshLayout swipeRefreshLayout;

    public MainActivity() {
        currentMovieList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        moviesAdapter = new MoviesAdapter(currentMovieList, MainActivity.this);
        moviesRv.setAdapter(moviesAdapter);
        moviesRv.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2) );
        moviesRv.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(this);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SAVE_INSTANCE_MOVIE_LIST)){
                currentMovieList = convertMovieListJsonToObject(savedInstanceState.getString(SAVE_INSTANCE_MOVIE_LIST));
                moviesAdapter.updateList(currentMovieList);
            }

            if(savedInstanceState.containsKey(SAVE_INSTANCE_ACTION_BAR_TEXT)){
                selectedActionBarTitle = savedInstanceState.getString(SAVE_INSTANCE_ACTION_BAR_TEXT);
            }

            if(savedInstanceState.containsKey(SAVE_INSTANCE_CURRENT_SORT_TYPE)){
                selectedSortType = savedInstanceState.getString(SAVE_INSTANCE_CURRENT_SORT_TYPE);
            }

            updateActionBar();

        }else{

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    getMoviesFromApi();
                }
            });

        }
        setupViewModel();
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<FavoriteEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteEntry> favoriteEntries) {

                boolean isFavorite = selectedSortType != null && selectedSortType.equals(SORT_BY_MY_FAVORITE);

                moviesAdapter.updateFavoriteList(favoriteEntries,isFavorite);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId){

            case R.id.action_sort_by_popular:
                refreshScreen(NetworkUtils.SORT_BY_POPULAR, getString(R.string.Popular));
                break;

            case R.id.action_sort_by_top_rated:
                refreshScreen(NetworkUtils.SORT_BY_TOP_RATED, getString(R.string.Top_Rated));
                break;

            case R.id.action_sort_by_now_playing:
                refreshScreen( NetworkUtils.SORT_BY_NOW_PLAYING, getString(R.string.Now_Playing));
                break;

            case R.id.action_sort_by_my_favorite:
                refreshScreen(SORT_BY_MY_FAVORITE, getString(R.string.My_Favorites));
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshScreen( String sortType, String selectedActionBarTitle){
        setSelectedActionBarTitle(selectedActionBarTitle);
        setSelectedSortType(sortType);
        onRefresh();
    }

    private void displayEmptyMessage() {
         emptyViewTv.setVisibility(View.VISIBLE);
    }

    private void clearEmptyMessage(){
        emptyViewTv.setVisibility(View.GONE);
    }

    private void updateActionBar(){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getSelectedActionBarTitle());
        }
    }

    private void getMoviesFromApi() {

        clearEmptyMessage();

        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(true);
        }

        updateActionBar();

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = NetworkUtils.buildUrlWithSortType(getSelectedSortType());

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayEmptyMessage();

                        if(swipeRefreshLayout != null){
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() !=null) {
                    List<Movie> movieList = new ArrayList<>();

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray moviesArray = jsonObject.getJSONArray(NetworkUtils.RESULT_KEY_RESULTS);

                        Type listType = new TypeToken<List<Movie>>(){}.getType();
                        movieList = gson.fromJson(moviesArray.toString(), listType);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final List<Movie> finalMovieList = movieList;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentMovieList = finalMovieList;
                            moviesAdapter.updateList(finalMovieList);
                            //scroll back to the top if user is at the bottom of the screen after sort changes
                            moviesRv.smoothScrollToPosition(0);
                            if(swipeRefreshLayout != null){
                                swipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    });

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayEmptyMessage();

                            if(swipeRefreshLayout != null){
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onListItemClick(Movie selectedMovie) {
        String movieJson = new Gson().toJson(selectedMovie);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.SELECTED_MOVIE, movieJson);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private String getSelectedActionBarTitle() {
        if(TextUtils.isEmpty(selectedActionBarTitle)){
            return selectedActionBarTitle = getString(R.string.Popular);
        }
        return selectedActionBarTitle;
    }

    private void setSelectedActionBarTitle(String selectedActionBarTitle) {
        this.selectedActionBarTitle = selectedActionBarTitle;
    }

    private void setSelectedSortType(String selectedSortType) {
        this.selectedSortType = selectedSortType;
    }

    private String getSelectedSortType() {
        if(TextUtils.isEmpty(selectedSortType)){
            return selectedSortType = NetworkUtils.SORT_BY_POPULAR;
        }
        return selectedSortType;
    }

    public List<Movie> getCurrentMovieList() {
        return currentMovieList;
    }

    @Override
    public void onRefresh() {

        moviesAdapter.clear();

        if(selectedSortType != null && selectedSortType.equals(SORT_BY_MY_FAVORITE) ){
            getMoviesFromDb();
        }else{
            getMoviesFromApi();
        }
    }


    private void getMoviesFromDb(){

        clearEmptyMessage();

        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(true);
        }

        updateActionBar();

        List<Movie> movieList =  moviesAdapter.getMovieListFromFavorites();

        if( movieList != null && movieList.size() == 0 ){
            displayEmptyMessage();
        }

        currentMovieList = movieList;
        moviesAdapter.updateList(movieList);
        //scroll back to the top if user is at the bottom of the screen after sort changes
        moviesRv.smoothScrollToPosition(0);
        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(false);
        }

        moviesRv.smoothScrollToPosition(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_INSTANCE_MOVIE_LIST , gson.toJson(getCurrentMovieList()));
        outState.putString(SAVE_INSTANCE_ACTION_BAR_TEXT , getSelectedActionBarTitle());
        outState.putString(SAVE_INSTANCE_CURRENT_SORT_TYPE , getSelectedSortType());
    }

    private List<Movie> convertMovieListJsonToObject(String jsonString) {
        Type listType = new TypeToken<List<Movie>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }

}
