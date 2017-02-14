package br.com.sandclan.moviesinthesky.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.adapter.MovieAdapter;
import br.com.sandclan.moviesinthesky.entity.Movie;

public class MainActivity extends AppCompatActivity {
    private BaseAdapter mMovieAdapter;
    private List<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMovies = new ArrayList<Movie>();
        Movie m1 = new Movie();
        m1.setTitle("oi1");
        mMovies.add(m1);
        Movie m2 = new Movie();
        m2.setTitle("oi2");
        mMovies.add(m2);
        Movie m3 = new Movie();
        m3.setTitle("oi3");
        mMovies.add(m3);
        Movie m4 = new Movie();
        m4.setTitle("oi4");
        mMovies.add(m4);

        mMovieAdapter = new MovieAdapter(MainActivity.this,mMovies);
        final ListView mForecastListView = (ListView) findViewById(R.id.listview_movies);
        mForecastListView.setAdapter(mMovieAdapter);
        mForecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie)mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, movie.getId());
                startActivity(detailIntent);
            }
        });
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
        FetchMovieTask weatherTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        weatherTask.execute("");
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            String order = "rate";

            try {

                final String MOVIE_BASE_URL =
                        "https://api.themoviedb.org/3/movie/550?api_key=THISISNOTMYKEY=)";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.d("MovieInTheSky", movieJsonStr);



                return new String[0];

            }catch(Exception e)
            {
                Log.d("MovieInTheSky",e.getMessage());
                return null;
            }
        }
}}
