package com.notaprogrammer.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private List<Movie> currentMovieList;
    private MoviesAdapter moviesAdapter;
    private Menu menu;
    private String selectedSortType;
    private String selectedActionBarTitle;
    Gson gson = new Gson();

    //Can not be private
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_error_message)
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

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SAVE_INSTANCE_MOVIE_LIST)){
                currentMovieList = convertMovieListJsonToObject(savedInstanceState.getString(SAVE_INSTANCE_MOVIE_LIST));
                moviesAdapter.updateList(currentMovieList);
            }
        }else{
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    getMoviesFromApi();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId){
            case R.id.action_sort_by_popular:
                refreshScreen(item, NetworkUtils.SORT_BY_POPULAR);
                break;

            case R.id.action_sort_by_top_rated:
                refreshScreen(item, NetworkUtils.SORT_BY_TOP_RATED);
                break;

            case R.id.action_sort_by_now_playing:
                refreshScreen(item, NetworkUtils.SORT_BY_NOW_PLAYING);
                break;

            case R.id.action_sort_by_my_favorite:
                refreshScreen(item, SORT_BY_MY_FAVORITE);
                break;


            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void refreshScreen(MenuItem item, String sortType){
        setSelectedActionBarTitle(item.getTitle().toString());
        setSelectedSortType(sortType);
        onRefresh();
    }

    private void displayErrorMessage() {
         emptyViewTv.setVisibility(View.VISIBLE);
    }

    private void clearErrorMessage(){
        emptyViewTv.setVisibility(View.GONE);
    }

    private void getMoviesFromApi() {

        clearErrorMessage();

        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(true);
        }

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getSelectedActionBarTitle());
        }

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = NetworkUtils.buildUrlWithSortType(getSelectedSortType());

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayErrorMessage();

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
                            displayErrorMessage();

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
            return selectedActionBarTitle = menu.findItem(R.id.action_sort_by_popular).getTitle().toString();
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

    @Override
    public void onRefresh() {
        if(selectedSortType.equals(SORT_BY_MY_FAVORITE) ){
            getMoviesFromDb();
        }else{
            getMoviesFromApi();
        }
    }

    private void getMoviesFromDb(){

        clearErrorMessage();

        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(true);
        }

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getSelectedActionBarTitle());
        }

        List<Movie> favoriteMovies = new ArrayList<>();


        if(favoriteMovies.size() == 0 ){
            displayErrorMessage();
        }

        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(false);
        }

        moviesAdapter.updateList(favoriteMovies);
        //scroll back to the top if user is at the bottom of the screen after sort changes
        moviesRv.smoothScrollToPosition(0);
    }

    public List<Movie> getCurrentMovieList() {
        return currentMovieList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_INSTANCE_MOVIE_LIST , gson.toJson(getCurrentMovieList()));
    }


    private List<Movie> convertMovieListJsonToObject(String jsonString) {
        Type listType = new TypeToken<List<Movie>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }
}
