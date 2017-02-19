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
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.sandclan.moviesinthesky.BuildConfig;
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

        mMovieAdapter = new MovieAdapter(MainActivity.this, mMovies);
        final GridView mForecastGridView = (GridView) findViewById(R.id.gridview);
        mForecastGridView.setAdapter(mMovieAdapter);
        mForecastGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra("Movie", movie);
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
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute("");
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(movies != null){
                mMovies.clear();
                mMovies.addAll(movies);
                mMovieAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection;
            BufferedReader reader;
            List<Movie> movies = new ArrayList<Movie>();

            // Will contain the raw JSON response as a string.
            String movieJsonStr;

            try {

                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String sortOrder = sharedPrefs.getString(
                        getString(R.string.pref_order_by_key),
                        getString(R.string.popularity_value));

                final String MOVIE_BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?include_adult=false&page=1";
                final String LANGUAGE = "language";
                final String SORT = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                        .appendQueryParameter(LANGUAGE, "pt-BR")
                        .appendQueryParameter(SORT, sortOrder)
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


                movies = getMoviesDataFromJson(movieJsonStr);

            } catch (Exception e) {
                Log.d("MovieInTheSky", e.getMessage());
            }
            return movies;
        }


    }



    private List<Movie> getMoviesDataFromJson(String JsonStr)
            throws JSONException {
        List<Movie> movies = new ArrayList<Movie>();
        JSONObject resultJsonObject = new JSONObject(JsonStr);
        JSONArray moviesArray = resultJsonObject.getJSONArray("results");


        for (int i = 0; i < moviesArray.length(); i++) {
            Movie movie = new Movie();
            JSONObject movieJSonObject = moviesArray.getJSONObject(i);
            movie.setId(movieJSonObject.getInt("id"));
            movie.setTitle(movieJSonObject.getString("title"));
            movie.setOriginal_title(movieJSonObject.getString("original_title"));
            movie.setImageUrl(movieJSonObject.getString("poster_path"));
            movie.setSynopsis(movieJSonObject.getString("overview"));
            movie.setVoteAverage(movieJSonObject.getDouble("vote_average"));
            movie.setReleaseDate((movieJSonObject.getString("release_date")));
            movies.add(movie);
        }

        return movies;

    }
}
