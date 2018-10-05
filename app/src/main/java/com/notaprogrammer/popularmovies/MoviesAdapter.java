package com.notaprogrammer.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.notaprogrammer.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.HttpUrl;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    final private ItemClickListener mOnClickListener;
    private List<Movie> movieList;

    public interface ItemClickListener {
        void onListItemClick(Movie selectedMovie);
    }

    MoviesAdapter(List<Movie> movieList, ItemClickListener listener){
        this.movieList = movieList;
        this.mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.view_item_movie;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position) {

        Movie movie = movieList.get(position);
        movieViewHolder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            movieImageView =  itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            HttpUrl httpUrl = NetworkUtils.buildImageUrl(movie.getPosterPath());
            Picasso.get().load(httpUrl.toString()).into(movieImageView);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            Movie selectedMovie = movieList.get(clickedPosition);
            mOnClickListener.onListItemClick(selectedMovie);
        }
    }

    void updateList(List<Movie> newMovieList){
        this.movieList = newMovieList;
        notifyDataSetChanged();
    }

}
