package com.example.tazpitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_about);

        int[] abouts = {
                R.id.about_eli_linkedIn, R.id.about_eli_github,
                R.id.about_evy_linkedIn, R.id.about_evy_github,
                R.id.about_gab_linkedIn, R.id.about_gab_github,
                R.id.about_rat_linkedIn, R.id.about_rat_github,
                R.id.about_nat_linkedIn, R.id.about_nat_github
        };
        String[] URLs = {
                "https://www.linkedin.com/in/eliezer-revach-81410a208/","https://github.com/eliezerRevach",
                "https://www.linkedin.com/in/evyatar-golan-300493195/","https://github.com/EvjaG",
                "https://www.linkedin.com/in/gabigutkin/","https://github.com/gabigut27",
                "https://www.linkedin.com/in/erdat-rata-ab92021b5/","https://github.com/Erdatrata",
                "https://www.linkedin.com/in/netanel-hazi-787386177/","https://github.com/NatiHazi"
        };

        for(int i=0;i<abouts.length;i++){
            int finalI = i;
            findViewById(abouts[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLs[finalI]));
                    startActivity(browserIntent);
                }
            });
        }

    }
}