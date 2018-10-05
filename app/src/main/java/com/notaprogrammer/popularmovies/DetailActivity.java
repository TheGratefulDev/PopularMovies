package com.notaprogrammer.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.notaprogrammer.popularmovies.object.Movie;
import com.notaprogrammer.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;

public class DetailActivity extends AppCompatActivity {

    public static final String SELECTED_MOVIE = "SELECTED_MOVIE";

    private Movie selectedMovie;

    //Can not be private
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.image_details_poster)
    protected ImageView posterIv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_release_date)
    protected TextView releaseDateTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_vote_average)
    protected TextView voteAverageTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_overview)
    protected TextView overviewTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_release_date_label)
    protected TextView releaseDateLabelTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_vote_average_label)
    protected TextView voteAverageLabelTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_overview_label)
    protected TextView overviewLabelTv;

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

        updateReleaseDate();
        updateOverView();
        updatePosterView();
        updateVoteAverage();
    }

    private void updatePosterView() {

        if( TextUtils.isEmpty(selectedMovie.getPosterPath()) ){
            return;
        }

        if(posterIv == null){
            return;
        }

        HttpUrl httpUrl = NetworkUtils.buildImageUrl( NetworkUtils.IMAGE_SIZE_W342 ,selectedMovie.getPosterPath());
        Picasso.get().load(httpUrl.toString()).error(R.drawable.movie_user_placeholder).into(posterIv);
        posterIv.setContentDescription(R.string.poster_content_description_image_prefix + selectedMovie.getOriginalTitle());
    }

    private void updateReleaseDate(){
        updateInfoHideLabelAndInfoIfEmpty(selectedMovie.getReleaseDate(), releaseDateLabelTv ,releaseDateTv);
    }

    private void updateOverView(){
        updateInfoHideLabelAndInfoIfEmpty(selectedMovie.getOverView(), overviewLabelTv ,overviewTv);
    }

    private void updateVoteAverage(){
        String voteAverageWithVoteCount = MessageFormat.format("{0}({1})", String.valueOf(selectedMovie.getVoteAverage()), selectedMovie.getVoteCount());
        updateInfoHideLabelAndInfoIfEmpty(voteAverageWithVoteCount, voteAverageLabelTv ,voteAverageTv);
    }

    private void updateInfoHideLabelAndInfoIfEmpty(String text, TextView labelTextView, TextView infoTextView){
        if(labelTextView == null || infoTextView == null){
            return;
        }

        if(TextUtils.isEmpty(text)){
            labelTextView.setVisibility(View.GONE);
            infoTextView.setVisibility(View.GONE);
        }else{
            infoTextView.setText(text);
        }
    }


    private Movie convertJsonToMovieObject(String movieJson){
        return new Gson().fromJson(movieJson, Movie.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                //Up Button return to movie list, however, it does not keep the scroll state of the
                //recycler view. make the up button act like the back button

                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
}
