package com.furkankerim.eventgo.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends Fragment {
    private View v;
    private Bitmap selectedPic;
    private Uri uriPicData;
    private Button changePassword, changeUsername, update;
    private EditText oldPassword, newPassword, newConfirmPassword;
    private EditText newUsername, password;
    private RelativeLayout passwordLayout, usernameLayout, emptyLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;
    private StorageReference mReference;
    private FirebaseStorage mStorage;
    private ImageView ProfilePic;
    private User user;
    public void inner() {
        changeUsername = v.findViewById(R.id.btnChangeUsername);
        changePassword = v.findViewById(R.id.btnChangePassword);
        passwordLayout = v.findViewById(R.id.settings_LayoutChangePassword);
        usernameLayout = v.findViewById(R.id.settings_LayoutChangeUsername);
        emptyLayout = v.findViewById(R.id.settings_LayoutEmpty);
        ProfilePic = v.findViewById(R.id.settings_img);

        oldPassword = v.findViewById(R.id.settings_oldPassword);
        newPassword = v.findViewById(R.id.settings_newPassword);
        newConfirmPassword = v.findViewById(R.id.settings_newConfirmPassword);

        newUsername = v.findViewById(R.id.settings_newUsername);
        password = v.findViewById(R.id.settings_Password);

        update = v.findViewById(R.id.settings_btnUpdate);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mReference = mStorage.getReference();

        user = getCurrentUser();

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordLayout.setVisibility(View.VISIBLE);
                usernameLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                newUsername.setText("");
                password.setText("");
            }
        });

        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordLayout.setVisibility(View.GONE);
                usernameLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                oldPassword.setText("");
                newPassword.setText("");
                newConfirmPassword.setText("");
            }
        });

        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentToGallery, 2);
                }
            }
        });

    }

    private User getCurrentUser() {
        if (mUser != null) {
            mFirestore.collection("User").document(mUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> data = value.getData();
                        if (data != null) {
                            String usernametxt = (String) data.get("username");
                            String email = (String) data.get("email");
                            String url = (String) data.get("downloadUrl");
                            boolean organizer = (boolean) data.get("organizer");

                            user = new User(email, usernametxt, url, organizer);
                        }
                    }
                }
            });
            return user;
        }
        return null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        inner();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email;
                if ((passwordLayout.getVisibility() == v.GONE && usernameLayout.getVisibility() == v.GONE && uriPicData==null)) {
                    Toast.makeText(v.getContext(), "Select the type you want to change.", Toast.LENGTH_SHORT).show();

                } else {
                    if (mAuth.getCurrentUser() != null) {

                        if (uriPicData != null) {
                            ProgressDialog progressDialog = new ProgressDialog(getContext());
                            progressDialog.setTitle("Uploading...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            UUID uuid = UUID.randomUUID();
                            String path = "images/" + uuid + ".jpeg";
                            mReference.child(path).putFile(uriPicData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StorageReference newRef = FirebaseStorage.getInstance().getReference(path);
                                    newRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUri = uri.toString();
                                            mFirestore.collection("User").document(mUser.getUid())
                                                    .update("downloadUrl", downloadUri);
                                            Toast.makeText(v.getContext(), "Image Updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            ProfilePic.setImageResource(R.drawable.ic_camera);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(v.getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
                        }

                        if (passwordLayout.getVisibility() == v.VISIBLE) {
                            if (!TextUtils.isEmpty(oldPassword.getText().toString()) &&
                                    !TextUtils.isEmpty(newPassword.getText().toString()) &&
                                    !TextUtils.isEmpty(newConfirmPassword.getText().toString())) {

                                if (newPassword.getText().toString().equals(newConfirmPassword.getText().toString())) {

                                    email = mUser.getEmail();
                                    AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword.getText().toString());
                                    mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Snackbar.make(passwordLayout, "Your password must consist of a minimum of 6 characters.", Snackbar.LENGTH_LONG).show();
                                                        } else {
                                                            oldPassword.setText("");
                                                            newPassword.setText("");
                                                            newConfirmPassword.setText("");
                                                            Snackbar.make(passwordLayout, "Password Successfully Modified", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(v.getContext(), "The old password is incorrect.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar.make(passwordLayout, "New Password do not equals Confirm Password.", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                if (TextUtils.isEmpty(oldPassword.getText().toString())) {
                                    Snackbar.make(passwordLayout, "Old Password cannot empty.", Snackbar.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(newPassword.getText().toString())) {
                                    Snackbar.make(passwordLayout, "New Password cannot empty.", Snackbar.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(newConfirmPassword.getText().toString())) {
                                    Snackbar.make(passwordLayout, "Confirm Password cannot empty.", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                        if (usernameLayout.getVisibility() == v.VISIBLE) {
                            if (!TextUtils.isEmpty(password.getText().toString()) && !TextUtils.isEmpty(newUsername.getText().toString())) {

                                String newemail = mUser.getEmail();
                                System.out.println(newemail);
                                AuthCredential credential = EmailAuthProvider.getCredential(newemail, password.getText().toString());
                                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mFirestore.collection("User").document(mUser.getUid())
                                                    .update("username", newUsername.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Snackbar.make(passwordLayout, "Username Successfully Modified", Snackbar.LENGTH_SHORT).show();
                                                    password.setText("");
                                                    newUsername.setText("");
                                                }
                                            });

                                        } else {
                                            Toast.makeText(v.getContext(), "Password is incorrect.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                if (TextUtils.isEmpty(newUsername.getText().toString())) {
                                    Snackbar.make(passwordLayout, "New Username cannot empty.", Snackbar.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(password.getText().toString())) {
                                    Snackbar.make(passwordLayout, "Password cannot empty.", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                        }

                    } else {
                        Snackbar.make(passwordLayout, "Please login.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            uriPicData = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getActivity().getContentResolver(), uriPicData);
                    selectedPic = ImageDecoder.decodeBitmap(source);
                } else {
                    selectedPic = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), uriPicData);
                }
                ProfilePic.setImageBitmap(selectedPic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}