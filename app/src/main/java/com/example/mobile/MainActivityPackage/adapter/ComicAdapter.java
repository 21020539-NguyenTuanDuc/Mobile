package com.example.mobile.MainActivityPackage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mobile.MainActivityPackage.SearchActivity.Comic;
import com.example.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class ComicAdapter extends ArrayAdapter<Comic> {
    private Context ct;
    private ArrayList<Comic> arr;
    public ComicAdapter(Context context, int resource, List<Comic> objects) {
        super(context, resource, objects);
        this.ct = context;
        this.arr = new ArrayList<>(objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_search_comic_view, null);
        }

        if (arr.size() > 0) {
            Comic comic = this.arr.get(position);
            TextView name_comic = convertView.findViewById(R.id.name_comic);
            TextView chapter_name = convertView.findViewById(R.id.chapter_name);
            ImageView image_comic = convertView.findViewById(R.id.img_comic);

            name_comic.setText(comic.getName());
            chapter_name.setText(comic.getChapter());
            Glide.with(this.ct).load(comic.getImageURL()).into(image_comic);
        }

        return convertView;
    }
}
