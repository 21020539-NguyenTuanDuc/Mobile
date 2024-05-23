package com.example.mobile.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobile.Model.ChapterModel;
import com.example.mobile.Model.UserModel;
import com.example.mobile.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<ChapterModel> chapterList;
    private UserModel userModel;

    public ChapterAdapter(List<ChapterModel> chapterList) {
        this.chapterList = chapterList;
        this.userModel = userModel;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_items, parent, false);
        return new ChapterViewHolder(view);
    }

    public interface OnChapterClickListener {
        void onChapterClick(int position);
    }

    private OnChapterClickListener onChapterClickListener;

    public void setOnChapterClickListener(OnChapterClickListener listener) {
        this.onChapterClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        ChapterModel chapter = chapterList.get(position);
        holder.bind(chapter);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition(); // Lấy position của item được nhấn
                if (onChapterClickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    onChapterClickListener.onChapterClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {

        private TextView tvChapter;
        private ImageView ivChapter;
        private Button buttonPurchase;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapter = itemView.findViewById(R.id.tvChapter);
            ivChapter = itemView.findViewById(R.id.ivChapter);
            buttonPurchase = itemView.findViewById(R.id.buttonPurchase);
        }

        // Trong phương thức bind của ChapterViewHolder
        public void bind(ChapterModel chapter) {
            // Set text for tvChapter
            tvChapter.setText("Chapter " + (getAdapterPosition() + 1));

            // Load first image from Firebase Storage as chapter thumbnail
            if (chapter.getList() != null && chapter.getList().size() > 0) {
                String firstImageName = chapter.getList().get(0);
                StorageReference imageRef = storage.getReference().child("images/" + firstImageName);
                // Load image from Storage
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Ensure uri is not null before loading image
                    if (uri != null) {
                        Glide.with(itemView.getContext())
                                .load(uri)
                                .centerCrop()
                                .into(ivChapter);
                    }
                }).addOnFailureListener(exception -> {
                    // Handle failure
                });
            }

            // Set text and state for buttonPurchase based on chapter's price
            if (chapter.getPrice() > 0) {
                mAuth = FirebaseAuth.getInstance();
                String userId = mAuth.getCurrentUser().getUid();

                // Kiểm tra xem người dùng đã mua chap này chưa
                if (chapter.getUsers() != null && chapter.getUsers().contains(userId)) {
                    buttonPurchase.setText("Paid");
                    buttonPurchase.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FF00")));
                    buttonPurchase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Xử lý khi chap miễn phí được nhấn
                            itemView.performClick();
                        }
                    });
                } else {
                    // Người dùng chưa mua chap này
                    // Truy vấn thông tin userModel từ Firestore
                    db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("User").document(userId);
                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userModel = documentSnapshot.toObject(UserModel.class);
                            buttonPurchase.setText("Vip");
                            buttonPurchase.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC0CB"))); // Màu hồng
                            buttonPurchase.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Xử lý việc mua chap
                                    showDialogToPurchase(userModel, chapter);
                                }
                            });
                        } else {
                            // Xử lý khi không thể tải thông tin người dùng
                            Toast.makeText(itemView.getContext(), "Không thể tải thông tin người dùng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        // Xử lý khi có lỗi xảy ra trong quá trình truy vấn
                    });
                }
            } else {
                // Nếu chap miễn phí
                buttonPurchase.setText("Free");
                buttonPurchase.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#33AFE8"))); // Màu xanh dương
                buttonPurchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý khi chap miễn phí được nhấn
                        itemView.performClick();
                    }
                });
            }
        }

        private void showDialogToPurchase(UserModel userModel, ChapterModel chapter) {
            if (userModel != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Xác nhận mua");
                builder.setMessage("Bạn đang có " + userModel.getCoin() + " coin. Bạn có muốn mua chap này với giá " + chapter.getPrice() + " coin không?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        purchaseChapter(userModel, chapter);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Hủy mua, không thực hiện gì
                    }
                });
                builder.show();
            } else {
                // Xử lý trường hợp khi userModel là null
                Toast.makeText(itemView.getContext(), "Không thể tải thông tin người dùng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        }

        private void purchaseChapter(UserModel userModel, ChapterModel chapter) {
            if (mAuth == null) {
                mAuth = FirebaseAuth.getInstance();
            }
            String userId = mAuth.getCurrentUser().getUid();

            // Lấy thông tin người dùng từ Firestore
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("User").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Kiểm tra số coin của người dùng có đủ để mua chap hay không
                    long userCoins = userModel.getCoin();
                    long chapterPrice = chapter.getPrice();
                    if (userCoins >= chapterPrice) {
                        // Kiểm tra xem người dùng đã mua chap này trước đó hay chưa
                        List<String> userList = chapter.getUsers();
                        if (userList != null && userList.contains(userId)) {
                            // Hiển thị thông báo cho người dùng rằng đã mua chap này trước đó
                            Toast.makeText(itemView.getContext(), "Bạn đã mua chap này rồi", Toast.LENGTH_SHORT).show();
                        } else {
                            // Trừ coin của người dùng sau khi thanh toán
                            userModel.setCoin(userCoins - chapterPrice);

                            // Thêm id của người dùng vào danh sách users của chap
                            if (userList == null) {
                                userList = new ArrayList<>();
                            }
                            userList.add(userId);
                            chapter.setUsers(userList);

                            buttonPurchase.setText("Paid");
                            buttonPurchase.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FF00")));
                            buttonPurchase.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Xử lý khi chap miễn phí được nhấn
                                    itemView.performClick();
                                }
                            });
                            // Cập nhật dữ liệu người dùng và chap lên Firestore
                            updateUserAndChapter(userRef, userModel, chapter);
                        }
                    } else {
                        // Hiển thị thông báo cho người dùng rằng số coin không đủ để mua chap
                        Toast.makeText(itemView.getContext(), "Số coin không đủ để mua chap này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý trường hợp không tìm thấy thông tin người dùng
                    Toast.makeText(itemView.getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Xử lý trường hợp lỗi khi truy vấn Firestore
                Toast.makeText(itemView.getContext(), "Đã xảy ra lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            });
        }

        private void updateUserAndChapter(DocumentReference userRef, UserModel user, ChapterModel chapter) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null && chapter != null && chapter.getId() != null) {
                String userId = currentUser.getUid();

                // Cập nhật dữ liệu người dùng và chap lên Firestore
                userRef.set(user)
                        .addOnSuccessListener(aVoid -> {
                            // Cập nhật dữ liệu chap
                            FirebaseFirestore.getInstance().collection("Chapter")
                                    .document(chapter.getId())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            ChapterModel existingChapter = documentSnapshot.toObject(ChapterModel.class);
                                            if (existingChapter != null) {
                                                List<String> userList = existingChapter.getUsers();
                                                if (userList == null) {
                                                    userList = new ArrayList<>();
                                                }
                                                userList.add(userId); // Sử dụng userId từ FirebaseAuth
                                                existingChapter.setUsers(userList);
                                                // Cập nhật dữ liệu chap vào Firestore
                                                FirebaseFirestore.getInstance().collection("Chapter")
                                                        .document(chapter.getId())
                                                        .update("users", userList)
                                                        .addOnSuccessListener(aVoid1 -> {
                                                            Toast.makeText(itemView.getContext(), "Đã mua thành công!", Toast.LENGTH_SHORT).show();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(itemView.getContext(), "Rất tiếc, giao dịch thất bại!", Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        } else {
                                            // Xử lý trường hợp không tìm thấy dữ liệu chap
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xử lý trường hợp lỗi khi truy vấn dữ liệu chap
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý trường hợp lỗi khi cập nhật dữ liệu người dùng
                        });
            } else {
                // Xử lý trường hợp currentUser, chapter hoặc chapter.getId() là null
                Toast.makeText(itemView.getContext(), "Lỗi khi cập nhật dữ liệu: Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

