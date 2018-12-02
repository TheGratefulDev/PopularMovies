package com.notaprogrammer.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.notaprogrammer.popularmovies.R;
import com.notaprogrammer.popularmovies.object.Review;

import java.text.MessageFormat;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    final private ReviewItemClickListener onListItemClick;
    private List<Review> reviewList;

    public interface ReviewItemClickListener {
        void onListItemClick(Review selectedReview);
    }

    public ReviewAdapter(List<Review> reviewList, ReviewItemClickListener listener){
        this.reviewList = reviewList;
        this.onListItemClick = listener;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.view_item_review;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder reviewViewHolder, int position) {
        Review review = reviewList.get(position);
        reviewViewHolder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView contentTextView;
        final TextView authorTextView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            contentTextView =  itemView.findViewById(R.id.tv_content);
            authorTextView = itemView.findViewById(R.id.tv_author);
            itemView.setOnClickListener(this);
        }

        void bind(Review review) {
            contentTextView.setText(review.getContent());

            if(!TextUtils.isEmpty(review.getAuthor())){
                authorTextView.setText(MessageFormat.format("by {0}", review.getAuthor()));
            }

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Review selectedReview = reviewList.get(clickedPosition);
            onListItemClick.onListItemClick(selectedReview);
        }
    }

    public void updateList(List<Review> newReviewList){
        this.reviewList = newReviewList;
        notifyDataSetChanged();
    }

}

