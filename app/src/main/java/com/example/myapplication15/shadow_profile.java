package com.example.myapplication15;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.CollapsibleActionView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;
import com.r0adkll.slidr.widget.SliderPanel;

import java.util.HashMap;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

public class shadow_profile extends AppCompatActivity {
    FirebaseFirestore db;
    TextView name,gender,bio,birthdate;
    ImageView shadowpic,back;
    ViewSkeletonScreen skeletonScreen;
  //  SwipeRefreshLayout swipeRefreshLayout;
    //private SlidrInterface slidr;
    ImageView options;
    PopupMenu popup;
    data_save data_save;
    HashMap<String, String> user_data;
    FirebaseDatabase database;
    DatabaseReference myRef;
    database1 databaseoffline;
    database databaseoffline_chatheads;
    androidx.appcompat.widget.Toolbar toolbar;
    TextView number_of_shadows;
    int numberofshadows;
    TextView shdownickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_profile);


        db = FirebaseFirestore.getInstance();
        init();

        getuser();
    }

    private void init() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        data_save.getinstance(this);
        user_data=data_save.getuserdata(this);
        databaseoffline= com.example.myapplication15.database1.getinsance(this);
        databaseoffline_chatheads=com.example.myapplication15.database.getinsance(this);
      //  slidr = Slidr.attach(this, new SlidrConfig.Builder().position(SlidrPosition.TOP).build());
        shdownickname=findViewById(R.id.shadownickname);
        options=findViewById(R.id.options);
        gender=findViewById(R.id.shadowgender);
        bio=findViewById(R.id.shadowbio);
        birthdate=findViewById(R.id.shadowbirthdate);
        shadowpic=findViewById(R.id.shadowpic);
        back=findViewById(R.id.back);
        toolbar=findViewById(R.id.toolbar);
        number_of_shadows=findViewById(R.id.number_of_shadows);


        //  swipeRefreshLayout=findViewById(R.id.shadowswip);
      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getuser();
            }
        });*/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        supportPostponeEnterTransition();
        Glide.with(shadow_profile.this).load(getIntent().getStringExtra("pic")).placeholder(R.drawable.pic).fitCenter().listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                supportStartPostponedEnterTransition();
                return false;
            }
        }).into(shadowpic);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup = new PopupMenu(shadow_profile.this, options);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.chat_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.show:
                                showme();
                                return true;
                            case R.id.hide:
                                hideme();
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popup.show();
            }
        });

        myRef.child("shadow_state").child(getIntent().getStringExtra("phonenumber")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    numberofshadows = 0;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.getValue().equals("true")) {
                            numberofshadows++;
                        }
                    }
                    number_of_shadows.setText(Integer.toString(numberofshadows));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showme() {
        myRef.child("shadow_state").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).setValue("true");

    }
    private void hideme() {
        myRef.child("blocklist").child(user_data.get("phone")).push().setValue(getIntent().getStringExtra("phonenumber"));
        myRef.child("blocklist").child(getIntent().getStringExtra("phonenumber")).push().setValue(user_data.get("phone"));
        try {

            myRef.child("messages").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).removeValue();
            myRef.child("messages").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).removeValue();

            databaseoffline.getmassages().delete(getIntent().getStringExtra("phonenumber")).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                }
            });
            databaseoffline.getmassages().delete(user_data.get("phone")).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                }
            });
            databaseoffline_chatheads.chatsduo().delete_using_phone(getIntent().getStringExtra("phonenumber")).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                }
            });


        }catch (Exception e){

        }
        finish();
    }

    private void getuser() {
            findViewById(R.id.loadingPanel10).setVisibility(View.VISIBLE);
        findViewById(R.id.linearLayout3).setVisibility(View.GONE);
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(getIntent().getStringExtra("phonenumber"))){
                                    toolbar.setTitle(document.getString("name"));
                                    toolbar.setSubtitle("120");
                                    toolbar.setCollapseContentDescription("120");
                                    bio.setText(document.getString("bio"));
                                    birthdate.setText(document.getString("birth_date"));
                                    gender.setText(document.getString("gender"));
                                    shdownickname.setText(document.getString("nickname"));

                                    //skeletonScreen.hide();
                                    findViewById(R.id.loadingPanel10).setVisibility(View.GONE);
                                    findViewById(R.id.linearLayout3).setVisibility(View.VISIBLE);
                                   // swipeRefreshLayout.setRefreshing(false);


                                }
                            }
                        }}});
    }



}