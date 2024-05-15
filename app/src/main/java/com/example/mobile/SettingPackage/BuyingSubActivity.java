package com.example.mobile.SettingPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mobile.MainActivity;
import com.example.mobile.Model.CreateOrder;
import com.example.mobile.Model.SubTransactionModel;
import com.example.mobile.Util.NumberTextWatcherForThousand;
import com.example.mobile.databinding.ActivityBuyingSubBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class BuyingSubActivity extends AppCompatActivity {
    ActivityBuyingSubBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyingSubBinding.inflate(getLayoutInflater());

        long pricePerDay = getIntent().getIntExtra("pricePerDay", 0);
        long duration = getIntent().getIntExtra("duration", 0);
        long save = getIntent().getIntExtra("save", 0);
        long totalCoin = getIntent().getIntExtra("totalCoin", 0);
        long totalAmount = getIntent().getIntExtra("totalAmount", 0);
        int packageType = getIntent().getIntExtra("packageType", 0);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        Date currentDate = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(currentDate);
        binding.dateReceipt.setText(dateString);

        binding.pricePerDay.setText(numberFormat.format(pricePerDay));
        binding.VIPDuration.setText(numberFormat.format(duration));
        binding.save.setText(numberFormat.format(save));
        binding.totalCoin.setText(numberFormat.format(totalCoin));
        binding.totalPrice.setText(numberFormat.format(totalAmount));

        long currentTimestamp = System.currentTimeMillis();
        long durationMillis = TimeUnit.DAYS.toMillis(duration);
        long futureTimestamp = currentTimestamp + durationMillis;
        Date futureDate = new Date(futureTimestamp);
        if (MainActivity.currentUser.getVipExpiredTimestamp() == -1 || MainActivity.currentUser.getVipExpiredTimestamp() < System.currentTimeMillis() / 1000) {
            binding.expiredDate.setText(formatter.format(futureDate));
        } else {
            long currentVipExpiredTimestamp = MainActivity.currentUser.getVipExpiredTimestamp();
            long futureVipExpiredTimestamp = currentVipExpiredTimestamp * 1000 + durationMillis;
            Date futureVipDate = new Date(futureVipExpiredTimestamp);
            binding.expiredDate.setText(formatter.format(futureVipDate));
        }

        // Zalo pay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        // End Zalo pay
        binding.zalopayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestZalo(packageType, duration, totalCoin);
            }
        });

        setContentView(binding.getRoot());
    }

    private void requestZalo(int packageType, long duration, long totalCoin) {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));
            Log.d("Amount", NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));
            String code = data.getString("return_code");

            if (code.equals("1")) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(BuyingSubActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        Log.d("ZaloPayment", "Payment complete " + NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));

                        String id = FirebaseFirestore.getInstance().collection("Transaction").document().getId();
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis() / 1000, 0);
                        long amount = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.totalPrice.getText().toString()));
                        String userID = MainActivity.currentUser.getId();
                        SubTransactionModel transactionModel = new SubTransactionModel(id, timestamp, amount, token, packageType, userID);
                        DocumentReference transactionRef = FirebaseFirestore.getInstance().collection("Transaction").document(id);
                        batch.set(transactionRef, transactionModel);

                        long currentTimestamp = System.currentTimeMillis();
                        long durationMillis = TimeUnit.DAYS.toMillis(duration);
                        long futureTimestamp = currentTimestamp + durationMillis;
                        long updatedVipExpiredTimestamp = futureTimestamp / 1000;

                        if (MainActivity.currentUser.getVipExpiredTimestamp() != 1 && MainActivity.currentUser.getVipExpiredTimestamp() < System.currentTimeMillis() / 1000) {
                            long currentVipExpiredTimestamp = MainActivity.currentUser.getVipExpiredTimestamp();
                            updatedVipExpiredTimestamp = currentTimestamp + durationMillis / 1000;
                        }

                        FirebaseFirestore.getInstance().collection("User").document(userID)
                                .update("vipExpiredTimestamp", updatedVipExpiredTimestamp);
                        FirebaseFirestore.getInstance().collection("User").document(userID)
                                .update("coin", MainActivity.currentUser.getCoin() + totalCoin);

                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("ZaloPayment", "Add transaction and ticket to database");
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.d("ZaloPayment", "Add transaction and ticket to database failed");
                                });

                        Intent intent = new Intent(BuyingSubActivity.this, SuccessPaymentActivity.class);
                        intent.putExtra("amount", binding.totalPrice.getText().toString());
                        intent.putExtra("transactionID", id);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Log.d("ZaloPayment", "Payment cancel");
                        Toast.makeText(BuyingSubActivity.this, "Payment cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.d("ZaloPayment", "Payment error");
                        Toast.makeText(BuyingSubActivity.this, "Payment error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}