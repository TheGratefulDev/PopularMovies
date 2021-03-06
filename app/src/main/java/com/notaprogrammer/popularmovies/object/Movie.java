package com.notaprogrammer.popularmovies.object;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.notaprogrammer.popularmovies.database.FavoriteEntry;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Movie {

    @SerializedName("id") long id;

    @SerializedName("video") boolean video;

    @SerializedName("adult") boolean adult;

    @SerializedName("vote_count") int voteCount;

    @SerializedName("popularity") double popularity;

    @SerializedName("vote_average") double voteAverage;

    @SerializedName("poster_path") String posterPath;

    @SerializedName("original_language") String originalLanguage;

    @SerializedName("original_title") String originalTitle;

    @SerializedName("title") String title;

    @SerializedName("backdrop_path") String backDropPath;

    @SerializedName("overview") String overView;

    @SerializedName("release_date") String releaseDate;

    @SerializedName("genre_ids") int genreIds[];

    List<Review> reviewList = new ArrayList<>();

    List<Video> videoList = new ArrayList<>();

    boolean isFavorite = false;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getBackDropPath() {
        return backDropPath;
    }

    public void setBackDropPath(String backDropPath) {
        this.backDropPath = backDropPath;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String toJsonString(){
        return new Gson().toJson(this);
    }

    public static Movie convertToMovie(FavoriteEntry favoriteEntry) {
        return new Gson().fromJson(favoriteEntry.getMovieJson(), Movie.class);
    }

}
