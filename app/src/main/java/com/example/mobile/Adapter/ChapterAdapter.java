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
import com.example.mobile.Model.ChapterModel;
import com.example.mobile.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private FirebaseStorage storage;
    private List<ChapterModel> chapterList;

    public ChapterAdapter(List<ChapterModel> chapterList) {
        this.chapterList = chapterList;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_items, parent, false);
        return new ChapterViewHolder(view);
    }

    public interface OnChapterClickListener {
        void onChapterClick(int position);
    }
    private OnChapterClickListener onChapterClickListener;
    public void setOnChapterClickListener(OnChapterClickListener listener) {
        this.onChapterClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        ChapterModel chapter = chapterList.get(position);
        holder.bind(chapter);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition(); // Lấy position của item được nhấn
                if (onChapterClickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    onChapterClickListener.onChapterClick(clickedPosition);
                }
            }
        });
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

        public void bind(ChapterModel chapter) {
            // Set text for tvChapter
            tvChapter.setText("Chapter " + (getAdapterPosition() + 1));

            // Load first image from Firebase Storage as chapter thumbnail
            if (chapter.getList() != null && chapter.getList().size() > 0) {
                String firstImageName = chapter.getList().get(0);
                StorageReference imageRef = storage.getReference().child("images/" + firstImageName);
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
}
