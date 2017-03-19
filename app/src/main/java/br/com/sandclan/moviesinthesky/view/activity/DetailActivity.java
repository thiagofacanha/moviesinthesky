package br.com.sandclan.moviesinthesky.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.Util.Constants;
import br.com.sandclan.moviesinthesky.assync.FetchMovieDetailsTask;
import br.com.sandclan.moviesinthesky.entity.Movie;
import br.com.sandclan.moviesinthesky.interfaces.AssyncTaskCompletListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;
    private List<String> mTrailers;
    @BindView(R.id.imageview_detail_poster)
    ImageView poster;
    @BindView(R.id.textview_summary)
    TextView summary;
    @BindView(R.id.textview_original_title)
    TextView originalTitle;
    @BindView(R.id.textview_detail_rate_value)
    TextView rateValue;
    @BindView(R.id.textview_release_date)
    TextView release_date;
    @BindView(R.id.imageview_trailer)
    ImageView trailer_icon;
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
            originalTitle.setText(mMovie.getOriginal_title());
            rateValue.setText(mMovie.getVoteAverage().toString());
            release_date.setText(mMovie.getReleaseDate());
            new FetchMovieDetailsTask(this, new FetchTrailersTaskCompleteListener()).execute(mMovie.getId());
        }

    }

    private class FetchTrailersTaskCompleteListener implements AssyncTaskCompletListener<List<String>> {

        @Override
        public void onTaskComplete(List<String> result) {
            mTrailers = result;
            if (mTrailers.size() > 0) {
                mTrailerUrl = mTrailers.get(0);
            }else{
                trailer_icon.setClickable(false);
            }
            Picasso.with(DetailActivity.this).load(String.format(Constants.BASIC_YOUTUBE_THUMB_URL, mTrailerUrl)).error(R.drawable.image_not_found).placeholder(R.drawable.image_not_found).into(trailer_icon);

        }
    }

    public void callYoutube(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASIC_YOUTUBE_URL + mTrailerUrl)));
    }


}
