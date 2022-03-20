package com.furkankerim.eventgo.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.furkankerim.eventgo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SignUpAndSignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextMail;
    private String txtEmail;
    private TextInputLayout inputEmail;
    private androidx.appcompat.widget.Toolbar mtoolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_and_sign_in);
        mAuth = FirebaseAuth.getInstance();
        editTextMail = findViewById(R.id.EditTextEmail);
        mtoolbar = findViewById(R.id.sign_in_and_up_toolbar);
        inputEmail = findViewById(R.id.sign_in_and_up_input_email);

        setSupportActionBar(mtoolbar);

    }

    public void nextBtn(View view) {
        txtEmail = editTextMail.getText().toString();
        System.out.println(txtEmail);

        if (!TextUtils.isEmpty(txtEmail)) {
            mAuth.fetchSignInMethodsForEmail(txtEmail)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean isNewUser;
                            if (task.isSuccessful()) {
                                isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if (isNewUser) {
                                    //Email veritabanında yok ise
                                    Intent signUpActivity = new Intent(SignUpAndSignInActivity.this, SignUpActivity.class);
                                    signUpActivity.putExtra("email", txtEmail);

                                    startActivity(signUpActivity);

                                } else {
                                    //Email veritabanında Var ise
                                    Intent signInActivity = new Intent(SignUpAndSignInActivity.this, SignInActivity.class);
                                    signInActivity.putExtra("email", txtEmail);

                                    startActivity(signInActivity);
                                }
                            }
                        }
                    });
        }else
            inputEmail.setError("E-mail cannot be Empty!!");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
