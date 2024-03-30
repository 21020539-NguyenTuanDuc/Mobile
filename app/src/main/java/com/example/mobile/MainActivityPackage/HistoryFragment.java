package com.example.mobile.MainActivityPackage;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import androidx.fragment.app.Fragment;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.Model.UserModel;
import com.example.mobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HistoryFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Log.d("HistoryFragment", "UserID: " + userId);
            db.collection("User").whereEqualTo("id", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                if (user != null) {
                                    if (!user.isSignIn()) {
                                        List<String> mangaIds = (List<String>) document.get("historyList");
                                        if (mangaIds != null && !mangaIds.isEmpty()) {
                                            fetchMangaDetails(mangaIds, linearLayout);
//                                            TextView tvName = view.findViewById(R.id.tvName);
//                                            tvName.setText(userId);
                                        } else {
                                            TextView tvName = view.findViewById(R.id.tvName);
                                            tvName.setText("No manga found");
                                        }
                                    } else {
                                        TextView tvName = view.findViewById(R.id.tvName);
                                        tvName.setText("No user found");
                                    }
                                } else {
                                    Log.e("HistoryFragment", "User is null");
                                }
                            }
                        } else {
                            TextView tvName = view.findViewById(R.id.tvName);
                            tvName.setText("Error fetching history");
                            Log.e("HistoryFragment", "Error fetching history: " + task.getException().getMessage());
                        }
                    });
        } else {
            TextView tvName = view.findViewById(R.id.tvName);
            tvName.setText("Error log in");
            Log.e("HistoryFragment", "User is not logged in");
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
                                    Button btnDelete = mangaView.findViewById(R.id.btnDelete);

                                    ivManga.setImageResource(manga.getImageResourceId(getContext()));
                                    tvName.setText(manga.getName());

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

