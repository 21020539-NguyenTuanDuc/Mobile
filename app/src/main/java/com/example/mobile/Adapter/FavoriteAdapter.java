package com.example.mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobile.MainActivityPackage.FavoriteFragment;
import com.example.mobile.MangaDetailPackage.FavoriteDetailActivity;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    FirebaseStorage storage;
    private List<MangaModel> mangaList;
    private Context context;
    private FavoriteFragment fragment;

    public FavoriteAdapter(Context context, FavoriteFragment fragment) {
        this.context = context;
        this.mangaList = new ArrayList<>();
        this.fragment = fragment;
        storage = FirebaseStorage.getInstance();
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
        private TextView tvAuthor;
        private TextView tvGenres;
        private TextView tvChap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivManga = itemView.findViewById(R.id.ivManga);
            tvName = itemView.findViewById(R.id.tvName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvGenres = itemView.findViewById(R.id.tvGenres);
            tvChap = itemView.findViewById(R.id.tvChap);
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
            String imageName = manga.getImage();

            StorageReference imageRef = storage.getReference().child("images/" + imageName);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(ivManga.getContext())
                        .load(uri)
                        .into(ivManga);
            }).addOnFailureListener(exception -> {
                // Xử lý khi load ảnh thất bại
            });
            tvName.setText(manga.getName());
            tvAuthor.setText(manga.getAuthor());
            List<String> genresList = manga.getGenres();
            String genresText = TextUtils.join(", ", genresList);
            String genresFormat = String.format("Genres: %s", genresText);
            tvGenres.setText(genresFormat);
            int numberOfChaps = manga.getChapList().size();
            String numberOfChapsString = String.valueOf(numberOfChaps);
            String totalChaps = manga.getChapTotal();
            String displayChaps = String.format("Chapters: %s/%s", numberOfChapsString, totalChaps);
            tvChap.setText(displayChaps);
        }

        public void onItemClick(String id, String image, String name) {
            fragment.onItemClick(id, image, name);
        }
    }
}
