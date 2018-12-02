package com.notaprogrammer.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notaprogrammer.popularmovies.adapter.ReviewAdapter;
import com.notaprogrammer.popularmovies.adapter.VideosAdapter;
import com.notaprogrammer.popularmovies.database.AppDatabase;
import com.notaprogrammer.popularmovies.database.FavoriteEntry;
import com.notaprogrammer.popularmovies.object.Movie;
import com.notaprogrammer.popularmovies.object.Review;
import com.notaprogrammer.popularmovies.object.Video;
import com.notaprogrammer.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
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

import static com.notaprogrammer.popularmovies.object.Video.SITE_YOUTUBE;

public class DetailActivity extends AppCompatActivity implements VideosAdapter.ItemClickListener, ReviewAdapter.ReviewItemClickListener {

    public static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    private static final String SAVE_INSTANCE_MOVIE = "SAVE_INSTANCE_MOVIE";

    //Can not be private
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.image_details_poster)
    protected ImageView posterIv;

    //release date
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_release_date_label)
    protected TextView releaseDateLabelTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_release_date)
    protected TextView releaseDateTv;

    //Voting Average
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_vote_average_label)
    protected TextView voteAverageLabelTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_vote_average)
    protected TextView voteAverageTv;

    //Overview
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_overview_label)
    protected TextView overviewLabelTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_overview)
    protected TextView overviewTv;

    //Videos
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_videos_label)
    protected TextView videosLabelTv;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rv_videos)
    protected RecyclerView videosRv;

    //Review
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_review_label)
    protected TextView reviewsLabelTV;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rv_reviews)
    protected RecyclerView reviewsRv;

    private Gson gson = new Gson();
    private Movie selectedMovie;

    private VideosAdapter videosAdapter;
    private List<Video> videoList;

    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;

    private AppDatabase appDatabase;

    private FavoriteEntry favoriteEntry;

    public FavoriteEntry getFavoriteEntry() {
        return favoriteEntry;
    }

    public void setFavoriteEntry(FavoriteEntry favoriteEntry) {
        this.favoriteEntry = favoriteEntry;
    }

    public DetailActivity() {
        videoList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        appDatabase = AppDatabase.getInstance(this.getApplicationContext());

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SAVE_INSTANCE_MOVIE)){
                selectedMovie = convertJsonToMovieObject(savedInstanceState.getString(SAVE_INSTANCE_MOVIE));
            }
        }else{
            Intent intent = getIntent();

            if(!intent.hasExtra(SELECTED_MOVIE)){
                Toast.makeText(this, R.string.error_problem_loading_detail, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String selectedMovieJson = intent.getStringExtra(SELECTED_MOVIE);
            selectedMovie = convertJsonToMovieObject(selectedMovieJson);
        }

        AddFavoriteViewModelFactory factory = new AddFavoriteViewModelFactory(appDatabase, selectedMovie.getId());
        final AddFavoriteViewModel viewModel = ViewModelProviders.of(this, factory).get(AddFavoriteViewModel.class);
        viewModel.getFavoriteEntryLiveData().observe(this, new Observer<FavoriteEntry>() {
            @Override
            public void onChanged(@Nullable FavoriteEntry favoriteEntry) {
                setFavoriteEntry(favoriteEntry);
                selectedMovie.setFavorite( favoriteEntry != null  );
            }
        });

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(selectedMovie.getTitle());
        }

        updateReleaseDate();
        updateOverView();
        updatePosterView();
        updateVoteAverage();
        updateVideos();
        updateReviews();
    }



    private void updateReviews() {
        if(reviewsRv == null){
            return;
        }

        reviewAdapter = new ReviewAdapter(reviewList, DetailActivity.this);
        reviewsRv.setAdapter(reviewAdapter);
        reviewsRv.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        reviewsRv.setHasFixedSize(true);

        if(selectedMovie.getReviewList().size() == 0) {
            OkHttpClient client = new OkHttpClient();
            HttpUrl url = NetworkUtils.buildReviewsUrl(selectedMovie.getId());
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            removeReviewsView();
                        }
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() !=null) {

                        List<Review> reviewList = null;


                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray reviewArray = jsonObject.getJSONArray(NetworkUtils.RESULT_KEY_RESULTS);

                            Type listType = new TypeToken<List<Review>>(){}.getType();
                            reviewList = gson.fromJson(reviewArray.toString(), listType);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(reviewList == null || reviewList.size() == 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    removeReviewsView();
                                }
                            });
                            return;
                        }

                        final List<Review> finalReviewList = reviewList;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reviewAdapter.updateList(finalReviewList);
                                selectedMovie.setReviewList(finalReviewList);
                            }
                        });

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                removeReviewsView();
                            }
                        });

                    }
                }
            });
        }else {
            reviewAdapter.updateList(selectedMovie.getReviewList());
        }

    }

    private void updateVideos() {
        if(videosRv == null){
            return;
        }

        videosAdapter = new VideosAdapter(this, videoList, DetailActivity.this);
        videosRv.setAdapter(videosAdapter);
        videosRv.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        videosRv.setHasFixedSize(true);

        if(selectedMovie.getVideoList().size() == 0 ){
            OkHttpClient client = new OkHttpClient();
            HttpUrl url = NetworkUtils.buildVideosUrl(selectedMovie.getId());
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            removeVideosView();
                        }
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() !=null) {

                        List<Video> videoList = null;

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONArray trailersArray = jsonObject.getJSONArray(NetworkUtils.RESULT_KEY_RESULTS);

                            Type listType = new TypeToken<List<Video>>(){}.getType();
                            videoList = gson.fromJson(trailersArray.toString(), listType);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(videoList == null || videoList.size() == 0 ){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    removeVideosView();
                                }
                            });
                            return;
                        }

                        final List<Video> finalVideoList = videoList;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                videosAdapter.updateList(finalVideoList);
                                selectedMovie.setVideoList(finalVideoList);
                            }
                        });

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                removeVideosView();
                            }
                        });

                    }
                }
            });
        }else{
            videosAdapter.updateList(selectedMovie.getVideoList());
        }

    }

    private void removeVideosView(){
        videosLabelTv .setVisibility(View.GONE);
        videosRv.setVisibility(View.GONE);
    }


    private void removeReviewsView(){
        reviewsLabelTV .setVisibility(View.GONE);
        reviewsRv.setVisibility(View.GONE);
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
        return gson.fromJson(movieJson, Movie.class);
    }


    @Override
    public void onListItemClick(Video video) {

        if(video.getSite().equals( SITE_YOUTUBE) ){

            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getYoutubeVendorSpecificUri()));
            boolean isUnableToPlayVideoWithApp = false;
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                isUnableToPlayVideoWithApp = true;
            }

            if(isUnableToPlayVideoWithApp){
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getYouTubeLink()));
                startActivity(webIntent);
            }
        }

    }

    @Override
    public void onListItemClick(Review selectedReview) {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra(ReviewActivity.SELECTED_REVIEW, gson.toJson(selectedReview));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_INSTANCE_MOVIE, selectedMovie.toJsonString());
    }

    MenuItem favoriteMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        favoriteMenuItem = menu.findItem(R.id.action_add_to_my_favorite);
        updateFavoriteStatus();
        return true;
    }

    private void updateFavoriteStatus() {
        if (selectedMovie.isFavorite() ) {
            favoriteMenuItem.setIcon(R.drawable.favorite_solid);
        } else {
            favoriteMenuItem.setIcon(R.drawable.favorite);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId){
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_add_to_my_favorite:

                performFavoriteToggleClick();
                invalidateOptionsMenu();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performFavoriteToggleClick() {

        if( selectedMovie.isFavorite() ){

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.favoriteDao().deleteTask(getFavoriteEntry());
                }
            });
            selectedMovie.setFavorite( false );

        }else{

            final FavoriteEntry favoriteEntry = new FavoriteEntry(selectedMovie.getId(), selectedMovie.toJsonString());
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.favoriteDao().insertTask(favoriteEntry);
                }
            });
            selectedMovie.setFavorite( true );

        }
    }



}
