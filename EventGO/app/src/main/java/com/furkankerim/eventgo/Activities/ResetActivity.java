package com.furkankerim.eventgo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.furkankerim.eventgo.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText resetEmail;
    private String txtEmail;
    private androidx.appcompat.widget.Toolbar mtoolbar;
    private TextInputLayout inputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mAuth = FirebaseAuth.getInstance();
        resetEmail = findViewById(R.id.reset_EditTextEmail);
        mtoolbar = findViewById(R.id.resetPassword_toolbar);
        inputEmail = findViewById(R.id.reset_input_isim);

        setSupportActionBar(mtoolbar);
    }

    public void resetBtn(View view) {
        txtEmail = resetEmail.getText().toString();
        if (!TextUtils.isEmpty(txtEmail)) {
            mAuth.sendPasswordResetEmail(txtEmail);
            finish();
            startActivity(new Intent(ResetActivity.this, MainActivity.class));
        }else
            inputEmail.setError("E-mail cannot be Empty!!");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}