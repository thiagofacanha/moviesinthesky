package br.com.sandclan.moviesinthesky.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.data.MovieContract;
import br.com.sandclan.moviesinthesky.entity.Movie;

public class MovieAdapter extends CursorAdapter {


    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_item_layout;


        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL))).error(R.drawable.image_not_found).placeholder(R.drawable.image_not_found).into(holder.poster);


    }

    @Override
    public Movie getItem(int position) {
        Movie movie = new Movie();
        Cursor cursor = getCursor();
        if (cursor.moveToPosition(position)) {
            movie.setIdAPI(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID_FROM_MOVIEDBAPI)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
            movie.setImageUrl(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL)));
            movie.setSynopsis(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS)));
            movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            movie.setFavourite(1 == cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVOURITE)));

        }
        return movie;
    }

    public class ViewHolder {

        private final TextView title;
        private final ImageView poster;

        public ViewHolder(View view) {

            title = (TextView) view.findViewById(R.id.textview_title);
            poster = (ImageView) view.findViewById(R.id.imageview_poster);

        }
    }
}
