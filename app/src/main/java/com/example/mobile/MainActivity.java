package com.example.mobile;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobile.MainActivityPackage.FavoriteFragment;
import com.example.mobile.MainActivityPackage.HistoryFragment;
import com.example.mobile.MainActivityPackage.HomeFragment;
import com.example.mobile.MainActivityPackage.SearchFragment;
import com.example.mobile.MainActivityPackage.SettingFragment;
import com.example.mobile.Model.UserModel;
import com.example.mobile.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    public static UserModel currentUser;
    private String FragmentID = "HomeFragment";
    public static Uri profilePicture = null;
    FirebaseUser user;
    ActivityMainBinding binding;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
//        currentUser = new UserModel(user.getUid(), user.getDisplayName(), null, user.getEmail()); // temp fix for current user not loaded when from Main info to setting fragment

        FragmentID = getIntent().getStringExtra("FragmentID");
        if (FragmentID != null) {
            switch (FragmentID) {
                case "HomeFragment":
                    replaceFragment(new HomeFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.home);
                    break;
                case "FavoriteFragment":
                    replaceFragment(new FavoriteFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.favorite);
                    break;
                case "SearchFragment":
                    replaceFragment(new SearchFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.search);
                    break;
                case "HistoryFragment":
                    replaceFragment(new HistoryFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.history);
                    break;
                case "SettingFragment":
                    replaceFragment(new SettingFragment());
                    binding.bottomNavigationView.setSelectedItemId(R.id.setting);
                    break;
            }
        } else {
            FragmentID = "HomeFragment";
            replaceFragment(new HomeFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.home);
        }

        getUser();

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.favorite:
                    FragmentID = "FavoriteFragment";
                    replaceFragment(new FavoriteFragment());
                    break;
                case R.id.search:
                    FragmentID = "SearchFragment";
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.history:
                    FragmentID = "HistoryFragment";
                    replaceFragment(new HistoryFragment());
                    break;
                case R.id.setting:
                    FragmentID = "SettingFragment";
                    replaceFragment(new SettingFragment());
                    break;
                default:
                    FragmentID = "HomeFragment";
                    replaceFragment(new HomeFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    protected void getUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (user != null) {
            String Uid = user.getUid();
//            Comment this to fix bug of current user not loaded when from edit info to setting fragment
//            currentUser = new UserModel(Uid, null, null, null);
            FirebaseFirestore.getInstance().collection("User").document(Uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            currentUser = documentSnapshot.toObject(UserModel.class);
                            assert currentUser != null;
                            currentUser.setId(Uid);
                            getProfilePicture();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "User Failed!", Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismiss();
                        }
                    });
        }
    }

    public void getProfilePicture() {
        FirebaseStorage.getInstance().getReference().child("images/" + currentUser.getId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profilePicture = uri;
                        sweetAlertDialog.dismiss();
                        Log.d("UI", "Get profile picture");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sweetAlertDialog.dismiss();
                        Log.d("UI", e.getMessage());
                    }
                });
    }
}