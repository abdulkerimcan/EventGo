package com.furkankerim.eventgo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView txtMail;
    private EditText editTextUsername, editTextPassword, editTextPasswordOnay;
    private TextInputLayout inputUsername, inputPassword, inputConfirm;
    private String userName, password, passwordOnay;
    private androidx.appcompat.widget.Toolbar mtoolbar;
    private LinearLayout mainLayout;

    private FirebaseFirestore mFirestore;
    private StorageReference mReference;
    private FirebaseStorage mStorage;

    private RadioButton orgbtn, cstmerbtn;
    private ImageView imguser;
    private Uri uriImgdata;
    private Bitmap selectedImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Intent intent = getIntent();
        txtMail = findViewById(R.id.signUp_txtGelenMail);
        editTextUsername = findViewById(R.id.signUp_EditUsername);
        editTextPassword = findViewById(R.id.signUp_EditSifre);
        editTextPasswordOnay = findViewById(R.id.signUp_EditSifreOnay);
        inputUsername = findViewById(R.id.sign_up_input_mail);
        inputPassword = findViewById(R.id.sign_up_input_sifre);
        inputConfirm = findViewById(R.id.sign_up_input_confirm);
        mainLayout = findViewById(R.id.signup_layout);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mReference = mStorage.getReference();

        imguser = findViewById(R.id.upload_userimg);

        cstmerbtn = findViewById(R.id.customer_radiobutton);
        orgbtn = findViewById(R.id.org_radiobutton);
        mtoolbar = findViewById(R.id.signUp_toolbar);
        setSupportActionBar(mtoolbar);


        mAuth = FirebaseAuth.getInstance();
        txtMail.setText(intent.getStringExtra("email"));
    }

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        txtMail.setText(intent.getStringExtra("email"));
        super.onResume();
    }

    public void btnSignUp(View v) {
        userName = editTextUsername.getText().toString();
        password = editTextPassword.getText().toString();
        passwordOnay = editTextPasswordOnay.getText().toString();

        if (uriImgdata != null && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(passwordOnay) && (cstmerbtn.isChecked() || orgbtn.isChecked())) {
            if (password.equals(passwordOnay)) {
                mAuth.createUserWithEmailAndPassword(txtMail.getText().toString(), password)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();


                        if (uriImgdata != null) {
                            UUID uuid = UUID.randomUUID();
                            String path = "userimages/" + uuid + ".jpeg";
                            mReference.child(path).putFile(uriImgdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StorageReference newRef = FirebaseStorage.getInstance().getReference(path);
                                    newRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();

                                            User user = new User(firebaseUser.getEmail(), userName, downloadUrl, orgbtn.isChecked());
                                            mFirestore.collection("User").document(firebaseUser.getUid()).set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(SignUpActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                                            Intent mainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                                                            startActivity(mainActivity);
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            User user = new User(firebaseUser.getEmail(), userName, "default", orgbtn.isChecked());
                            mFirestore.collection("User").document(firebaseUser.getUid()).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "Registration Successful.", Toast.LENGTH_SHORT).show();
                                            Intent mainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(mainActivity);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            } else {
                Snackbar.make(mainLayout, "Passwords do not match.", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            if (uriImgdata != null) {
                if (!TextUtils.isEmpty(userName)) {
                    if (!TextUtils.isEmpty(password)) {
                        if (!TextUtils.isEmpty(passwordOnay)) {
                            if (cstmerbtn.isChecked() || orgbtn.isChecked()) {
                                System.out.println("Bu kısım çalışmayacak üst kısım çalışırsa");
                            } else {
                                Snackbar.make(mainLayout, "Customer Type cannot be empty.", Snackbar.LENGTH_SHORT).show();
                            }
                        } else
                            Snackbar.make(mainLayout, "Password Confirm cannot be empty.", Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(mainLayout, "Password cannot be empty.", Snackbar.LENGTH_SHORT).show();
                } else
                    Snackbar.make(mainLayout, "Username cannot be empty.", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mainLayout, "User Picture cannot be empty.", Snackbar.LENGTH_SHORT).show();
            }
        }


    }

    public void selectImg(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            uriImgdata = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), uriImgdata);
                    selectedImg = ImageDecoder.decodeBitmap(source);
                    imguser.setImageBitmap(selectedImg);
                } else {
                    selectedImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImgdata);
                    imguser.setImageBitmap(selectedImg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}