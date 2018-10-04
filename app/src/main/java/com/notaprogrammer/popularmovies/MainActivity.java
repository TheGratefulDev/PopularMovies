package com.notaprogrammer.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.themoviedb.org")
                .addPathSegment("3")
                .addPathSegment("movie")
                .addPathSegment("popular")
                .addQueryParameter("api_key", BuildConfig.TheMovieDBAPIKey)
                .build();
        Log.d("TAG", url.toString() + "");
        Request request = new Request.Builder()
                .url(url)
                .build();


        progressBar.setVisibility(View.VISIBLE);
        client.newCall(request)
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse( @NonNull Call call, @NonNull final Response response) throws IOException {

                        if (response.isSuccessful() && response.body() !=null) {
                            List<Movie> movieList = new ArrayList<>();
                            Gson gson = new Gson();

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONArray moviesArray = jsonObject.getJSONArray("results");

                                Type listType = new TypeToken<List<Movie>>(){}.getType();
                                List<Movie> movies =  gson.fromJson(moviesArray.toString(), listType);
                                movieList = movies;
                                Log.d("TAG", "successResponse: " + movies.get(5).getOriginalTitle());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            final List<Movie> finalMovieList = movieList;


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    setMovieList(finalMovieList);

                                    progressBar.setVisibility(View.GONE);

                                    moviesAdapter = new MoviesAdapter(finalMovieList, MainActivity.this);
                                    moviesRv.setAdapter(moviesAdapter);

                                    moviesRv.setLayoutManager( new GridLayoutManager(getApplicationContext(), 2) );
                                    moviesRv.setHasFixedSize(true);
                                    moviesAdapter.notifyDataSetChanged();
                                }
                            });



                        } else {
                            throw new IOException("Unexpected code " + response);
                        }
                    }
                });


    }

    private void setMovieList(List<Movie> finalMovieList) {
        movieList = finalMovieList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        Context context = MainActivity.this;

        switch (itemThatWasClickedId){
            case R.id.action_sort_by_popular:
                moviesAdapter.orderByPopular();
                break;

            case R.id.action_sort_by_highest_rated:
                moviesAdapter.orderByHighestRated();
                break;

            case R.id.action_sort_by_most_voted:
                moviesAdapter.orderByMostVoted();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void displayErrorMessage(){
         emptyViewTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
