package com.example.mobile.MainActivityPackage.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mobile.MainActivityPackage.HomeFragment;
import com.example.mobile.MainActivityPackage.SearchFragment;
import com.example.mobile.MangaDetailPackage.MangaDetailActivity;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ComicAdapter extends BaseAdapter {

    private Context context;
    private List<MangaModel> mangaList;

    private List<MangaModel> copy_mangaList;
    private SearchFragment fragment;
    private FirebaseStorage storage;

    public ComicAdapter(Context context, SearchFragment fragment) {
        this.context = context;
        this.mangaList = new ArrayList<>();
        this.fragment = fragment;
        storage = FirebaseStorage.getInstance();
        this.copy_mangaList = new ArrayList<>();
    }

    public void addManga(int position, MangaModel manga) {
        mangaList.add(position, manga);
        copy_mangaList.add(position, manga);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mangaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mangaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_comic_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MangaModel manga = mangaList.get(position);
        holder.bind(manga);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MangaDetailActivity.class);
                intent.putExtra("id", manga.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void searchComicByName(String query) {
        query = query.trim();
        mangaList.clear();
        mangaList.addAll(copy_mangaList);
        List<MangaModel> filteredList = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            for (MangaModel manga : mangaList) {
                // Kiểm tra xem tên của manga có chứa từ khóa tìm kiếm không
                if (manga.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(manga);
                }
            }
        } else {
            filteredList.addAll(mangaList); // Nếu query rỗng, hiển thị toàn bộ danh sách manga
        }
        // Cập nhật danh sách manga hiển thị sau khi tìm kiếm
        mangaList.clear();
        mangaList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public void searchComicByGenre(List<String> selectedGenres) {
        mangaList.clear();
        mangaList.addAll(copy_mangaList);
        List<MangaModel> filteredList = new ArrayList<>();
        if (selectedGenres != null && !selectedGenres.isEmpty()) {
            for (MangaModel manga : mangaList) {
                // Kiểm tra xem manga có thuộc ít nhất một thể loại được chọn không
                boolean containsGenre = true;
                for (String genre : selectedGenres) {
                    if (!manga.getGenres().contains(genre)) {
                        containsGenre = false;
                        break;
                    }
                }
                if (containsGenre) {
                    filteredList.add(manga);
                }
            }
        } else {
            filteredList.addAll(mangaList); // Nếu không có thể loại nào được chọn, hiển thị toàn bộ danh sách manga
        }
        // Cập nhật danh sách manga hiển thị sau khi tìm kiếm theo thể loại
        mangaList.clear();
        mangaList.addAll(filteredList);
        notifyDataSetChanged();
    }


    private class ViewHolder {
        private ImageView ivManga;
        private TextView tvName;
        private TextView tvChap;

        ViewHolder(View itemView) {
            ivManga = itemView.findViewById(R.id.img_comic);
            tvName = itemView.findViewById(R.id.name_comic);
            tvChap = itemView.findViewById(R.id.chapter_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    MangaModel manga = mangaList.get(position);
                    fragment.onItemClick(manga.getId(), manga.getImage(), manga.getName());
                }
            });
        }


        void bind(MangaModel manga) {
            String imageName = manga.getPoster();
            StorageReference imageRef = storage.getReference().child("images/" + imageName);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(context)
                        .load(uri)
                        .into(ivManga);
            }).addOnFailureListener(exception -> {
                // Handle failure
            });

            tvName.setText(manga.getName());
            List<String> genresList = manga.getGenres();
            String genresText = TextUtils.join(", ", genresList);
            String genresFormat = String.format("Genres: %s", genresText);
            int numberOfChaps = manga.getChapList().size();
            String numberOfChapsString = String.valueOf(numberOfChaps);
            String totalChaps = manga.getChapTotal();
            String displayChaps = String.format("Chap: %s/%s", numberOfChapsString, totalChaps);
            tvChap.setText(displayChaps);
        }
    }
}
