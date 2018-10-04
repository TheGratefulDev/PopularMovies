package com.notaprogrammer.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    final private ItemClickListener mOnClickListener;
    final private List<Movie> movieList;


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
            //TODO CLEAN UP
            Picasso.get().load("https://image.tmdb.org/t/p/w185//"+movie.getPosterPath()).into(movieImageView);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            Movie selectedMovie = movieList.get(clickedPosition);
            mOnClickListener.onListItemClick(selectedMovie);
        }
    }

    void orderByPopular() {
        Collections.sort(movieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return Double.compare(o2.getPopularity(), o1.getPopularity());

            }
        });

        notifyDataSetChanged();
    }

    void orderByHighestRated() {
        Collections.sort(movieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return Double.compare(o2.getVoteAverage(), o1.getVoteAverage());
            }
        });

        notifyDataSetChanged();
    }

    void orderByMostVoted() {
        Collections.sort(movieList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return Double.compare(o2.getVoteCount(), o1.getVoteCount());
            }
        });

        notifyDataSetChanged();
    }


}
