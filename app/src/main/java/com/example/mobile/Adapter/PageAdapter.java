package com.example.mobile.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobile.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    private FirebaseStorage storage;
    private List<String> list;

    public PageAdapter(List<String> list) {
        this.list = list;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_items, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bindImage(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageManga;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageManga = itemView.findViewById(R.id.imageManga);
        }

        public void bindImage(String imageName) {
            // Tạo đường dẫn tới ảnh trong Storage
            StorageReference imageRef = storage.getReference().child("images/" + imageName);
            // Load ảnh từ Storage
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(itemView.getContext())
                        .load(uri)
                        .override(Target.SIZE_ORIGINAL)
                        .into(imageManga);
            }).addOnFailureListener(exception -> {
                // Handle failure
            });
        }
    }
}
