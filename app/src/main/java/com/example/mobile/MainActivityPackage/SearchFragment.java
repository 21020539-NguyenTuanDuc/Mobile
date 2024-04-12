package com.example.mobile.MainActivityPackage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mobile.MainActivityPackage.SearchActivity.Comic;
import com.example.mobile.MainActivityPackage.interfaces.GetComic;
import com.example.mobile.MainActivityPackage.adapter.ComicAdapter;
import com.example.mobile.R;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFragment extends Fragment implements GetComic {

    GridView gdvComicList;

    ComicAdapter adapter;

    ArrayList<Comic> comicArrayList;

    EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        mapping(view);
        setUp();
        setClick();
//        new ApiGetComic(this).execute();
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
        searchEditText = view.findViewById(R.id.searchEditText);
    }
    private void setUp() {
        gdvComicList.setAdapter(adapter);
    }
    private void setClick() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = searchEditText.getText().toString();
                adapter.searchComicByName(str);
            }
        });
    }

    @Override
    public void start() {
        Toast.makeText(this.getContext(), "Getting data ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void end(String data) {
        System.out.print("hello");
        try {
            JSONArray arr = new JSONArray(data);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                comicArrayList.add(new Comic(o));
            }
            adapter = new ComicAdapter(this.getContext(), 0, comicArrayList);
            gdvComicList.setAdapter(adapter);
        } catch (JSONException e){

        }
    }

    @Override
    public void isError() {
        Toast.makeText(this.getContext(), "Error", Toast.LENGTH_SHORT).show();
    }
}