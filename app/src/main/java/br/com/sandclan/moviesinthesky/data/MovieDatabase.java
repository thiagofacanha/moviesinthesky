package br.com.sandclan.moviesinthesky.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {

    public static final int VERSION = 1;

    @Table(MovieColumns.class)
    public static final String MOVIES = "movies";

    @Table(TrailersColumns.class)
    public static final String TRAILERS = "trailers";

    @Table(ReviewsColumns.class)
    public static final String REVIEWS = "reviews";
}
