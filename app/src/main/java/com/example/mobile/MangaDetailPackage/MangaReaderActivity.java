package com.example.mobile.MangaDetailPackage;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MangaReaderActivity extends AppCompatActivity {

    FirebaseStorage storage;
    private ImageButton imageButton;
    private ScrollView scrollView;
    private ImageView imageManga;
    private Button b1, b2, b3;
    MangaModel manga;
    private List<String> chapList = new ArrayList<>();
    private int currentChapterIndex = 0; // Index of the current chapter in chapList

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_reader);

        storage = FirebaseStorage.getInstance();
        manga = (MangaModel) getIntent().getSerializableExtra("manga");
        // Init views
        scrollView = findViewById(R.id.scrollView3);
        imageManga = findViewById(R.id.imageManga);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);

        // Get chapList from intent
        if (getIntent().hasExtra("chapList")) {
            chapList = (List<String>) getIntent().getSerializableExtra("chapList");
        }

        loadChapterImage(currentChapterIndex);

        b1.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                currentChapterIndex--;
                loadChapterImage(currentChapterIndex);
            }
        });

        b3.setOnClickListener(v -> {
            if (currentChapterIndex < chapList.size() - 1) {
                currentChapterIndex++;
                loadChapterImage(currentChapterIndex);
            }
        });
        b2.setOnClickListener(view -> {
            Intent intent = new Intent(MangaReaderActivity.this, ChapterActivity.class);
            intent.putExtra("manga", manga);
            startActivity(intent);
        });
    }

    private void loadChapterImage(int index) {
        if (chapList.isEmpty() || index < 0 || index >= chapList.size()) {
            return;
        }
        // Tạo đường dẫn tới ảnh trong Storage
        String imageName = chapList.get(index);
        StorageReference imageRef = storage.getReference().child("images/" + imageName);
        // Load ảnh từ Storage
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .override(Target.SIZE_ORIGINAL)
                    .into(imageManga);
        }).addOnFailureListener(exception -> {
            // Handle failure
        });
    }
}
