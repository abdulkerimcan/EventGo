package com.furkankerim.eventgo.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.furkankerim.eventgo.R;

public class SplashActivity extends AppCompatActivity {
    Animation mAnim,anim1,anim2;
    ImageView img,resim1,resim2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img = findViewById(R.id.splash_logo);
        mAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);
        anim1 = AnimationUtils.loadAnimation(this,R.anim.down_left);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.up_right);

        resim1 = findViewById(R.id.resim1);
        resim2 = findViewById(R.id.resim2);


        resim1.startAnimation(anim1);
        img.startAnimation(mAnim);
        resim2.startAnimation(anim2);



        new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                finish();
                startActivity(mainIntent);
            }
        }.start();
    }
}