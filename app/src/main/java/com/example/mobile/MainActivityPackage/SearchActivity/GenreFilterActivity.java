package com.example.mobile.MainActivityPackage.SearchActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class GenreFilterActivity extends AppCompatActivity {

    private ListView genreListView;
    private ImageButton okButton;
    private List<String> selectedGenres = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.genre_list);

        genreListView = findViewById(R.id.genreListView);
        okButton = findViewById(R.id.ok_button);

        // Danh sách thể loại mẫu (có thể thay đổi tùy theo nhu cầu của ứng dụng)
        List<String> genreList = new ArrayList<>();
        genreList.add("Comedy");
        genreList.add("Drama");
        genreList.add("Mystery");
        genreList.add("Romance");
        genreList.add("School Life");
        genreList.add("Detective");
        genreList.add("18+");
        genreList.add("Harem");
        genreList.add("Fantasy");
        genreList.add("Historical");
        genreList.add("Shounen");
        genreList.add("Action");
        genreList.add("Supernatural");
        genreList.add("Adventure");
        genreList.add("Horor");

        // Adapter cho ListView hiển thị danh sách thể loại
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, genreList);
        genreListView.setAdapter(adapter);
        genreListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Xử lý sự kiện khi chọn các thể loại
        genreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = genreList.get(position);
                if (selectedGenres.contains(selectedGenre)) {
                    selectedGenres.remove(selectedGenre);
                } else {
                    selectedGenres.add(selectedGenre);
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút OK
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGenres.isEmpty()) {
                    Toast.makeText(GenreFilterActivity.this, "Vui lòng chọn ít nhất một thể loại", Toast.LENGTH_SHORT).show();
                } else {
                    // Gửi danh sách các thể loại đã chọn về activity trước (hoặc xử lý theo nhu cầu)
                    Intent resultIntent = new Intent();
                    resultIntent.putStringArrayListExtra("selectedGenres", (ArrayList<String>) selectedGenres);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
