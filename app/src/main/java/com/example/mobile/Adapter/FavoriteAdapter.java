package com.example.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.MainActivityPackage.FavoriteFragment;
import com.example.mobile.MangaDetailPackage.FavoriteDetailActivity;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<MangaModel> mangaList;
    private Context context;
    private FavoriteFragment fragment;

    public FavoriteAdapter(Context context, FavoriteFragment fragment) {
        this.context = context;
        this.mangaList = new ArrayList<>();
        this.fragment = fragment;
    }

    public void addManga(MangaModel manga) {
        mangaList.add(manga);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manga_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        MangaModel manga = mangaList.get(position);
        holder.bind(manga);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FavoriteDetailActivity.class);
                intent.putExtra("id", manga.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivManga;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivManga = itemView.findViewById(R.id.ivManga);
            tvName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MangaModel manga = mangaList.get(position);
                        fragment.onItemClick(manga.getId(), manga.getImage(), manga.getName());
                    }
                }
            });

            // Set click listener for ivManga
            ivManga.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MangaModel manga = mangaList.get(position);
                        fragment.onItemClick(manga.getId(), manga.getImage(), manga.getName());
                    }
                }
            });

            // Set click listener for tvName
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MangaModel manga = mangaList.get(position);
                        fragment.onItemClick(manga.getId(), manga.getImage(), manga.getName());
                    }
                }
            });
        }

        public void bind(MangaModel manga) {
            ivManga.setImageResource(manga.getImageResourceId(context));
            tvName.setText(manga.getName());
        }

        public void onItemClick(String id, String image, String name) {
            fragment.onItemClick(id, image, name);
        }
    }
}
