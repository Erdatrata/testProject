package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private  static final int SPLASH_SCREEN=2000;
  Animation topAnim,bottomAnim;
  ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e){}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.buttom_anim);
        image=findViewById(R.id.imageView);
        image.setAnimation(topAnim);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);

    }
}