package com.example.mobile.MainActivityPackage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.mobile.MainActivityPackage.SearchActivity.Comic;
import com.example.mobile.MainActivityPackage.adapter.ComicAdapter;
import com.example.mobile.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    GridView gdvComicList;

    ComicAdapter adapter;

    ArrayList<Comic> comicArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        mapping(view);
        setUp();
        setClick();
        return view;
    }

    private void init(View view) {
        comicArrayList = new ArrayList<>();
        comicArrayList.add(new Comic("Naruto", "Chapter 700.9", "https://www.nettruyentr.vn/images/comics/naruto.jpg"));
        comicArrayList.add(new Comic("Chuyển sinh thành Liễu đột biến", "Chapter 24", "https://www.nettruyentr.vn/images/comics/chuyen-sinh-thanh-lieu-dot-bien.jpg"));
        comicArrayList.add(new Comic("Tuyệt sắc đạo lữ đều nói Ngô Hoảng thể chất vô địch", "Chapter 210", "https://www.nettruyentr.vn/images/comics/tuyet-sac-dao-lu-deu-noi-ngo-hoang-the-chat-vo-dich.jpg"));
        comicArrayList.add(new Comic("Chưởng môn khiêm tốn chút", "Chapter 343", "https://www.nettruyentr.vn/images/comics/chuong-mon-khiem-ton-chut.jpg"));
        comicArrayList.add(new Comic("Naruto", "Chapter 700.9", "https://www.nettruyentr.vn/images/comics/naruto.jpg"));
        comicArrayList.add(new Comic("Chuyển sinh thành Liễu đột biến", "Chapter 24", "https://www.nettruyentr.vn/images/comics/chuyen-sinh-thanh-lieu-dot-bien.jpg"));
        comicArrayList.add(new Comic("Tuyệt sắc đạo lữ đều nói Ngô Hoảng thể chất vô địch", "Chapter 210", "https://www.nettruyentr.vn/images/comics/tuyet-sac-dao-lu-deu-noi-ngo-hoang-the-chat-vo-dich.jpg"));
        comicArrayList.add(new Comic("Chưởng môn khiêm tốn chút", "Chapter 343", "https://www.nettruyentr.vn/images/comics/chuong-mon-khiem-ton-chut.jpg"));
        comicArrayList.add(new Comic("Naruto", "Chapter 700.9", "https://www.nettruyentr.vn/images/comics/naruto.jpg"));
        comicArrayList.add(new Comic("Chuyển sinh thành Liễu đột biến", "Chapter 24", "https://www.nettruyentr.vn/images/comics/chuyen-sinh-thanh-lieu-dot-bien.jpg"));
        comicArrayList.add(new Comic("Tuyệt sắc đạo lữ đều nói Ngô Hoảng thể chất vô địch", "Chapter 210", "https://www.nettruyentr.vn/images/comics/tuyet-sac-dao-lu-deu-noi-ngo-hoang-the-chat-vo-dich.jpg"));
        comicArrayList.add(new Comic("Chưởng môn khiêm tốn chút", "Chapter 343", "https://www.nettruyentr.vn/images/comics/chuong-mon-khiem-ton-chut.jpg"));

        adapter = new ComicAdapter(view.getContext(), 0, comicArrayList);
    }
    private void mapping(View view) {
        gdvComicList = view.findViewById(R.id.gdvComicList);
    }
    private void setUp() {
        gdvComicList.setAdapter(adapter);
    }
    private void setClick() {}
}