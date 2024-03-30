package com.example.mobile.SettingPackage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.R;
import com.example.mobile.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends AppCompatActivity {
    ActivityPrivacyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(R.string.securitynprivacy);
    }
}