
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

    private static final String VERSION3 = "3";
    private static final String MOVIE = "movie";
    private static final String API_KEY = "api_key";

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