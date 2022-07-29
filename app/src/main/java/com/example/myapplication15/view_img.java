package com.example.myapplication15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

public class view_img extends AppCompatActivity {
ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_img);
        imageView=findViewById(R.id.shadowimg);
         Slidr.attach(this, new SlidrConfig.Builder().position(SlidrPosition.TOP).build());
        //imageView.setImageURI(Uri.parse(getIntent().getStringExtra("pic")));
        Glide.with(this).load(Uri.parse(getIntent().getStringExtra("pic"))).placeholder(R.drawable.profile).into(imageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Glide.with(this).load(Uri.parse(getIntent().getStringExtra("pic"))).into(imageView);
    }
}