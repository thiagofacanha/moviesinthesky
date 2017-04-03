package br.com.sandclan.moviesinthesky.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "br.com.sandclan.moviesinthesky";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ID_FROM_MOVIEDBAPI = "idapi";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_FAVOURITE = "favourite";


        public static Uri buildMovieByID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static String getIdFromMovie(Uri uri) {
            return uri.getPathSegments().get(0);
        }


    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_ID_FROM_MOVIEDBAPI = "idapi";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_TYPE = "type";


        public static Uri buildMovieByID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static String getIdFromMovie(Uri uri) {
            return uri.getPathSegments().get(0);
        }
    }


    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_ID_FROM_MOVIEDBAPI = "idapi";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";


        public static Uri buildMovieByID(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static String getIdFromMovie(Uri uri) {
            return uri.getPathSegments().get(0);
        }
    }

}
