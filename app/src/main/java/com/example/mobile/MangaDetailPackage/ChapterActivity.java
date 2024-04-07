package com.example.mobile.MangaDetailPackage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile.Adapter.ChapterAdapter;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChapterAdapter adapter;
    private List<String> chapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);


        // Nhận danh sách chương từ intent
        MangaModel manga = (MangaModel) getIntent().getSerializableExtra("manga");
        chapterList = manga.getChapList();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChapterAdapter(chapterList);
        recyclerView.setAdapter(adapter);
    }
}


