package com.example.moodify.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodify.R;
import com.example.moodify.response.GenreItem;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<GenreItem> genreList;
    private Context context;

    public interface OnGenreClickListener {
        void onGenreClick(GenreItem item);
    }

    private OnGenreClickListener listener;

    public GenreAdapter(List<GenreItem> genreList, Context context, OnGenreClickListener listener) {
        this.genreList = genreList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        GenreItem item = genreList.get(position);
        holder.genreName.setText(item.getName());
        holder.genreImage.setImageResource(item.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onGenreClick(item));
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreName;
        ImageView genreImage;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.tv_genre_name);
            genreImage = itemView.findViewById(R.id.img_genre);
        }
    }
}
