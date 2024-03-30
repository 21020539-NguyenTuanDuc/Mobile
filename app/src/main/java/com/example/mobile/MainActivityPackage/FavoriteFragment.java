package com.example.mobile.MainActivityPackage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobile.Model.MangaModel;
import com.example.mobile.Model.UserModel;
import com.example.mobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Log.d("FavoriteFragment", "UserID: " + userId);
            db.collection("User").whereEqualTo("id", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                if (user != null) {
                                    if (!user.isSignIn()) {
                                        List<String> mangaIds = (List<String>) document.get("favoriteList");
                                        if (mangaIds != null && !mangaIds.isEmpty()) {
                                            fetchMangaDetails(mangaIds, linearLayout);
//                                            TextView tvName = view.findViewById(R.id.tvName);
//                                            tvName.setText(userId);
                                        } else {
//                                            TextView tvName = view.findViewById(R.id.tvName);
//                                            tvName.setText("No manga found");
                                        }
                                    } else {
//                                        TextView tvName = view.findViewById(R.id.tvName);
//                                        tvName.setText("No user found");
                                    }
                                } else {
                                    Log.e("FavoriteFragment", "User is null");
                                }
                            }
                        } else {
//                            TextView tvName = view.findViewById(R.id.tvName);
//                            tvName.setText("Error fetching favorite");
//                            Log.e("FavoriteFragment", "Error fetching favorite: " + task.getException().getMessage());
                        }
                    });
        } else {
//            TextView tvName = view.findViewById(R.id.tvName);
//            tvName.setText("Error log in");
//            Log.e("FavoriteFragment", "User is not logged in");
        }

        return view;
    }

    private void fetchMangaDetails(List<String> mangaIds, LinearLayout linearLayout) {
        for (String mangaId : mangaIds) {
            db.collection("Manga")
                    .whereEqualTo("id", mangaId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MangaModel manga = document.toObject(MangaModel.class);
                                if (manga != null) {
                                    Log.d("MangaName", "Tên manga: " + manga.getName());

                                    View mangaView = LayoutInflater.from(getContext()).inflate(R.layout.manga_items, null);
                                    ImageView ivManga = mangaView.findViewById(R.id.ivManga);
                                    TextView tvName = mangaView.findViewById(R.id.tvName);

                                    ivManga.setImageResource(manga.getImageResourceId(getContext()));
                                    tvName.setText(manga.getName());

                                    // Thêm view vào LinearLayout từ manga_items.xml
                                    linearLayout.addView(mangaView);
                                } else {
                                    Log.e("fetchMangaDetails", "Manga is null");
                                }
                            }
                        } else {
                            Log.e("fetchMangaDetails", "Error getting documents: " + task.getException().getMessage());
                        }
                    });
        }
    }

}

