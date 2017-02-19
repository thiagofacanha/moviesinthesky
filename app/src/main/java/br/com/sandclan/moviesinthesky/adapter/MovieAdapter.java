package br.com.sandclan.moviesinthesky.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.sandclan.moviesinthesky.R;
import br.com.sandclan.moviesinthesky.entity.Movie;

public class MovieAdapter extends BaseAdapter {

    private Context context;
    private List<Movie> movies;


    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.movie_item_layout, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }


        Movie movie = getItem(position);
        holder.title.setText(movie.getTitle());
        Picasso.with(context).load( movie.getImageUrl()).into(holder.poster);


        return view;
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
