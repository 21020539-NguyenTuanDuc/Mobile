package com.example.mobile.MainActivityPackage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import android.content.Intent;


import com.example.mobile.Adapter.HistoryAdapter;
import com.example.mobile.MangaDetailPackage.MangaDetailActivity;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.Model.UserModel;
import com.example.mobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class HistoryFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);

        // Khởi tạo adapter và thiết lập LayoutManager
        adapter = new HistoryAdapter(getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            Log.d("HistoryFragment", "UserID: " + userId);
            db.collection("User").whereEqualTo("id", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                if (user != null && !user.isSignIn()) {
                                    List<String> mangaIds = (List<String>) document.get("historyList");
                                    if (mangaIds != null && !mangaIds.isEmpty()) {
                                        fetchMangaDetails(mangaIds);
                                    }
                                }
                            }
                        }
                    });
        }

        return view;
    }

    private void fetchMangaDetails(List<String> mangaIds) {
        for (String mangaId : mangaIds) {
            db.collection("Manga")
                    .document(mangaId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                MangaModel manga = document.toObject(MangaModel.class);
                                if (manga != null) {
                                    adapter.addManga(0, manga);
                                }
                            }
                        } else {
                            Log.e("fetchMangaDetails", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }
    public void onItemClick(String id, String image, String name){
        Intent i = new Intent(getContext(), MangaDetailActivity.class);
        i.putExtra("id", id);
        i.putExtra("image", image);
        i.putExtra("name", name);
        startActivity(i);
    }

}