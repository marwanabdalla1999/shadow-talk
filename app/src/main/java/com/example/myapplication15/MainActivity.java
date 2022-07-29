package com.example.myapplication15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    viewpageradpt viewpageradpt;
    ViewPager viewPager;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.viewPager2);
        tabLayout=findViewById(R.id.tabLayout);
        viewpageradpt=new viewpageradpt(getSupportFragmentManager());
        viewpageradpt.addfragment(new BlankFragment2(),"Chats");
        viewpageradpt.addfragment(new BlankFragment(),"Matching");
        viewpageradpt.addfragment(new profile(),"Profile");
        viewPager.setAdapter(viewpageradpt);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_message_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_group_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_person_24);



    }




}