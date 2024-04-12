package com.example.mobile.MainActivityPackage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    private ArrayList<Integer> indexList;
    private String searchKeyword;

    public ComicAdapter(Context context, int resource, List<Comic> objects) {
        super(context, resource, objects);
        this.ct = context;
        this.arr = new ArrayList<>(objects);
        this.indexList = new ArrayList<>();
        fillIndexListIfEmpty();
        this.searchKeyword = "";
    }

    private void fillIndexListIfEmpty() {
        for (int i = 0; i < arr.size(); i++) {
            indexList.add(i); // Thêm index của comic vào danh sách nếu tên chứa từ khóa tìm kiếm
        }
    }

    public void searchComicByName(String query) {
        query = query.trim().toUpperCase();
        indexList.clear();
        if (!"".equals(query)) {
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getName().toUpperCase().contains(query)) {
                    indexList.add(i); // Thêm index của comic vào danh sách nếu tên chứa từ khóa tìm kiếm
                }
            }
        } else {
            fillIndexListIfEmpty();
        }
        this.searchKeyword = query;
        notifyDataSetChanged(); // Cập nhật giao diện
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_search_comic_view, null);
        }

        if (position >= 0 && position < arr.size() && indexList.contains(position)) { // Kiểm tra vị trí có hợp lệ không
            convertView.setVisibility(View.VISIBLE);
            Comic comic = arr.get(position); // Lấy ra comic tương ứng với index trong danh sách filteredIndexes

            TextView name_comic = convertView.findViewById(R.id.name_comic);
            TextView chapter_name = convertView.findViewById(R.id.chapter_name);
            ImageView image_comic = convertView.findViewById(R.id.img_comic);

            name_comic.setText(comic.getShortName());
            chapter_name.setText(comic.getChapter());
            Glide.with(this.ct).load(comic.getImageURL()).into(image_comic);
        } else {
            convertView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSearchKeyword(String keyword) {
        this.searchKeyword = keyword.trim();
    }
}
