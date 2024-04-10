package com.example.mobile.MangaDetailPackage;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobile.Adapter.ChapterAdapter;
import com.example.mobile.Model.ChapterModel;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChapterAdapter adapter;
    private List<String> chapterIdList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Nhận danh sách ID chương từ intent
        MangaModel manga = (MangaModel) getIntent().getSerializableExtra("manga");
        chapterIdList = manga.getChapList();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách đầy đủ các đối tượng ChapterModel từ Firestore
        fetchChapterList();

//        // Khởi tạo và thiết lập listener cho adapter
//        List<ChapterModel> chapters = new ArrayList<>();
//        adapter = new ChapterAdapter(chapters);
//        recyclerView.setAdapter(adapter);
//        adapter.setOnChapterClickListener(new ChapterAdapter.OnChapterClickListener() {
//            @Override
//            public void onChapterClick(int position) {
//                ChapterModel clickedChapter = chapters.get(position);
//                // Chuyển đến MangaReaderActivity và truyền dữ liệu cần thiết
//                Intent intent = new Intent(ChapterActivity.this, MangaReaderActivity.class);
//                intent.putExtra("chapter", clickedChapter);
//                intent.putExtra("currentChap", position + 1);
//                intent.putExtra("manga", manga);
//                startActivity(intent);
//            }
//        });
    }

    private void fetchChapterList() {
        // Danh sách lưu trữ các đối tượng ChapterModel
        List<ChapterModel> chapters = new ArrayList<>();

        // Lặp qua danh sách ID chương và truy vấn Firestore để lấy dữ liệu cho từng chương
        for (String chapterId : chapterIdList) {
            db.collection("Chapter").document(chapterId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Chuyển đổi dữ liệu từ Firestore thành đối tượng ChapterModel và thêm vào danh sách
                                    ChapterModel chapter = document.toObject(ChapterModel.class);
                                    chapters.add(chapter);

                                    // Kiểm tra nếu đã lấy đủ dữ liệu cho tất cả các chương
                                    if (chapters.size() == chapterIdList.size()) {
                                        // Sau khi lấy đủ dữ liệu, cập nhật RecyclerView
                                        updateRecyclerView(chapters);
                                    }
                                }
                            } else {
                                // Xử lý trường hợp lỗi khi truy vấn Firestore
                            }
                        }
                    });
        }
    }

    private void updateRecyclerView(List<ChapterModel> chapters) {
        // Khởi tạo adapter và thiết lập RecyclerView
        adapter = new ChapterAdapter(chapters);
        recyclerView.setAdapter(adapter);

        MangaModel manga = (MangaModel) getIntent().getSerializableExtra("manga");
        adapter.setOnChapterClickListener(new ChapterAdapter.OnChapterClickListener() {
            @Override
            public void onChapterClick(int position) {
                ChapterModel clickedChapter = chapters.get(position);
                // Truy vấn chapList từ Firestore
                db.collection("Manga").document(manga.getId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Kiểm tra kiểu dữ liệu của chapList
                                Object chapListObject = documentSnapshot.get("chapList");
                                if (chapListObject instanceof List<?>) {
                                    // Ép kiểu chapListObject thành List<String>
                                    List<String> chapList = (List<String>) chapListObject;

                                    // Cập nhật dữ liệu trên Firestore và chuyển đến ChapterReaderActivity
                                    updateCurrentChapterAndStartActivity(position, clickedChapter, chapList);
                                } else {
                                    // Xử lý trường hợp dữ liệu không hợp lệ
                                    Toast.makeText(ChapterActivity.this, "Dữ liệu chapList không hợp lệ", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ChapterActivity.this, "Không tìm thấy manga", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            private void updateCurrentChapterAndStartActivity(int position, ChapterModel clickedChapter, List<String> chapList) {
                db.collection("Manga").document(manga.getId())
                        .update("currentChap", position + 1)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(ChapterActivity.this, ChapterReaderActivity.class);
                            intent.putExtra("chapter", clickedChapter);
                            intent.putExtra("currentChap", position + 1);
                            intent.putExtra("manga", manga);
                            intent.putStringArrayListExtra("chapList", new ArrayList<>(chapList));
                            intent.putStringArrayListExtra("imageList", (ArrayList<String>) clickedChapter.getList());
                            intent.putExtra("currentChapterIndex", position);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý trường hợp lỗi khi cập nhật trên Firestore
                            Log.e(TAG, "Error updating current chapter", e);
                            // Hiển thị thông báo lỗi nếu cần
                            Toast.makeText(ChapterActivity.this, "Failed to update current chapter", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
