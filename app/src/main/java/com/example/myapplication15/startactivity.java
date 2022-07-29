package com.example.myapplication15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class startactivity extends AppCompatActivity {
    RelativeLayout bg;
    private static startactivity instance=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startactivity);
        bg=findViewById(R.id.bg);
       // bg.getBackground().setAlpha(50);
        instance=this;
addfragment(new phonenumber(),"phone",false);

    }

   public  void addfragment(Fragment fragment,String name,Boolean addtobackstack){
       FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
       ft.setCustomAnimations(R.anim.slide_in_right,
               R.anim.slide_out_left, R.anim.slide_in_left,
               R.anim.slide_out_right);
       ft.replace(R.id.framelayout,fragment);
       if(addtobackstack){
ft.addToBackStack(name);}

       ft.commit();
   }
    public static startactivity getInstance() {
        return instance;
    }
}