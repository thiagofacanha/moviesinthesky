package br.com.sandclan.moviesinthesky.view.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.Util.Constants;
import br.com.sandclan.moviesinthesky.assync.FetchMovieReviewsTask;
import br.com.sandclan.moviesinthesky.assync.FetchMovieTrailerTask;
import br.com.sandclan.moviesinthesky.data.MovieContract;
import br.com.sandclan.moviesinthesky.entity.Movie;
import br.com.sandclan.moviesinthesky.interfaces.AssyncTaskCompletListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;
    private List<String> mTrailers;
    private List<String> mReviews;
    @BindView(R.id.imageview_detail_poster)
    ImageView poster;
    @BindView(R.id.textview_summary)
    TextView summary;
    @BindView(R.id.textview_original_title)
    TextView originalTitle;
    @BindView(R.id.textview_detail_rate_value)
    TextView rateValue;
    @BindView(R.id.textview_release_date)
    TextView releaseDate;
    @BindView(R.id.imageview_trailer)
    ImageView trailerIcon;
    @BindView(R.id.imageview_favourite)
    ImageView favouriteIcon;

    @BindView(R.id.textview_users_review)
    TextView userReview;

    private String mTrailerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Constants.MOVIE)) {
            mMovie = (Movie) intent.getSerializableExtra(Constants.MOVIE);

            setTitle(mMovie.getTitle());
            Picasso.with(this).load(mMovie.getImageUrl()).error(R.drawable.image_not_found).placeholder(R.drawable.image_not_found).into(poster);
            summary.setText(mMovie.getSynopsis());
            originalTitle.setText(mMovie.getOriginalTitle());
            rateValue.setText(mMovie.getVoteAverage().toString());
            releaseDate.setText(mMovie.getReleaseDate());
            new FetchMovieTrailerTask(this, new FetchTrailersTaskCompleteListener()).execute(mMovie.getIdAPI());
            new FetchMovieReviewsTask(this, new FetchReviewTaskCompleteListener()).execute(mMovie.getIdAPI());
        }

        if (mMovie.isFavourite()) {
            favouriteIcon.setBackgroundResource(R.drawable.favourite);
        } else {
            favouriteIcon.setBackgroundResource(R.drawable.normal);
        }

    }

    private class FetchTrailersTaskCompleteListener implements AssyncTaskCompletListener<List<String>> {

        @Override
        public void onTaskComplete(List<String> result) {
            mTrailers = result;
            if (mTrailers.size() > 0) {
                mTrailerUrl = mTrailers.get(0);
            } else {
                trailerIcon.setClickable(false);
            }
            Picasso.with(DetailActivity.this).load(String.format(Constants.BASIC_YOUTUBE_THUMB_URL, mTrailerUrl)).error(R.drawable.image_not_found).placeholder(R.drawable.image_not_found).into(trailerIcon);

        }
    }

    private class FetchReviewTaskCompleteListener implements AssyncTaskCompletListener<List<String>> {

        @Override
        public void onTaskComplete(List<String> result) {
            mReviews = result;
            for (String review : result) {
                userReview.setText(userReview.getText() + review);
            }
        }
    }

    public void callYoutube(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASIC_YOUTUBE_URL + mTrailerUrl)));
    }

    public void favourite(View view) {
        ContentValues contentValues = new ContentValues();
        int result;
        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, mMovie.isFavourite() ? 0 : 1);
        String selectionClause = MovieContract.MovieEntry.COLUMN_ID_FROM_MOVIEDBAPI + "= ?";
        String[] selectionArgs = {String.valueOf(mMovie.getIdAPI())};
        result = getContentResolver().update(MovieContract.BASE_CONTENT_URI, contentValues, selectionClause, selectionArgs);

        if (result > 0) {
            mMovie.setFavourite(!mMovie.isFavourite());
            favouriteIcon.setBackgroundResource(mMovie.isFavourite() ? R.drawable.favourite : R.drawable.normal);
        } else {
            Toast.makeText(DetailActivity.this, "Error updating database!", Toast.LENGTH_LONG).show();
        }
    }


}
