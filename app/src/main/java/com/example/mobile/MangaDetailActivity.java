package com.example.mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.Model.MangaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MangaDetailActivity extends AppCompatActivity {
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

        setContentView(R.layout.activity_manga_detail);

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
            Intent intent = new Intent(MangaDetailActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnFav.setOnClickListener(view -> {
            if (imgFav.getVisibility() == View.VISIBLE)
                imgFav.setVisibility(View.INVISIBLE);
            else
                imgFav.setVisibility(View.VISIBLE);
        });

//        btnRead.setOnClickListener(view -> {
//            Intent i = new Intent(MangaDetailActivity.this, MangaReader.class);
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
