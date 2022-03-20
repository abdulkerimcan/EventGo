package com.furkankerim.eventgo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextView tvEmail;
    private EditText etPassword;
    private String txtPassword,txtEmail;
    private Intent intent;
    private FirebaseAuth mAuth;
    private TextInputLayout inputLayout;
    private androidx.appcompat.widget.Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        etPassword = findViewById(R.id.signIn_EditSifre);
        tvEmail = findViewById(R.id.textViewEmail);
        mtoolbar = findViewById(R.id.signIn_toolbar);
        inputLayout = findViewById(R.id.sign_in_input_sifre);
        setSupportActionBar(mtoolbar);


        intent = getIntent();
        txtEmail = intent.getStringExtra("email");
        tvEmail.setText(txtEmail);

    }

    public void btnSignIn(View view) {
        txtPassword = etPassword.getText().toString();
        if (!TextUtils.isEmpty(txtEmail)) {
            if (!TextUtils.isEmpty(txtPassword)) {
                mAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else
                inputLayout.setError("Password cannot be empty");
        }

    }
    public void btnForgotPassword(View view) {
        startActivity(new Intent(SignInActivity.this,ResetActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}