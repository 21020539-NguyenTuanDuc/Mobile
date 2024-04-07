package com.example.mobile.SettingPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mobile.MainActivity;
import com.example.mobile.R;
import com.example.mobile.databinding.ActivityEditInfoBinding;
import com.example.mobile.databinding.ActivitySubscriptionBinding;

public class SubscriptionActivity extends AppCompatActivity {
    ActivitySubscriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());

        if(MainActivity.currentUser.getVipExpiredTimestamp() > System.currentTimeMillis() / 1000) {
            binding.currentPlan.setText("Your current Plan: VIP");
        } else {
            binding.currentPlan.setText("Your current Plan: Standard");
        }

        binding.thirtyDaysSub.setOnClickListener(v -> {
            binding.thirtyDaysSub.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                binding.thirtyDaysSub.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }).start();
            Intent intent = new Intent(SubscriptionActivity.this, BuyingSubActivity.class);
            intent.putExtra("pricePerDay", 1000);
            intent.putExtra("duration", 30);
            intent.putExtra("save", 0);
            intent.putExtra("totalCoin",100);
            intent.putExtra("totalAmount", 30000);
            intent.putExtra("packageType", 0);
            startActivity(intent);
        });

        binding.ninetyDaysSub.setOnClickListener(v -> {
            binding.ninetyDaysSub.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                binding.ninetyDaysSub.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }).start();
            Intent intent = new Intent(SubscriptionActivity.this, BuyingSubActivity.class);
            intent.putExtra("pricePerDay", 888);
            intent.putExtra("duration", 90);
            intent.putExtra("save", 10000);
            intent.putExtra("totalCoin",375);
            intent.putExtra("totalAmount", 80000);
            intent.putExtra("packageType", 1);
            startActivity(intent);
        });

        binding.oneEightyDaysSub.setOnClickListener(v -> {
            binding.oneEightyDaysSub.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                binding.oneEightyDaysSub.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }).start();
            Intent intent = new Intent(SubscriptionActivity.this, BuyingSubActivity.class);
            intent.putExtra("pricePerDay", 850);
            intent.putExtra("duration", 180);
            intent.putExtra("save", 27000);
            intent.putExtra("totalCoin",940);
            intent.putExtra("totalAmount", 153000);
            intent.putExtra("packageType", 2);
            startActivity(intent);
        });

        binding.threeSixtyDaysSub.setOnClickListener(v -> {
            binding.threeSixtyDaysSub.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).withEndAction(() -> {
                binding.threeSixtyDaysSub.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }).start();
            Intent intent = new Intent(SubscriptionActivity.this, BuyingSubActivity.class);
            intent.putExtra("pricePerDay", 800);
            intent.putExtra("duration", 360);
            intent.putExtra("save", 72000);
            intent.putExtra("totalCoin",2260);
            intent.putExtra("totalAmount", 288000);
            intent.putExtra("packageType", 3);
            startActivity(intent);
        });

        setContentView(binding.getRoot());
    }
}