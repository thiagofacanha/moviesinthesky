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

public class DetailActivity extends AppCompatActivity {
    private Movie mMovie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("Movie")) {
            mMovie = (Movie) intent.getSerializableExtra("Movie");

            setTitle(mMovie.getTitle());
            ImageView poster = (ImageView) findViewById(R.id.imageview_detail_poster);
            Picasso.with(this).load(mMovie.getImageUrl()).into(poster);

            TextView summary = (TextView)findViewById(R.id.textview_summary);
            summary.setText(mMovie.getSynopsis());

            TextView originalTitle = (TextView) findViewById(R.id.textview_original_title);
            originalTitle.setText(mMovie.getOriginal_title());

            TextView rateValue = (TextView)findViewById(R.id.textview_detail_rate_value);
            rateValue.setText(mMovie.getVoteAverage().toString());

            TextView release_date = (TextView)findViewById(R.id.textview_release_date);
            release_date.setText(mMovie.getReleaseDate());

        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}
