package com.notaprogrammer.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.notaprogrammer.popularmovies.object.Review;

import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {

    public static final String SELECTED_REVIEW = "SELECTED_REVIEW";
    private static final String SAVE_INSTANCE_REVIEW = "SAVE_INSTANCE_REVIEW";
    private Review selectedReview = null;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_content)
    protected TextView contentTextView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_author)
    protected TextView authorTextView;

    String reviewState;
    Gson gson = new Gson();


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_INSTANCE_REVIEW , gson.toJson(selectedReview));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);



        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SAVE_INSTANCE_REVIEW)){
                selectedReview = convertReviewJsonStringToObject(savedInstanceState.getString(SAVE_INSTANCE_REVIEW));
            }
        }else{
            Intent intent = getIntent();

            if(!intent.hasExtra(SELECTED_REVIEW)){
                Toast.makeText(this, R.string.errorProblem_loading_review, Toast.LENGTH_SHORT).show();
                finish();
            }

            String reviewJsonString = intent.getStringExtra(SELECTED_REVIEW);
            selectedReview = convertReviewJsonStringToObject( reviewJsonString );
        }

        contentTextView.setMaxLines(Integer.MAX_VALUE);
        contentTextView.setText(selectedReview.getContent());

        authorTextView.setText(selectedReview.getAuthor());


        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(MessageFormat.format("Review Written By {0}", selectedReview.getAuthor()));
        }


    }

    private Review convertReviewJsonStringToObject(String reviewJsonString) {
        return gson.fromJson(reviewJsonString, Review.class);
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
