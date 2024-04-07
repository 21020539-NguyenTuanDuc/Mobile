package com.example.mobile.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobile.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private FirebaseStorage storage;
    private List<String> chapterList;

    public ChapterAdapter(List<String> chapterList) {
        this.chapterList = chapterList;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_items, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        String chapter = chapterList.get(position);
        holder.bind(chapter, position);
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tvChapter;
        private ImageView ivChapter;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapter = itemView.findViewById(R.id.tvChapter);
            ivChapter = itemView.findViewById(R.id.ivChapter);
        }

        public void bind(String chapter, int position) {
            // Set text for tvChapter
            tvChapter.setText("Chapter " + (position + 1));

            // Load image from Firebase Storage
            String imageName = chapterList.get(position);
            StorageReference imageRef = storage.getReference().child("images/" + imageName);
            // Load image from Storage
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Ensure uri is not null before loading image
                if (uri != null) {
                    Glide.with(itemView.getContext())
                            .load(uri)
                            .centerCrop()
                            .into(ivChapter);
                }
            }).addOnFailureListener(exception -> {
                // Handle failure
            });
        }
    }
}
