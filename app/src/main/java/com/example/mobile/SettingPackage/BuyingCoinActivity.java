package com.example.mobile.SettingPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mobile.MainActivity;
import com.example.mobile.Model.CoinTransactionModel;
import com.example.mobile.Model.CreateOrder;
import com.example.mobile.Util.NumberTextWatcherForThousand;
import com.example.mobile.databinding.ActivityBuyingCoinBinding;
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

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class BuyingCoinActivity extends AppCompatActivity {

    ActivityBuyingCoinBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBuyingCoinBinding.inflate(getLayoutInflater());

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        Date currentDate = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(currentDate);
        binding.dateReceipt.setText(dateString);
        binding.numberOfCoin.setTransformationMethod(new NumericKeyBoardTransformationMethod());

        binding.numberOfCoin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
//                try {
                if (!editable.toString().isEmpty()) {
                    binding.price.setText(NumberTextWatcherForThousand.getDecimalFormattedString(String.valueOf(Long.parseLong(editable.toString()) * 100)));
                    long price = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.price.getText().toString()));
                    long discount = 0;
                    if (price >= 100000) {
                        discount = (long) (price * 0.1);
                        binding.discount.setText(NumberTextWatcherForThousand.getDecimalFormattedString(String.valueOf(discount)));
                    } else {
                        binding.discount.setText("0");
                    }
                    binding.total.setText(NumberTextWatcherForThousand.getDecimalFormattedString(String.valueOf(price - discount)));
//                } catch (Exception e) {
//                    System.err.println(e.getMessage());
//                    binding.price.setText("0");
//                    binding.discount.setText("0");
//                }
                }
                else {
                    binding.price.setText("0");
                    binding.discount.setText("0");
                    binding.total.setText("0");
                }
            }
        });


        // Zalo pay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        // End Zalo pay
        binding.zalopayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long total = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.total.getText().toString()));
                long coin = Long.parseLong(NumberTextWatcherForThousand.trimCommaOfString(binding.numberOfCoin.getText().toString()));
                requestZalo(total, coin);
            }
        });

        setContentView(binding.getRoot());
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }

    private void requestZalo(long total, long coin) {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(String.valueOf(total));
            Log.d("Amount", String.valueOf(total));
            String code = data.getString("return_code");

//            Log.d("ZaloPayment", "This is code " + code);

            if (code.equals("1")) {
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                String token = data.getString("zp_trans_token");
                Log.d("ZaloPayment", "This is token " + token);
                ZaloPaySDK.getInstance().payOrder(BuyingCoinActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        Log.d("ZaloPayment", "Payment complete " + total);

                        String id = FirebaseFirestore.getInstance().collection("CoinTransaction").document().getId();
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis() / 1000, 0);
                        String userID = MainActivity.currentUser.getId();
                        CoinTransactionModel transactionModel = new CoinTransactionModel(id, total, timestamp, coin, token, userID);
                        DocumentReference transactionRef = FirebaseFirestore.getInstance().collection("CoinTransaction").document(id);
//                        System.out.println("adniqndioqwnfio tai sao lai ngu the");
                        batch.set(transactionRef, transactionModel);

                        FirebaseFirestore.getInstance().collection("User").document(userID)
                                .update("coin", MainActivity.currentUser.getCoin() + coin);

                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("ZaloPayment", "Add transaction and ticket to database");
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.d("ZaloPayment", "Add transaction and ticket to database failed");
                                });

                        Intent intent = new Intent(BuyingCoinActivity.this, SuccessPayment2Activity.class);
                        intent.putExtra("amount", total);
                        intent.putExtra("transactionID", id);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Log.d("ZaloPayment", "Payment cancel");
                        Toast.makeText(BuyingCoinActivity.this, "Payment cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Log.d("ZaloPayment", "Payment error");
                        Toast.makeText(BuyingCoinActivity.this, "Payment error", Toast.LENGTH_SHORT).show();
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