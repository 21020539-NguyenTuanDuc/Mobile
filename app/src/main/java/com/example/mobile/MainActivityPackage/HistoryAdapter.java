package com.example.mobile.MainActivityPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<MangaModel> mangaList;
    private Context context;

    public HistoryAdapter(Context context) {
        this.context = context;
        this.mangaList = new ArrayList<>();
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MangaModel manga = mangaList.get(position);
        holder.bind(manga);
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
        }

        public void bind(MangaModel manga) {
            ivManga.setImageResource(manga.getImageResourceId(context));
            tvName.setText(manga.getName());
        }
    }
}
