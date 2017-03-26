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
import br.com.sandclan.moviesinthesky.Util.Constants;
import br.com.sandclan.moviesinthesky.interfaces.AssyncTaskCompletListener;

public class FetchMovieReviewsTask extends AsyncTask<Integer, Void, List<String>> {

    private Context mContext;
    private AssyncTaskCompletListener<List<String>> listener;

    public FetchMovieReviewsTask(Context ctx, AssyncTaskCompletListener<List<String>> list)
    {
        this.mContext = ctx;
        this.listener = list;
    }

    @Override
    protected void onPostExecute(List<String> trailers) {
        if(trailers != null){
            super.onPostExecute(trailers);
            listener.onTaskComplete(trailers);
        }
    }

    @Override
    protected List<String> doInBackground(Integer... params) {

        if (params.length == 0) {
            return null;
        }
        HttpURLConnection urlConnection;
        BufferedReader reader;
        List<String> trailers = new ArrayList<>();

        // Will contain the raw JSON response as a string.
        String movieJsonStr;

        try {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(mContext);


            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+params[0]+"/reviews";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
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


            trailers = getMovieDataFromJson(movieJsonStr);

        } catch (Exception e) {
            Log.d("MovieInTheSky", e.getMessage());
        }
        return trailers;
    }


    private List<String> getMovieDataFromJson(String JsonStr)
            throws JSONException {
        List<String> reviews = new ArrayList<>();
        JSONObject resultJsonObject = new JSONObject(JsonStr);
        JSONArray videoObjects = resultJsonObject.getJSONArray(Constants.RESULTS);


        for (int i = 0; i < videoObjects.length(); i++) {
            JSONObject movieJSonObject = videoObjects.getJSONObject(i);
                reviews.add(movieJSonObject.getString(Constants.JSON_REVIEW_AUTHOR) + " : " + movieJSonObject.getString(Constants.JSON_REVIEW_CONTENT));
        }

        return reviews;

    }


}


