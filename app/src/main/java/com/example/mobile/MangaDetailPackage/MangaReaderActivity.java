package com.example.mobile.MangaDetailPackage;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mobile.Adapter.PageAdapter;
import com.example.mobile.Model.ChapterModel;
import com.example.mobile.Model.MangaModel;
import com.example.mobile.Model.UserModel;
import com.example.mobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MangaReaderActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private ImageButton imageButton;
    private ScrollView scrollView;
    private ImageView imageManga;
    private Button b1, b2, b3;
    MangaModel manga;
    ChapterModel chapter;
    private List<String> chapList = new ArrayList<>();
    private int currentChapterIndex;
    private RecyclerView recyclerView;
    private PageAdapter pageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_reader);

        storage = FirebaseStorage.getInstance();
        manga = (MangaModel) getIntent().getSerializableExtra("manga");
        chapter = (ChapterModel) getIntent().getSerializableExtra("chapter");

        // Init views
        scrollView = findViewById(R.id.scrollView3);
        recyclerView = findViewById(R.id.recyclerView);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Get chapList from intent
        if (getIntent().hasExtra("chapList")) {
            chapList = (List<String>) getIntent().getSerializableExtra("chapList");
        }

        List<String> imageList = getIntent().getStringArrayListExtra("imageList");
        updateRecyclerView(imageList);

        int currentChap = getIntent().getIntExtra("currentChap", 0) - 1;

        currentChapterIndex = getIntent().getIntExtra("currentChapterIndex", currentChap);
        loadChapterImage(currentChapterIndex);

        b1.setOnClickListener(v -> {
            if (currentChapterIndex > 0) {
                db.collection("Manga").document(manga.getId())
                        .update("currentChap", currentChapterIndex)
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
                currentChapterIndex--;
                loadChapterImage(currentChapterIndex);
                b2.setText("Chap " + (currentChapterIndex + 1));
            }
        });

        b3.setOnClickListener(v -> {
            if (currentChapterIndex < chapList.size() - 1) {
                db.collection("Manga").document(manga.getId())
                        .update("currentChap", currentChapterIndex + 2)
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
                currentChapterIndex++;
                loadChapterImage(currentChapterIndex);
                b2.setText("Chap " + (currentChapterIndex + 1));
            }
        });
        b2.setOnClickListener(view -> {
            Intent intent = new Intent(MangaReaderActivity.this, ChapterActivity.class);
            intent.putExtra("manga", manga);
            startActivity(intent);
        });

        b2.setText("Chap " + (currentChapterIndex + 1));
    }

    private void loadChapterImage(int index) {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        if (index < 0 || index >= chapList.size()) {
            return;
        }

        String chapId = chapList.get(index);
        if (chapId == null) {
            Log.e(TAG, "ChapId is null");
            return;
        }

        db.collection("Chapter").document(chapId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ChapterModel chapter = document.toObject(ChapterModel.class);
                            if (chapter != null && chapter.getPrice() > 0) {
                                // Nếu chapter có giá tiền
                                List<String> users = chapter.getUsers();
                                if (users != null && users.contains(userId)) {
                                    // Nếu danh sách users chứa id của currentUser
                                    List<String> imageList = chapter.getList();
                                    updateRecyclerView(imageList);
                                } else {
                                    // Người dùng không có quyền đọc chap này
                                    // Truy vấn thông tin userModel từ Firestore
                                    DocumentReference userRef = FirebaseFirestore.getInstance().collection("User").document(userId);
                                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                            if (userModel != null) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(MangaReaderActivity.this);
                                                builder.setTitle("Xác nhận mua");
                                                builder.setMessage("Bạn đang có " + userModel.getCoin() + " coin. Bạn có muốn mua chap này với giá " + chapter.getPrice() + " coin không?");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        purchaseChapter(userModel, chapter, new PurchaseCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                // Cập nhật giao diện sau khi mua thành công
                                                                Toast.makeText(MangaReaderActivity.this, "Giao dịch thành công! Chúc bạn có những giờ phút giải trí tuyệt vời với Manga++!", Toast.LENGTH_SHORT).show();
                                                                List<String> imageList = chapter.getList();
                                                                updateRecyclerView(imageList);
                                                            }

                                                            @Override
                                                            public void onFailure() {
                                                                // Xử lý khi việc mua chap thất bại
                                                                Toast.makeText(MangaReaderActivity.this, "Giao dịch thất bại! Vui lòng nạp thêm coin để mua chap!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(MangaReaderActivity.this, ChapterActivity.class);
                                                                intent.putExtra("manga", manga);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                });
                                                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // Chuyển người dùng đến ChapterActivity
                                                        Toast.makeText(MangaReaderActivity.this, "Bạn không có quyền đọc chap này, vui lòng chuyển sang giao diện Chapter và nhấn vào nút 'VIP' để mua chap!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(MangaReaderActivity.this, ChapterActivity.class);
                                                        intent.putExtra("manga", manga);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                                // Tạo dialog và thiết lập không thể hủy bằng cách nhấn bên ngoài
                                                AlertDialog dialog = builder.create();
                                                dialog.setCancelable(false); // Ngăn người dùng nhấn bên ngoài dialog để đóng nó

                                                // Thiết lập OnCancelListener để xử lý khi dialog bị hủy
                                                dialog.setOnCancelListener(dialogInterface -> {
                                                    Toast.makeText(MangaReaderActivity.this, "Bạn không có quyền đọc chap này, vui lòng chuyển sang giao diện Chapter và nhấn vào nút 'VIP' để mua chap!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(MangaReaderActivity.this, ChapterActivity.class);
                                                    intent.putExtra("manga", manga);
                                                    startActivity(intent);
                                                    finish();
                                                });

                                                dialog.show();
                                            } else {
                                                Toast.makeText(MangaReaderActivity.this, "Không thể tải thông tin người dùng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MangaReaderActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(MangaReaderActivity.this, "Đã xảy ra lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                // Nếu chapter không có giá tiền hoặc có giá là 0
                                if (chapter != null && chapter.getList() != null) {
                                    List<String> imageList = chapter.getList();
                                    updateRecyclerView(imageList);
                                } else {
                                    Log.e(TAG, "Chapter or image list is null");
                                }
                            }
                        } else {
                            Log.e(TAG, "No such document");
                        }
                    } else {
                        Log.e(TAG, "get failed with ", task.getException());
                    }
                });
    }

    // Interface PurchaseCallback để xử lý kết quả mua chap
    private interface PurchaseCallback {
        void onSuccess();

        void onFailure();
    }

    private void purchaseChapter(UserModel userModel, ChapterModel chapter, PurchaseCallback callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null && chapter != null && chapter.getId() != null) {
            String userId = currentUser.getUid();

            // Lấy thông tin user từ Firestore
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("User").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    long userCoins = userModel.getCoin();
                    long chapterPrice = chapter.getPrice();
                    if (userCoins >= chapterPrice) {
                        userModel.setCoin(userCoins - chapterPrice);

                        // Truy vấn dữ liệu chap từ Firestore
                        DocumentReference chapterRef = FirebaseFirestore.getInstance().collection("Chapter").document(chapter.getId());
                        chapterRef.get().addOnSuccessListener(chapterDocumentSnapshot -> {
                            if (chapterDocumentSnapshot.exists()) {
                                ChapterModel existingChapter = chapterDocumentSnapshot.toObject(ChapterModel.class);
                                if (existingChapter != null) {
                                    List<String> userList = existingChapter.getUsers();
                                    if (userList == null) {
                                        userList = new ArrayList<>();
                                    }
                                    userList.add(userId); // Sử dụng userId từ FirebaseAuth
                                    existingChapter.setUsers(userList);

                                    List<String> finalUserList = userList;
                                    userRef.set(userModel).addOnSuccessListener(aVoid -> {
                                        chapterRef.update("users", finalUserList).addOnSuccessListener(aVoid1 -> {
                                            callback.onSuccess();
                                        }).addOnFailureListener(e -> {
                                            callback.onFailure();
                                        });
                                    }).addOnFailureListener(e -> {
                                        callback.onFailure();
                                    });
                                } else {
                                    callback.onFailure();
                                }
                            } else {
                                callback.onFailure();
                            }
                        }).addOnFailureListener(e -> {
                            callback.onFailure();
                        });
                    } else {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            }).addOnFailureListener(e -> {
                callback.onFailure();
            });
        } else {
            callback.onFailure();
        }
    }

    private void updateRecyclerView(List<String> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            Log.e(TAG, "Image list is null or empty");
            return;
        }

        // Khởi tạo adapter và thiết lập RecyclerView
        pageAdapter = new PageAdapter(imageList);
        recyclerView.setAdapter(pageAdapter);
    }
}
