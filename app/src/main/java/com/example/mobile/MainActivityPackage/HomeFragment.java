package com.example.mobile.MainActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobile.Adapter.MangaAdapter;
import com.example.mobile.MainActivity;
import com.example.mobile.MangaDetailPackage.MangaDetailActivity;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.Quiz.QuizActivity;
import com.example.mobile.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private GridView gridView;
    private List<MangaModel> mangaList;
    private MangaAdapter mangaAdapter;
    private FirebaseFirestore db;

    private FloatingActionButton minigameBtn;
    private AdRequest adRequest;
    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();

        gridView = view.findViewById(R.id.gdvComicList);
        mangaList = new ArrayList<>();
        mangaAdapter = new MangaAdapter(getActivity(), this); // Pass the fragment reference
        gridView.setAdapter(mangaAdapter);

        adView = view.findViewById(R.id.adView);

        MainActivity.userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if(currentUser == null) {
                adView.setVisibility(View.GONE);
            } else {
                if(currentUser.getVipExpiredTimestamp() < System.currentTimeMillis() / 1000) {
                    adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                } else {
                    adView.setVisibility(View.GONE);
                }
            }
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load manga data
        loadMangaData();

        // Set click listener for grid items
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the position of the item clicked in the adapter
                int adapterPosition = gridView.getPositionForView(view);

                // Ensure the adapter position is valid
                if (adapterPosition != GridView.INVALID_POSITION) {
                    MangaModel selectedManga = mangaList.get(adapterPosition);
                    HomeFragment.this.onItemClick(selectedManga.getId(), selectedManga.getImage(), selectedManga.getName());
                }
            }
        });

        minigameBtn = view.findViewById(R.id.minigame_btn);
        minigameBtn.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), QuizActivity.class);
            startActivity(i);
        });

        return view;
    }

    private void loadMangaData() {
        // Query Firestore for manga data
        db.collection("Manga")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mangaList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MangaModel manga = document.toObject(MangaModel.class);
                            mangaAdapter.addManga(0, manga);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error loading manga: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void onItemClick(String id, String image, String name) {
        Intent i = new Intent(getContext(), MangaDetailActivity.class);
        i.putExtra("id", id);
        i.putExtra("image", image);
        i.putExtra("name", name);
        startActivity(i);
    }
}
