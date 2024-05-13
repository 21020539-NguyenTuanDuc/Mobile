package com.example.mobile.MangaDetailPackage;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobile.Adapter.PageAdapter;
import com.example.mobile.MainActivity;
import com.example.mobile.MainActivityPackage.HomeFragment;
import com.example.mobile.Model.ChapterModel;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChapterReaderActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private ImageButton imageButton;
    private ScrollView scrollView;
    private ImageView imageManga;
    private Button b1, b2, prev, next;
    MangaModel manga;
    ChapterModel chapter;
    private List<String> chapList = new ArrayList<>();
    private int currentChapterIndex;
    private RecyclerView recyclerView;
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_reader);

        storage = FirebaseStorage.getInstance();
        manga = (MangaModel) getIntent().getSerializableExtra("manga");
        chapter = (ChapterModel) getIntent().getSerializableExtra("chapter");

        // Init views
        scrollView = findViewById(R.id.scrollView3);
        recyclerView = findViewById(R.id.recyclerView);
//        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Get chapList from intent
        if (getIntent().hasExtra("chapList")) {
            chapList = (List<String>) getIntent().getSerializableExtra("chapList");
        }

        List<String> imageList = getIntent().getStringArrayListExtra("imageList");
        updateRecyclerView(imageList);

        int currentChap = getIntent().getIntExtra("currentChap", 0) - 1;

        currentChapterIndex = currentChap;
        loadChapterImage(currentChapterIndex);

        prev.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                db.collection("Manga").document(manga.getId())
                        .update("currentChap", currentChapterIndex + 1)
                        .addOnSuccessListener(aVoid -> {
                            // Cập nhật thành công, tiến hành load chap mới và cập nhật giao diện
                            loadChapterImage(currentChapterIndex);
                            b2.setText("Chap " + (currentChapterIndex + 1));
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý khi có lỗi xảy ra trong quá trình cập nhật dữ liệu
                        });
            }
        });

        next.setOnClickListener(v -> {
            if (currentChapterIndex < chapList.size() - 1) {
                currentChapterIndex++;
                db.collection("Manga").document(manga.getId())
                        .update("currentChap", currentChapterIndex + 1)
                        .addOnSuccessListener(aVoid -> {
                            // Cập nhật thành công, tiến hành load chap mới và cập nhật giao diện
                            loadChapterImage(currentChapterIndex);
                            b2.setText("Chap " + (currentChapterIndex + 1));
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý khi có lỗi xảy ra trong quá trình cập nhật dữ liệu
                        });
            }
        });

//        b1.setOnClickListener(v -> {
//            Intent intent = new Intent(ChapterReaderActivity.this, MainActivity.class);
//            startActivity(intent);
//        });

        b2.setOnClickListener(view -> {
            Intent intent = new Intent(ChapterReaderActivity.this, ChapterActivity.class);
            intent.putExtra("manga", manga);
            startActivity(intent);
        });

        b2.setText("Chap " + (currentChapterIndex + 1));
    }

    private void loadChapterImage(int index) {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        if (index < 0 || index >= chapList.size()) {
            return;
        }

        String chapId = chapList.get(index);
        if (chapId == null) {
            Log.e(TAG, "ChapId is null");
            return;
        }

        db.collection("Chapter").document(chapId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ChapterModel chapter = document.toObject(ChapterModel.class);
                            if (chapter != null && chapter.getPrice() > 0) {
                                // Nếu chapter có giá tiền
                                List<String> users = chapter.getUsers();
                                if (users != null && users.contains(userId)) {
                                    // Nếu danh sách users chứa id của currentUser
                                    List<String> imageList = chapter.getList();
                                    updateRecyclerView(imageList);
                                } else {
                                    // Người dùng không có quyền đọc chap này
                                    // Hiển thị thông báo hoặc thực hiện hành động phù hợp
                                    Toast.makeText(ChapterReaderActivity.this, "Bạn không có quyền đọc chap này, vui lòng chuyển sang giao diện Chapter để mua chap!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ChapterReaderActivity.this, ChapterActivity.class);
                                    intent.putExtra("manga", manga);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                // Nếu chapter không có giá tiền hoặc có giá là 0
                                if (chapter != null && chapter.getList() != null) {
                                    List<String> imageList = chapter.getList();
                                    updateRecyclerView(imageList);
                                } else {
                                    Log.e(TAG, "Chapter or image list is null");
                                }
                            }
                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                });
    }


    private void updateRecyclerView(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            Log.e(TAG, "Image list is null or empty");
            return;
        }

        // Khởi tạo adapter và thiết lập RecyclerView
        pageAdapter = new PageAdapter(imageList);
        recyclerView.setAdapter(pageAdapter);
    }
}
