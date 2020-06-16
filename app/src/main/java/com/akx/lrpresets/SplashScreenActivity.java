package com.akx.lrpresets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final ImageView imgSplash=findViewById(R.id.imgSplash);
        Animation animation= AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        imgSplash.startAnimation(animation);

        CountDownTimer countDownTimer=new CountDownTimer(800,500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent=new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);
                Animatoo.animateFade(SplashScreenActivity.this);
                finish();
            }
        }.start();
    }
}
