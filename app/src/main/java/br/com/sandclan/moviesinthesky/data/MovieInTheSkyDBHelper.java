package br.com.sandclan.moviesinthesky.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.sandclan.moviesinthesky.data.MovieContract.MovieEntry;
import br.com.sandclan.moviesinthesky.data.MovieContract.TrailerEntry;
import br.com.sandclan.moviesinthesky.data.MovieContract.ReviewEntry;

public class MovieInTheSkyDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "moviesky.db";


    public MovieInTheSkyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_ID_FROM_MOVIEDBAPI + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IMAGE_URL + " TEXT, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieEntry.COLUMN_POPULARITY + " INTEGER, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER, " +
                MovieEntry.COLUMN_FAVOURITE + " INTEGER " +
                " );";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                TrailerEntry.COLUMN_ID_FROM_MOVIEDBAPI + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_NAME + " TEXT, " +
                TrailerEntry.COLUMN_SITE + " TEXT, " +
                TrailerEntry.COLUMN_SIZE + " INTEGER, " +
                TrailerEntry.COLUMN_LANGUAGE + " TEXT, " +
                TrailerEntry.COLUMN_KEY + " TEXT, " +
                TrailerEntry.COLUMN_TYPE + " TEXT " +
                " );";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                ReviewEntry.COLUMN_ID_FROM_MOVIEDBAPI + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT , " +
                ReviewEntry.COLUMN_CONTENT + " TEXT " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
