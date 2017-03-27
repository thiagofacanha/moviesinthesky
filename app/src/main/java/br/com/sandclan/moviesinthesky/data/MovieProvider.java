package br.com.sandclan.moviesinthesky.data;


import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public final class MovieProvider {
    public static final String AUTHORITY = "br.com.sandclan.moviesinthesky.data.MovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    interface Path {
        String MOVIES = "movies";
        String TRAILERS = "trailers";
        String REVIEWS = "reviews";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }


    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movies {
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.TITLE + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }


    @TableEndpoint(table = MovieDatabase.TRAILERS)
    public static class Trailers {
        @ContentUri(
                path = Path.TRAILERS,
                type = "vnd.android.cursor.dir/trailer",
                defaultSort = TrailersColumns.MOVIE_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.TRAILERS);

        @InexactContentUri(
                name = "TRAILER_ID",
                path = Path.TRAILERS + "/#",
                type = "vnd.android.cursor.item/trailer",
                whereColumn = TrailersColumns._ID,
                pathSegment = 1)

        public static Uri withMovieId(long id) {
            return buildUri(Path.TRAILERS, String.valueOf(id));
        }
    }

}





