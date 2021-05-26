package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {
  Animation topAnim,bottomAnim;
  ImageView image;
  TextView logo,slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.buttom_anim);
//rata
        image=findViewById(R.id.imageView);
        logo=findViewById(R.id.logo_name);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
     //   intent intent =new intent( packageContext:SignUp.this,LoginActivity);
//        slogan.setAnimation(bottomAnim);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SignUp.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }),

    }
}