package com.notaprogrammer.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.notaprogrammer.popularmovies.R;
import com.notaprogrammer.popularmovies.object.Movie;
import com.notaprogrammer.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.HttpUrl;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    final private ItemClickListener onListItemClick;
    private List<Movie> movieList;

    public interface ItemClickListener {
        void onListItemClick(Movie selectedMovie);
    }

    public MoviesAdapter(List<Movie> movieList, ItemClickListener listener){
        this.movieList = movieList;
        this.onListItemClick = listener;
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

        final ImageView movieImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            movieImageView =  itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        void bind(Movie movie) {
            HttpUrl httpUrl = NetworkUtils.buildImageUrl( NetworkUtils.IMAGE_SIZE_W185, movie.getPosterPath() );
            Picasso.get().load(httpUrl.toString()).error(R.drawable.movie_user_placeholder).into(movieImageView);
            movieImageView.setContentDescription(R.string.poster_content_description_image_prefix + movie.getOriginalTitle());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie selectedMovie = movieList.get(clickedPosition);
            onListItemClick.onListItemClick(selectedMovie);
        }
    }

    public void updateList(List<Movie> newMovieList){
        this.movieList = newMovieList;
        notifyDataSetChanged();
    }

}
