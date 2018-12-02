
package com.notaprogrammer.popularmovies.utilities;

import com.notaprogrammer.popularmovies.BuildConfig;

import okhttp3.HttpUrl;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    public static final String SORT_BY_NOW_PLAYING = "now_playing";
    public static final String SORT_BY_TOP_RATED = "top_rated";
    public static final String SORT_BY_POPULAR = "popular";
    public static final String IMAGE_SIZE_W185 = "w185";
    public static final String IMAGE_SIZE_W342 = "w342";
    public static final String RESULT_KEY_RESULTS = "results";

    private static final String HTTPS = "https";
    private static final String THE_MOVIE_DB = "api.themoviedb.org";
    private static final String TMDB = "image.tmdb.org";
    private static final String T = "t";
    private static final String P = "p";
    private static final String V = "v";

    private static final String YOU_TUBE_IMAGE = "img.youtube.com";
    private static final String YOU_TUBE = "www.youtube.com";
    private static final String VIDEO_IMAGE = "vi";
    private static final String DEFAULT_THUMBNAIL_IMAGE = "0.jpg";

    private static final String VERSION3 = "3";
    private static final String MOVIE = "movie";
    private static final String API_KEY = "api_key";
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";
    private static final String WATCH = "watch";

    public static HttpUrl buildReviewsUrl(long id){

        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(THE_MOVIE_DB)
                .addPathSegment(VERSION3)
                .addPathSegment(MOVIE)
                .addPathSegment(String.valueOf(id))
                .addPathSegment(REVIEWS)
                .addQueryParameter(API_KEY, BuildConfig.TheMovieDBAPIKey)
                .build();
    }

    public static HttpUrl buildYoutubeThumbnailUrl(String key){

        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(YOU_TUBE_IMAGE)
                .addPathSegment(VIDEO_IMAGE)
                .addPathSegment(key)
                .addPathSegment(DEFAULT_THUMBNAIL_IMAGE)
                .build();
    }

    //http://www.youtube.com/watch?v=cxLG2wtE7TM"


    public static HttpUrl buildYoutubeUrl(String key){

        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(YOU_TUBE)
                .addPathSegment(WATCH)
                .addQueryParameter(V, key)
                .build();
    }


    public static HttpUrl buildVideosUrl(long id){

        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(THE_MOVIE_DB)
                .addPathSegment(VERSION3)
                .addPathSegment(MOVIE)
                .addPathSegment(String.valueOf(id))
                .addPathSegment(VIDEOS)
                .addQueryParameter(API_KEY, BuildConfig.TheMovieDBAPIKey)
                .build();
    }


    public static HttpUrl buildUrlWithSortType(String sortBy){

        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(THE_MOVIE_DB)
                .addPathSegment(VERSION3)
                .addPathSegment(MOVIE)
                .addPathSegment(sortBy)
                .addQueryParameter(API_KEY, BuildConfig.TheMovieDBAPIKey)
                .build();
    }

    public static HttpUrl buildImageUrl(String imageSize, String key){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(TMDB)
                .addPathSegment(T)
                .addPathSegment(P)
                .addPathSegment(imageSize)
                .addEncodedPathSegment(removeFilePath(key))
                .build();
    }

    private static String removeFilePath(String key){
        if(key.startsWith("/")){
            return key.replace("/", "");
        }
        return key;
    }

}