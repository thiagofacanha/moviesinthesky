package br.com.sandclan.moviesinthesky.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import br.com.sandclan.moviesinthesky.BuildConfig;
import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.Util.Constants;
import br.com.sandclan.moviesinthesky.data.MovieColumns;
import br.com.sandclan.moviesinthesky.data.MovieProvider;
import br.com.sandclan.moviesinthesky.data.TrailersColumns;

import static br.com.sandclan.moviesinthesky.data.MovieColumns.FAVOURITE;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {


    private static final int SYNC_INTERVAL = 60 * 60 * 24;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {

        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d("MovieInTheSky", "onPerformSync - MovieSyncAdapter");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr;
        try {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            String sortOrder = sharedPrefs.getString(getContext().getString(R.string.pref_order_by_key),
                    getContext().getString(R.string.popularity_value));

            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + sortOrder + "?include_adult=false&page=1";
            final String LANGUAGE = "language";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                    .appendQueryParameter(LANGUAGE, "pt-BR")
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
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
                return;
            }
            movieJsonStr = buffer.toString();
            Log.d("MovieInTheSky", movieJsonStr);

            getMoviesDataFromJson(movieJsonStr);

        } catch (IOException e) {
            Log.e("MovieInTheSky", "Error ", e);
        } catch (JSONException e) {
            Log.e("MovieInTheSky", e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MovieInTheSky", "Error closing stream", e);
                }
            }
        }
    }


    private void getMoviesDataFromJson(String JsonStr)
            throws JSONException {


        try {
            JSONObject resultJsonObject = new JSONObject(JsonStr);
            JSONArray moviesArray = resultJsonObject.getJSONArray(Constants.RESULTS);

            Vector<ContentValues> cVVector = new Vector<>(moviesArray.length());
            if (moviesArray.length() > 0) {
                // deleting movies not favourites
                String whereString = MovieColumns.FAVOURITE + " = 0";
                getContext().getContentResolver().delete(MovieProvider.Movies.CONTENT_URI, whereString, null);
            }
            for (int i = 0; i < moviesArray.length(); i++) {
                // These are the values that will be collected.
                JSONObject movieJSonObject = moviesArray.getJSONObject(i);

                String whereString = MovieColumns.ID_FROM_API + " =  ? AND " + MovieColumns.FAVOURITE + " = ? ";
                String[] values = {movieJSonObject.getString(Constants.JSON_ID), "1"};


                Cursor existFavouriteItem = getContext().getContentResolver().query(MovieProvider.Movies.CONTENT_URI, null, whereString, values, null);

                if (existFavouriteItem != null && existFavouriteItem.getCount() == 0) {

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieColumns.ID_FROM_API, movieJSonObject.getString(Constants.JSON_ID));
                    movieValues.put(MovieColumns.TITLE, movieJSonObject.getString(Constants.JSON_TITLE));
                    movieValues.put(MovieColumns.ORIGINAL_TITLE, movieJSonObject.getString(Constants.JSON_ORIGINAL_TITLE));
                    movieValues.put(MovieColumns.IMAGE_URL, Constants.HTTP_IMAGE_TMDB_ORG_T_P_W500 + movieJSonObject.getString(Constants.JSON_POSTER_PATH));
                    movieValues.put(MovieColumns.SYNOPSIS, movieJSonObject.getString(Constants.JSON_OVERVIEW));
                    movieValues.put(MovieColumns.VOTE_AVERAGE, movieJSonObject.getDouble(Constants.JSON_VOTE_AVERAGE));
                    movieValues.put(MovieColumns.RELEASE_DATE, movieJSonObject.getString(Constants.JSON_RELEASE_DATE));
                    movieValues.put(FAVOURITE, 0);

                    cVVector.add(movieValues);
                    requestReviewFromMovie(movieJSonObject.getInt(Constants.JSON_ID));
                    requestTraillersFromMovie(movieJSonObject.getInt(Constants.JSON_ID));
                }
                existFavouriteItem.close();
            }

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                //Inserting new movies
                getContext().getContentResolver().bulkInsert(MovieProvider.Movies.CONTENT_URI, cvArray);
            }
            Log.d("MovieInTheSky", "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e("MovieInTheSky", e.getMessage(), e);
        }
    }

    private void requestReviewFromMovie(int movieId) {
        if (movieId == 0) {
            return;
        }
        HttpURLConnection urlConnection;
        BufferedReader reader;
        // Will contain the raw JSON response as a string.
        String movieJsonStr;

        try {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getContext());


            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + movieId + "/reviews";
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
                return;
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
                return;
            }
            movieJsonStr = buffer.toString();
            Log.d("MovieInTheSky", movieJsonStr);

            saveMovieReviewFromJsonInDB(movieJsonStr);

        } catch (Exception e) {
            Log.d("MovieInTheSky", e.getMessage());
        }
    }

    private static void saveMovieReviewFromJsonInDB(String movieJsonStr) {
//        List<String> reviews = new ArrayList<>();
//        JSONObject resultJsonObject = new JSONObject(movieJsonStr);
//        JSONArray videoObjects = resultJsonObject.getJSONArray(Constants.RESULTS);
//
//
//        for (int i = 0; i < videoObjects.length(); i++) {
//            JSONObject movieJSonObject = videoObjects.getJSONObject(i);
//            reviews.add(movieJSonObject.getString(Constants.JSON_REVIEW_AUTHOR) + " : " + movieJSonObject.getString(Constants.JSON_REVIEW_CONTENT));
//        }

    }


    private void requestTraillersFromMovie(int movieId) {
        if (movieId == 0) {
            return;
        }
        HttpURLConnection urlConnection;
        BufferedReader reader;
        List<String> trailers = new ArrayList<>();

        // Will contain the raw JSON response as a string.
        String movieJsonStr;

        try {

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            String sortOrder = sharedPrefs.getString(getContext().getString(R.string.pref_order_by_key),
                    getContext().getString(R.string.popularity_value));

            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/movie/" + movieId + "/videos";
            final String LANGUAGE = "language";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                    //    .appendQueryParameter(LANGUAGE, "pt-BR")
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
                return;
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
                return;
            }
            movieJsonStr = buffer.toString();
            Log.d("MovieInTheSky", movieJsonStr);


            saveMovieTraillersFromJsonInDB(movieId, movieJsonStr);

        } catch (Exception e) {
            Log.e("MovieInTheSky", e.getMessage());
        }
    }

    private void saveMovieTraillersFromJsonInDB(int movieId, String JsonStr) {
        JSONObject resultJsonObject;
        JSONArray videoObjects;
        try {
            resultJsonObject = new JSONObject(JsonStr);
            videoObjects = resultJsonObject.getJSONArray(Constants.RESULTS);
            Vector<ContentValues> cVVector = new Vector<>(videoObjects.length());
            for (int i = 0; i < videoObjects.length(); i++) {
                JSONObject movieJSonObject = videoObjects.getJSONObject(i);
                ContentValues trailersValues = new ContentValues();

                trailersValues.put(TrailersColumns.MOVIE_ID, movieId);
                trailersValues.put(TrailersColumns.ID_FROM_API, movieJSonObject.getString(Constants.JSON_TRAILER_ID));
                trailersValues.put(TrailersColumns.KEY, movieJSonObject.getString(Constants.KEY));
                trailersValues.put(TrailersColumns.NAME, movieJSonObject.getString(Constants.JSON_TRAILER_NAME));
                trailersValues.put(TrailersColumns.LANGUAGE, movieJSonObject.getString(Constants.JSON_TRAILER_LANGUAGE));
                trailersValues.put(TrailersColumns.SITE, movieJSonObject.getString(Constants.JSON_TRAILER_SITE));
                trailersValues.put(TrailersColumns.SIZE, movieJSonObject.getString(Constants.JSON_TRAILER_SIZE));
                trailersValues.put(TrailersColumns.TYPE, movieJSonObject.getString(Constants.JSON_TRAILER_TYPE));


                String whereString = TrailersColumns.ID_FROM_API + " =  ? ";
                String[] values = {movieJSonObject.getString(Constants.JSON_TRAILER_ID), "1"};


                Cursor existTrailers = getContext().getContentResolver().query(MovieProvider.Trailers.CONTENT_URI, null, whereString, values, null);

                if (existTrailers != null && existTrailers.getCount() == 0) {
                    cVVector.add(trailersValues);
                    existTrailers.close();
                }
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    //Inserting new trailers
                    getContext().getContentResolver().bulkInsert(MovieProvider.Trailers.CONTENT_URI, cvArray);
                }

            }

        } catch (JSONException e) {
            Log.e("MovieInTheSky", e.getMessage());
        }

    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                MovieProvider.AUTHORITY, bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}


