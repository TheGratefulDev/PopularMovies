package com.notaprogrammer.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.notaprogrammer.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;

public class DetailActivity extends AppCompatActivity {

    public static final String SELECTED_MOVIE = "SELECTED_MOVIE";

    //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
    Movie selectedMovie;

     @BindView(R.id.image_details_poster)
     ImageView posterIv;

     @BindView(R.id.tv_release_date)
     TextView releaseDateTv;

     @BindView(R.id.tv_vote_average)
     TextView voteAverageTv;

     @BindView(R.id.tv_overview)
     TextView overviewTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if(!intent.hasExtra(SELECTED_MOVIE)){
            Toast.makeText(this, R.string.error_problem_loading_detail, Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedMovieJson = intent.getStringExtra(SELECTED_MOVIE);
        selectedMovie = convertJsonToMovieObject(selectedMovieJson);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(selectedMovie.getOriginalTitle());
        }


        releaseDateTv.setText(selectedMovie.getReleaseDate());
        voteAverageTv.setText(MessageFormat.format("{0}({1})", String.valueOf(selectedMovie.getVoteAverage()), selectedMovie.getVoteCount()));
        overviewTv.setText(selectedMovie.getOverView());
        HttpUrl httpUrl = NetworkUtils.buildImageUrl(selectedMovie.getPosterPath());
        Picasso.get().load(httpUrl.toString()).into(posterIv);
    }

    private Movie convertJsonToMovieObject(String movieJson){
        return new Gson().fromJson(movieJson, Movie.class);
    }

}
