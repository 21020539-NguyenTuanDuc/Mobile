package com.example.mobile;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.Model.MangaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDetailActivity extends AppCompatActivity {
    public int MAX_HISTORY_SIZE = 10;
    ImageView imgView;
    ImageView imgFav;
    TextView txtDes;
    TextView txtAuth;
    TextView txtProgress;
    TextView txtChap;
    TextView txtLike;
    TextView txtTitle;
    Button btnPrev;
    Button btnFav;
    Button btnRead;
    FirebaseFirestore db;
    MangaModel manga;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_favorite_detail);

        db = FirebaseFirestore.getInstance();

        txtDes = findViewById(R.id.txtDes);
        txtAuth = findViewById(R.id.txtAuth);
        txtProgress = findViewById(R.id.txtProgress);
        txtLike = findViewById(R.id.txtLike);
        txtTitle = findViewById(R.id.txtTitle);
        imgView = findViewById(R.id.imgView);
        imgFav = findViewById(R.id.imageFav);
        btnFav = findViewById(R.id.btnFav);
        btnRead = findViewById(R.id.btnRead);
        imgFav.bringToFront();

        String id = getIntent().getStringExtra("id");
        imgFav.setVisibility(View.INVISIBLE);

        db.collection("Manga").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                manga = document.toObject(MangaModel.class);
                                updateUI();
                            }
                        }
                    }
                });

        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(view -> {
            Intent intent = new Intent(FavoriteDetailActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnFav.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                db.collection("User").document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> favoriteList = (List<String>) document.get("favoriteList");
                                    if (favoriteList == null) {
                                        favoriteList = new ArrayList<>();
                                    }

                                    if (favoriteList.contains(manga.getId())) {
                                        // Xóa truyện khỏi danh sách yêu thích
                                        favoriteList.remove(manga.getId());

                                        // Cập nhật danh sách yêu thích mới vào Firestore
                                        db.collection("User").document(userId)
                                                .update("favoriteList", favoriteList)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(FavoriteDetailActivity.this, "Đã xóa khỏi danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                                                    // Giảm 1 giá trị likes của truyện
                                                    db.collection("Manga").document(manga.getId())
                                                            .update("likes", manga.getLikes() - 1)
                                                            .addOnSuccessListener(aVoid1 -> {
                                                                Toast.makeText(FavoriteDetailActivity.this, "Đã giảm đi 1 lượt thích cho truyện!", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(FavoriteDetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(FavoriteDetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(FavoriteDetailActivity.this, "Bạn đã xóa truyện khỏi danh sách yêu thích trước đó rồi!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        });
            } else {
                Toast.makeText(FavoriteDetailActivity.this, "Bạn cần đăng nhập để thực hiện thao tác này!", Toast.LENGTH_SHORT).show();
            }
        });



        btnRead.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                db.collection("User").document(userId).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> historyList = (List<String>) document.get("historyList");
                                    if (historyList == null) {
                                        historyList = new ArrayList<>();
                                    }

                                    if (historyList.contains(manga.getId())) {
                                        historyList.remove(manga.getId());
                                    }

                                    historyList.add(manga.getId());

                                    if (historyList.size() > MAX_HISTORY_SIZE) {
                                        historyList.remove(0);
                                    }

                                    db.collection("User").document(userId)
                                            .update("historyList", historyList)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(FavoriteDetailActivity.this, "Đã thêm vào lịch sử đọc!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(FavoriteDetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        });
            } else {
                Toast.makeText(FavoriteDetailActivity.this, "Bạn cần đăng nhập để thực hiện thao tác này!", Toast.LENGTH_SHORT).show();
            }
        });

//        btnRead.setOnClickListener(view -> {
//            Intent i = new Intent(FavoriteDetailActivity.this, MangaReader.class);
//            i.putExtra("img", manga.getImageResourceId());
//            startActivity(i);
//        });
    }

    private void updateUI() {
        if (manga != null) {
            txtAuth.setText(manga.getAuthor());
            txtLike.setText(String.valueOf(manga.getLikes()));
//            txtProgress.setText(manga.getProgress());
            txtTitle.setText(manga.getName());
//            imgView.setImageResource(manga.getImageResourceId());
//            txtChap.setText(manga.getGenres().size());
            txtDes.setText(manga.getDescription());
            imgView.setImageResource(manga.getImageResourceId(this));
        }
    }
}

