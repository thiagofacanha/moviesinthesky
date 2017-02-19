package br.com.sandclan.moviesinthesky.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.entity.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;
    @BindView(R.id.imageview_detail_poster) ImageView poster;
    @BindView(R.id.textview_summary) TextView summary;
    @BindView(R.id.textview_original_title) TextView originalTitle;
    @BindView(R.id.textview_detail_rate_value) TextView rateValue;
    @BindView(R.id.textview_release_date) TextView release_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("Movie")) {
            mMovie = (Movie) intent.getSerializableExtra("Movie");
            setTitle(mMovie.getTitle());
            Picasso.with(this).load(mMovie.getImageUrl()).into(poster);
            summary.setText(mMovie.getSynopsis());
            originalTitle.setText(mMovie.getOriginal_title());
            rateValue.setText(mMovie.getVoteAverage().toString());
            release_date.setText(mMovie.getReleaseDate());
        }

    }

}
