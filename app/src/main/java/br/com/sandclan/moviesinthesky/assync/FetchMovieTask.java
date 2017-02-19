package br.com.sandclan.moviesinthesky.assync;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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
import br.com.sandclan.moviesinthesky.entity.Movie;
import br.com.sandclan.moviesinthesky.interfaces.AssyncTaskCompletListener;

public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

    private Context mContext;
    private AssyncTaskCompletListener<List<Movie>> listener;

    public FetchMovieTask(Context ctx, AssyncTaskCompletListener<List<Movie>> list)
    {
        this.mContext = ctx;
        this.listener = list;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        if(movies != null){
            super.onPostExecute(movies);
            listener.onTaskComplete(movies);
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
                    PreferenceManager.getDefaultSharedPreferences(mContext);
            String sortOrder = sharedPrefs.getString(mContext.getString(R.string.pref_order_by_key),
                    mContext.getString(R.string.popularity_value));

            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+sortOrder+"?include_adult=false&page=1";
            final String LANGUAGE = "language";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                    .appendQueryParameter(LANGUAGE, "pt-BR")
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


