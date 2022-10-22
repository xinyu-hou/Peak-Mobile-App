package edu.northeastern.numad22fa_team15;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.numad22fa_team15.model.MovieTv;
import edu.northeastern.numad22fa_team15.model.Result;

public class MovieTvAdapter extends RecyclerView.Adapter<MovieTvViewHolder> {

    private List<MovieTv> results;
    private final Context context;

    public MovieTvAdapter(List<MovieTv> results, Context context) {
        if (results == null || context == null) {
            throw new IllegalArgumentException("Results and context cannot be null.");
        }
        this.results = results;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieTvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_tv, parent, false);
        return new MovieTvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTvViewHolder holder, int position) {
        holder.itemTitle.setText(results.get(position).getMovieTvTitle());
        holder.itemYear.setText(results.get(position).getMovieTvYear());
        holder.itemID.setText(results.get(position).getMovieTvImdbID());
        holder.itemType.setText(results.get(position).getType());

        String imageURL = results.get(position).getPosterURL();
        Picasso.get().load(imageURL).resize(400,600).centerCrop().into(holder.itemPoster);
        holder.itemPoster.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullImageActivity.class);
            Bundle imageBundle = new Bundle();
            imageBundle.putString("Image URL", imageURL);
            intent.putExtras(imageBundle);
            context.startActivity(intent);
        });

//        // Set an OnClick event on the itemview
//        holder.itemView.setOnClickListener(view -> {
//            // Try to launch link in a web browser
//            try {
//                Uri linkUri = convertStringToUri(links.get(position).getActualLink());
//                Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, linkUri);
//                context.startActivity(openLinkIntent);
//            } catch (ActivityNotFoundException e) {
//                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show();
//            }
//        });
    }

    /**
     * Return the number of lists in the link list
     * @return the number of lists
     */
    @Override
    public int getItemCount() {
        return results.size();
    }
}