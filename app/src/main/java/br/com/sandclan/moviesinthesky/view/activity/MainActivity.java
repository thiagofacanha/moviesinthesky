package br.com.sandclan.moviesinthesky.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.Util.Constants;
import br.com.sandclan.moviesinthesky.adapter.MovieAdapter;
import br.com.sandclan.moviesinthesky.assync.FetchMovieTask;
import br.com.sandclan.moviesinthesky.entity.Movie;
import br.com.sandclan.moviesinthesky.interfaces.AssyncTaskCompletListener;

public class MainActivity extends AppCompatActivity {
    private BaseAdapter mMovieAdapter;
    private List<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMovies = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(MainActivity.this, mMovies);
        final GridView mForecastGridView = (GridView) findViewById(R.id.gridview);
        mForecastGridView.setAdapter(mMovieAdapter);
        mForecastGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra(Constants.MOVIE, movie);
                startActivity(detailIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent detailIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(detailIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        new FetchMovieTask(this, new FetchMoviesTaskCompleteListener()).execute("");

    }

    private class FetchMoviesTaskCompleteListener implements AssyncTaskCompletListener<List<Movie>>
    {

        @Override
        public void onTaskComplete(List<Movie> result) {
            mMovies.clear();
            mMovies.addAll(result);
            mMovieAdapter.notifyDataSetChanged();
        }
    }




}
