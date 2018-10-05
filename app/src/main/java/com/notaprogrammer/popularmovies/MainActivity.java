package com.notaprogrammer.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickListener{

    @BindView(R.id.tv_error_message)
    TextView emptyViewTv;

    @BindView(R.id.rv_movie_choices)
    RecyclerView moviesRv;

    @BindView(R.id.pg_loading)
    ProgressBar progressBar;

    MoviesAdapter moviesAdapter;

    List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        moviesAdapter = new MoviesAdapter(movieList, MainActivity.this);
        moviesRv.setAdapter(moviesAdapter);
        moviesRv.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2) );
        moviesRv.setHasFixedSize(true);

        getMoviesFromApi(NetworkUtils.SORT_BY_POPULAR);
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
                getMoviesFromApi(NetworkUtils.SORT_BY_POPULAR);
                break;

            case R.id.action_sort_by_top_rated:
                getMoviesFromApi(NetworkUtils.SORT_BY_TOP_RATED);
                break;

            case R.id.action_sort_by_upcoming:
                getMoviesFromApi(NetworkUtils.SORT_BY_UPCOMING);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayProgressBar(boolean visible){
        if(visible){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }

    }

    private void displayErrorMessage(){
         emptyViewTv.setVisibility(View.VISIBLE);
    }

    private void getMoviesFromApi(String sortBy){

        displayProgressBar(true);

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = NetworkUtils.buildUrlWithSortType(sortBy);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayProgressBar(false);
                        displayErrorMessage();
                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() !=null) {
                    List<Movie> movieList = new ArrayList<>();
                    Gson gson = new Gson();

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
                            moviesAdapter.updateList(finalMovieList);
                            displayProgressBar(false);
                        }
                    });

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayProgressBar(false);
                            displayErrorMessage();
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


        Toast.makeText(this, selectedMovie.getOriginalTitle(), Toast.LENGTH_SHORT).show();
    }
}
