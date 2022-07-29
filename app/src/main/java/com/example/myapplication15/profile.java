package com.example.myapplication15;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;

public class profile extends Fragment {
HashMap<String, String> user_data;
    data_save userdata;
ImageView profileimg;
TextView edit;
TextView name;
TextView nickname;
TextView phonenumber;
TextView birthdate;
TextView gender;
TextView userphonenumber;
    FirebaseFirestore db;
    int myear,mMonth,mday;
    LinearLayout editgender;
    RadioGroup newgender;
    String newgenderstr="";
    TextView savegender,cancelgender;
    ImageView changeprofilepic;
    StorageReference storageRef;
    TextView bio,biotxt;
    EditText editbio;
    LinearLayout editbiolayout;
    TextView okbio,cancelbio;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView audioplayer;
    ConstraintLayout background;
    String nicknamestr;
    ImageView logout;
    Boolean logoutbool;
    TextView shadows;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int numberofshadows;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        storageRef = FirebaseStorage.getInstance().getReference();
        editgender=view.findViewById(R.id.editgender);
        savegender=view.findViewById(R.id.savegender);
        cancelgender=view.findViewById(R.id.cancelgender);
        newgender=view.findViewById(R.id.genders);
        data_save.getinstance(getContext());
        user_data=userdata.getuserdata(getContext());
        userphonenumber=view.findViewById(R.id.userphonenumber);
        profileimg=view.findViewById(R.id.profileimg2);
        name=view.findViewById(R.id.username);
        phonenumber=view.findViewById(R.id.userphonenumber);
        birthdate=view.findViewById(R.id.birthdate);
        gender=view.findViewById(R.id.usergender);
        changeprofilepic=view.findViewById(R.id.changeprofilepic);
        bio=view.findViewById(R.id.bio);
        biotxt=view.findViewById(R.id.biotxt);
        editbiolayout=view.findViewById(R.id.editbiolayout);
        editbio=view.findViewById(R.id.editbio);
        okbio=view.findViewById(R.id.okbio);
        cancelbio=view.findViewById(R.id.cancelbio);
        swipeRefreshLayout=view.findViewById(R.id.profileswip);
        //audioplayer=view.findViewById(R.id.audioplayer);
        background=view.findViewById(R.id.linearLayout);
        edit=view.findViewById(R.id.edit);
        nickname=view.findViewById(R.id.nickname);
        logout=view.findViewById(R.id.logout);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        shadows=view.findViewById(R.id.numberofshadows);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    storageRef.child("images").child(userphonenumber.getText().toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Glide.with(getContext()).load(task.getResult()).circleCrop().into(profileimg);
                            data_save.getinstance(getContext());
                            data_save.registeruser(getContext(),name.getText().toString(),userphonenumber.getText().toString(),
                                    gender.getText().toString(),task.getResult().toString(),birthdate.getText().toString(),biotxt.getText().toString(),nickname.getText().toString());
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    });


                    }
                catch (Exception e){}
            }
        });
        showprofileimg();
        changeprofilepic();
        audioplayer();
        db = FirebaseFirestore.getInstance();
        changenickname();
        getuserdata();
         editdata();
         logout();
        return view;
    }

    private void logout() {
         logoutbool=false;
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_save.logout();
                Intent intent=new Intent(getContext(),startactivity.class);
                startActivity(intent);
                getActivity().finish();
                deleteCache(getContext());
                clearAppData();
             // Toast t = Toast.makeText(getContext(), "Click one more time to logout !", Toast.LENGTH_LONG);
             // t.show();
                //      logoutbool=true;

            //    if (t.getView().isShown()){
             //       Toast.makeText(getContext(), "logoottttt !", Toast.LENGTH_SHORT).show();
              /*  data_save.logout();
                Intent intent=new Intent(getContext(),startactivity.class);
                startActivity(intent);
                getActivity().finish();*/
         //       }

            }
        });
    }

    private void changenickname() {
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 nicknamestr="";
                Dialog editnickname=new Dialog(getContext(),R.style.mydialog);
                editnickname.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
               // editnickname.getWindow().setBackgroundDrawable(new ColorDrawable(0x4A0C0C0C));
                editnickname.setContentView(R.layout.nickname);
                EditText nickname=editnickname.findViewById(R.id.nicknameedit);
                TextView nickname1=editnickname.findViewById(R.id.fristnickname);
                TextView nickname2=editnickname.findViewById(R.id.secondnickname);
                TextView nickname3=editnickname.findViewById(R.id.thirdnickname);
                TextView nickname4=editnickname.findViewById(R.id.fourthnickname);
                TextView nickname5=editnickname.findViewById(R.id.fifthnickname);
                TextView nickname6=editnickname.findViewById(R.id.sixthnickname);
                TextView save=editnickname.findViewById(R.id.save);
                TextView exit=editnickname.findViewById(R.id.exit);
                nickname1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if (nickname1.getBackground().getConstantState()==getResources().getDrawable(R.drawable.border).getConstantState()){
                        nickname1.setBackgroundColor(Color.TRANSPARENT);
                        nicknamestr="";

                    }
                    else {
                        nickname2.setBackgroundColor(Color.TRANSPARENT);
                        nickname3.setBackgroundColor(Color.TRANSPARENT);
                        nickname4.setBackgroundColor(Color.TRANSPARENT);
                        nickname5.setBackgroundColor(Color.TRANSPARENT);
                        nickname6.setBackgroundColor(Color.TRANSPARENT);
                        nickname1.setBackground(getResources().getDrawable(R.drawable.border));
                        nicknamestr=nickname1.getText().toString();
                    }
                    }
                });
                nickname2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nickname2.getBackground().getConstantState()==getResources().getDrawable(R.drawable.border).getConstantState()){
                            nickname2.setBackgroundColor(Color.TRANSPARENT);
                            nicknamestr="";

                        }
                        else {
                            nickname1.setBackgroundColor(Color.TRANSPARENT);
                            nickname3.setBackgroundColor(Color.TRANSPARENT);
                            nickname4.setBackgroundColor(Color.TRANSPARENT);
                            nickname5.setBackgroundColor(Color.TRANSPARENT);
                            nickname6.setBackgroundColor(Color.TRANSPARENT);
                            nickname2.setBackground(getResources().getDrawable(R.drawable.border));
                            nicknamestr=nickname2.getText().toString();
                        }
                    }
                });
                nickname3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nickname3.getBackground().getConstantState()==getResources().getDrawable(R.drawable.border).getConstantState()){
                            nickname3.setBackgroundColor(Color.TRANSPARENT);
                            nicknamestr="";

                        }
                        else {
                            nickname2.setBackgroundColor(Color.TRANSPARENT);
                            nickname1.setBackgroundColor(Color.TRANSPARENT);
                            nickname4.setBackgroundColor(Color.TRANSPARENT);
                            nickname5.setBackgroundColor(Color.TRANSPARENT);
                            nickname6.setBackgroundColor(Color.TRANSPARENT);
                            nickname3.setBackground(getResources().getDrawable(R.drawable.border));
                            nicknamestr=nickname3.getText().toString();
                        }
                    }
                });
                nickname4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nickname4.getBackground().getConstantState()==getResources().getDrawable(R.drawable.border).getConstantState()){
                            nickname4.setBackgroundColor(Color.TRANSPARENT);
                            nicknamestr="";

                        }
                        else {
                            nickname2.setBackgroundColor(Color.TRANSPARENT);
                            nickname3.setBackgroundColor(Color.TRANSPARENT);
                            nickname1.setBackgroundColor(Color.TRANSPARENT);
                            nickname5.setBackgroundColor(Color.TRANSPARENT);
                            nickname6.setBackgroundColor(Color.TRANSPARENT);
                            nickname4.setBackground(getResources().getDrawable(R.drawable.border));
                            nicknamestr=nickname4.getText().toString();
                        }
                    }
                });
                nickname5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nickname5.getBackground().getConstantState()==getResources().getDrawable(R.drawable.border).getConstantState()){
                            nickname5.setBackgroundColor(Color.TRANSPARENT);
                            nicknamestr="";

                        }
                        else {
                            nickname2.setBackgroundColor(Color.TRANSPARENT);
                            nickname3.setBackgroundColor(Color.TRANSPARENT);
                            nickname4.setBackgroundColor(Color.TRANSPARENT);
                            nickname1.setBackgroundColor(Color.TRANSPARENT);
                            nickname6.setBackgroundColor(Color.TRANSPARENT);
                            nickname5.setBackground(getResources().getDrawable(R.drawable.border));
                            nicknamestr=nickname5.getText().toString();
                        }
                    }
                });
                nickname6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nickname6.getBackground().getConstantState()==getResources().getDrawable(R.drawable.border).getConstantState()){
                            nickname6.setBackgroundColor(Color.TRANSPARENT);
                            nicknamestr="";

                        }
                        else {
                            nickname2.setBackgroundColor(Color.TRANSPARENT);
                            nickname3.setBackgroundColor(Color.TRANSPARENT);
                            nickname4.setBackgroundColor(Color.TRANSPARENT);
                            nickname5.setBackgroundColor(Color.TRANSPARENT);
                            nickname1.setBackgroundColor(Color.TRANSPARENT);
                            nickname6.setBackground(getResources().getDrawable(R.drawable.border));
                            nicknamestr=nickname6.getText().toString();
                        }
                    }
                });
             //   ((InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
               // editnickname.setCanceledOnTouchOutside(false);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!nickname.getText().toString().equals("") && !nicknamestr.equals("")){
                            Toast.makeText(getContext(), "please don't choose nickname or write custom one don't do both of them", Toast.LENGTH_SHORT).show();

                        }
                        else if(!nickname.getText().toString().equals("")){

                            updatenickname(nickname.getText().toString());
                            editnickname.cancel();
                        }
                        else if(!nicknamestr.equals("")){

                            updatenickname(nicknamestr);
                            editnickname.cancel();
                        }
                        else {
                            Toast.makeText(getContext(), "please  choose nickname or write custom one", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editnickname.cancel();

                    }
                });
                editnickname.show();
            }
        });
    }

    private void updatenickname(String nicknamestr) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(userphonenumber.getText().toString())){
                                    db.collection("users").document(document.getId()).update("nickname",nicknamestr);
                                    data_save.getinstance(getContext());
                                    data_save.registeruser(getContext(),name.getText().toString(),userphonenumber.getText().toString(),
                                            gender.getText().toString(),user_data.get("Uri_pic"),birthdate.getText().toString(),biotxt.getText().toString(),nicknamestr);
                                    getuserdata();


                                }
                            }
                        }}});

    }

    private void audioplayer() {
       /* audioplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                intent_upload = Intent.createChooser(intent_upload, "Select audio or music");
                startActivityForResult(intent_upload,1);

            }
        });*/


    }

   public void playaudio(Intent data){
Uri uri=data.getData();
       MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
       metaRetriver.setDataSource(getContext(),uri);
       try {


           MediaPlayer mMediaPlayer = MediaPlayer.create(getContext(), uri);
           mMediaPlayer.start();
          
         //  album_art.setImageBitmap(songImage);

       } catch (Exception e) {

       }
    }


    private void showprofileimg() {
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),image_viewer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(),profileimg, ViewCompat.getTransitionName(profileimg));
                intent.putExtra("pic",user_data.get("Uri_pic"));
                getContext().startActivity(intent,optionsCompat.toBundle());
            }
        });
    }

    private void refreshphoto() {
        try {
            storageRef.child("images").child(userphonenumber.getText().toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(getContext()).load(task.getResult()).circleCrop().into(profileimg);
                    data_save.getinstance(getContext());
                    data_save.registeruser(getContext(),name.getText().toString(),userphonenumber.getText().toString(),
                            gender.getText().toString(),task.getResult().toString(),birthdate.getText().toString(),biotxt.getText().toString(),nickname.getText().toString());

                }
            });


        }
        catch (Exception e){}
    }

    private void changeprofilepic() {
        changeprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 101);
            }
        });
    }

    private void editdata() {
        final Calendar c = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener Date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH)+1;
                mday = c.get(Calendar.DAY_OF_MONTH);
                updatedate(mday+"/"+mMonth+"/"+myear+"");}};
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog editname=new Dialog(getContext(),R.style.WideDialog);
                editname.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                editname.getWindow().setBackgroundDrawable(new ColorDrawable(0x4A0C0C0C));
                editname.setContentView(R.layout.nameedit);
                EditText name=editname.findViewById(R.id.editname);
                ((InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                editname.setCanceledOnTouchOutside(false);
                TextView ok,cancel;
                ok=editname.findViewById(R.id.ok);
                cancel=editname.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editname.cancel();
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatename(name.getText().toString());
                        editname.cancel();

                    }
                });
                name.requestFocus();
                editname.show();
            }
        });
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), Date, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editgender.setVisibility(View.VISIBLE);
                newgender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId){
                            case R.id.femalegender:
                                newgenderstr="female";
                                break;

                            case R.id.malegender:
                                newgenderstr="male";
                                break;
                        }
                    }
                });
                savegender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updategender(newgenderstr);
                        editgender.setVisibility(View.GONE);
                    }
                });
                cancelgender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editgender.setVisibility(View.GONE);
                    }
                });

            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biotxt.setVisibility(View.GONE);
                editbiolayout.setVisibility(View.VISIBLE);
                cancelbio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editbiolayout.setVisibility(View.GONE);
                        biotxt.setVisibility(View.VISIBLE);
                    }
                });
                okbio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editbio.getText().toString().isEmpty()){
                            editbio.setError("please fill your bio");
                            editbio.requestFocus();
                        }
                        else{
                            editbiolayout.setVisibility(View.GONE);
                            biotxt.setVisibility(View.VISIBLE);
                            updatebio(editbio.getText().toString());
                        }
                    }
                });
            }
        });
    }

    private void updatebio(String bio) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(userphonenumber.getText().toString())){
                                    db.collection("users").document(document.getId()).update("bio",bio);
                                    data_save.getinstance(getContext());
                                    data_save.registeruser(getContext(),name.getText().toString(),userphonenumber.getText().toString(),
                                            gender.getText().toString(),user_data.get("Uri_pic"),birthdate.getText().toString(),bio,nickname.getText().toString());
                                    getuserdata();


                                }
                            }
                        }}});
    }

    private void updatename(String name) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(userphonenumber.getText().toString())){
                                    db.collection("users").document(document.getId()).update("name",name);
                                    data_save.getinstance(getContext());
                                    data_save.registeruser(getContext(),name,userphonenumber.getText().toString(),
                                            gender.getText().toString(),user_data.get("Uri_pic"),birthdate.getText().toString(),biotxt.getText().toString(),nickname.getText().toString());
                                    getuserdata();


                                }
                            }
                        }}});

    }

    private void getuserdata() {
        user_data=userdata.getuserdata(getContext());
        try {

            Glide.with(getContext()).load(Uri.parse(user_data.get("Uri_pic"))).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        refreshphoto();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).circleCrop().into(profileimg);

        }
        catch (Exception e){

        }
        phonenumber.setText(user_data.get("phone"));
        name.setText(user_data.get("name"));
        birthdate.setText(user_data.get("birthdate"));
        gender.setText(user_data.get("gender"));
        biotxt.setText(user_data.get("bio"));
        editbio.setText(user_data.get("bio"));
        nickname.setText(user_data.get("nickname"));
        databaseReference.child("shadow_state").child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    numberofshadows = 0;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (snap.getValue().equals("true")) {
                            numberofshadows++;
                        }
                    }
                    shadows.setText(Integer.toString(numberofshadows));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void updatedate(String date) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(userphonenumber.getText().toString())){
                                    db.collection("users").document(document.getId()).update("birth_date",date);
                                    data_save.getinstance(getContext());
                                    data_save.registeruser(getContext(),name.getText().toString(),userphonenumber.getText().toString(),
                                            gender.getText().toString(),user_data.get("Uri_pic"),date,biotxt.getText().toString(),nickname.getText().toString());
                                    getuserdata();


                                }
                            }
                        }}});
    }
    private void updategender(String gender) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(userphonenumber.getText().toString())){
                                    db.collection("users").document(document.getId()).update("gender",gender);
                                    data_save.getinstance(getContext());
                                    data_save.registeruser(getContext(),name.getText().toString(),userphonenumber.getText().toString(),
                                            gender,user_data.get("Uri_pic"),birthdate.getText().toString(),biotxt.getText().toString(),nickname.getText().toString());
                                    getuserdata();


                                }
                            }
                        }}});
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

            if (reqCode==1 && resultCode == RESULT_OK){
                playaudio(data);
            }
        if (resultCode == RESULT_OK && reqCode==101) {
            Uri imageuri= CropImage.getPickImageResultUri(getContext(),data);
            if(CropImage.isReadExternalStoragePermissionsRequired(getContext(),imageuri)){

                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},0);

            }else{
                StartCrop(imageuri);
            }

        }
        if(reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                Toast.makeText(getContext(), "Uploading photo...", Toast.LENGTH_SHORT).show();
                storageRef.child("images").child(userphonenumber.getText().toString()).putFile(result.getUri()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(getContext(), "profile picture changed successfully", Toast.LENGTH_SHORT).show();
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                db.collection("users")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                if (task1.isSuccessful()) {
                                                    Boolean found=false;
                                                    for (QueryDocumentSnapshot document : task1.getResult()) {
                                                        if(document.getString("phone_number").equals(userphonenumber.getText().toString())){
                                                            db.collection("users").document(document.getId()).update("profile_picture",task.getResult().toString());
                                                            data_save.getinstance(getContext());
                                                            data_save.registeruser(getContext(), name.getText().toString(), userphonenumber.getText().toString(),
                                                                    gender.getText().toString(), task.getResult().toString(), birthdate.getText().toString(), biotxt.getText().toString(),nickname.getText().toString());

                                                            Glide.with(getContext()).load(task.getResult()).circleCrop().into(profileimg);


                                                        }
                                                    }
                                                }}});



                            }
                        });
                    }
                });
            }catch (Exception e){}
           /* try {
                Glide.with(getContext()).load(result.getUri()).circleCrop().into(profileimg);
             //   uri=result.getUri();
            }catch (Exception e){

            }*/

        }
        if(reqCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            Toast.makeText(getContext(), "error in pick", Toast.LENGTH_SHORT).show();
        }

    }
    private void StartCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true).setMinCropWindowSize(1000,1000).start(getContext(),this);
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }}

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)getActivity().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getActivity().getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}