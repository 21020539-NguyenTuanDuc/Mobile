package com.example.mobile.MainActivityPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mobile.MainActivityPackage.adapter.ComicAdapter;
import com.example.mobile.MangaDetailPackage.MangaDetailActivity;
import com.example.mobile.MainActivityPackage.SearchActivity.GenreFilterActivity;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

    private GridView gridView;
    private List<MangaModel> mangaList;
    private ComicAdapter comicAdapter;
    private FirebaseFirestore db;
    private EditText searchEditText;

    private static final int REQUEST_CODE_FILTER = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        gridView = view.findViewById(R.id.gdvComicList);
        ImageButton filteredButton = view.findViewById(R.id.filterButton);
        mangaList = new ArrayList<>();
        comicAdapter = new ComicAdapter(getActivity(), this); // Pass the fragment reference
        gridView.setAdapter(comicAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load manga data
        loadMangaData();

        // Set click listener for grid items
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            // Get the position of the item clicked in the adapter
            int adapterPosition = gridView.getPositionForView(view1);

            // Ensure the adapter position is valid
            if (adapterPosition != GridView.INVALID_POSITION) {
                MangaModel selectedManga = mangaList.get(adapterPosition);
                onItemClick(selectedManga.getId(), selectedManga.getImage(), selectedManga.getName());
            }
        });

        filteredButton.setOnClickListener(v -> openGenreFilterActivity());

        // EditText for searching
        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Perform actions before text changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform actions while text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Perform actions after text changed
                performSearch(s.toString());
            }
        });

        return view;
    }

    private void openGenreFilterActivity() {
        Intent intent = new Intent(getContext(), GenreFilterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FILTER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILTER && resultCode == AppCompatActivity.RESULT_OK) {
            ArrayList<String> selectedGenres = data.getStringArrayListExtra("selectedGenres");
            if (selectedGenres != null) {
                // Xử lý danh sách các thể loại được chọn
                StringBuilder selectedGenresString = new StringBuilder("Selected genres: ");
                for (String genre : selectedGenres) {
                    selectedGenresString.append(genre).append(", ");
                }
                performFilter(selectedGenres);
                Toast.makeText(getContext(), selectedGenresString.toString(), Toast.LENGTH_SHORT).show();
            }
        }
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
                            comicAdapter.addManga(0, manga);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error loading manga: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performSearch(String query) {
        // Perform search based on the query and update data in GridView or RecyclerView
        // For example: update manga list based on search result
        comicAdapter.searchComicByName(query);
    }

    private void performFilter(List<String> selectedGenres) {
        comicAdapter.searchComicByGenre(selectedGenres);
    }

    public void onItemClick(String id, String image, String name) {
        Intent i = new Intent(getContext(), MangaDetailActivity.class);
        i.putExtra("id", id);
        i.putExtra("image", image);
        i.putExtra("name", name);
        startActivity(i);
    }
}
