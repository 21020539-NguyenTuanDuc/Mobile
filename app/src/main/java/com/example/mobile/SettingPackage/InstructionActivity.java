package com.example.mobile.SettingPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.R;
import com.example.mobile.databinding.ActivityInstructionBinding;

public class InstructionActivity extends AppCompatActivity {
    ActivityInstructionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.instruction);
        itemConstructor();
    }

    private void itemConstructor() {
        binding.editProfileInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editProfileInstruction.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.editProfileInstruction.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                }).start();
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "editProfile");
                startActivity(intent);
            }
        });
        binding.languageInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.languageInstruction.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.languageInstruction.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                }).start();
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "language");
                startActivity(intent);
            }
        });
        binding.contactAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.contactAdmin.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.contactAdmin.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                }).start();
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "contactAdmin");
                startActivity(intent);
            }
        });
        binding.deleteAccountInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.deleteAccountInstruction.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.deleteAccountInstruction.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                }).start();
                Intent intent = new Intent(InstructionActivity.this, InstructionItemContentActivity.class);
                intent.putExtra("instruction", "deleteAccount");
                startActivity(intent);
            }
        });

    }
}