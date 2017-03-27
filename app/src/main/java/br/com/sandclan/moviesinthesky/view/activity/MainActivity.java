package br.com.sandclan.moviesinthesky.view.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.Util.Constants;
import br.com.sandclan.moviesinthesky.adapter.MovieAdapter;
import br.com.sandclan.moviesinthesky.data.MovieColumns;
import br.com.sandclan.moviesinthesky.data.MovieProvider;
import br.com.sandclan.moviesinthesky.entity.Movie;
import br.com.sandclan.moviesinthesky.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private MovieAdapter mMovieAdapter;
    private GridView mMovieGridView;

    private int mPosition = ListView.INVALID_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MovieInTheSky", "onCreate - MainActivity");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMovieAdapter = new MovieAdapter(MainActivity.this, null, 0);
        mMovieGridView = (GridView) findViewById(R.id.gridview);
        mMovieGridView.setAdapter(mMovieAdapter);
        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra(Constants.MOVIE, movie);
                startActivity(detailIntent);
            }
        });
        MovieSyncAdapter.initializeSyncAdapter(this);

        getLoaderManager().initLoader(0, null, this);
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
        MovieSyncAdapter.syncImmediately(MainActivity.this);
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieColumns.TITLE + " ASC";

        Uri allMoviesUri = MovieProvider.Movies.CONTENT_URI;

        return new CursorLoader(MainActivity.this,
                allMoviesUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mMovieGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
