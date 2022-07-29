
package com.example.myapplication15;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class chatactivity extends AppCompatActivity  {
ImageView sendmassage;
EditText massage;
    FirebaseDatabase database;
    DatabaseReference myRef;
    data_save data_save;
    HashMap<String, String> user_data;
    RecyclerView massagesrcv;
    recycleviewadpt arrayAdapter;
    ImageView backbtn;
    ImageView userphoto;
    TextView username;
    FirebaseFirestore db;
    TextView typing;
    boolean isTyping = false;
    Runnable timerRunnable;
    Handler timerHandler;
    Runnable timerRunnable1;
    Handler timerHandler1;
    ImageView options;
    PopupMenu popup;
    ImageView recorderbtn;
    StorageReference store;
    int seconds=0,minutes=0;
    TextView txttime;
    LinearLayout recordinglayout;
    ImageView removerecord;
    TextView cancelrecording;
    CardView recoderlayout;
    database databaseoffline_chatheads;
    database1 databaseoffline;
    List<massagemodel> massagemodels;
    ValueEventListener seen_ref;
    ImageView photolibarary;
    Parcelable recyclerViewState;
    LinearLayout block,nonblocked;
    float x1=0;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE__PERMISSION = 211;
    private MediaRecorder recorder;
    private String fileName=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactivity);
        sendmassage=findViewById(R.id.sendmassage);
        massage=findViewById(R.id.massage);
        backbtn=findViewById(R.id.backbtn);
        userphoto=findViewById(R.id.userphoto);
        username=findViewById(R.id.nameofshadow);
        backbtn.setOnClickListener(i-> onBackPressed());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        db = FirebaseFirestore.getInstance();
        typing=findViewById(R.id.typing);
        options=findViewById(R.id.options);
        recorderbtn=findViewById(R.id.voice_recorder);
        txttime=findViewById(R.id.recordtimer);
        removerecord=findViewById(R.id.removerecord);
        databaseoffline= com.example.myapplication15.database1.getinsance(this);
        databaseoffline_chatheads=com.example.myapplication15.database.getinsance(this);
        recordinglayout=findViewById(R.id.itemsofrecording);
        store= FirebaseStorage.getInstance().getReference();
        cancelrecording=findViewById(R.id.cancelrecording);
        cancelrecording.setOnClickListener(i-> stopRecording());
        recoderlayout=findViewById(R.id.recoderlayout);
        photolibarary=findViewById(R.id.choosephoto);
        block=findViewById(R.id.block);
        nonblocked=findViewById(R.id.nonblocked);
        data_save.getinstance(this);
        user_data=data_save.getuserdata(this);
        seenmassages();
        recorder();
        getshadowprofile();
        getphoto();
        getstate();
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 popup = new PopupMenu(chatactivity.this, options);
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

        massagesrcv=findViewById(R.id.massages);
        ArrayList<massagemodel> massages=new ArrayList<>();


        setupofflinemassages();


        getistyping();
        massage.addTextChangedListener(new TextWatcher() {
            Boolean istyping=false;
            Boolean timerrun=false;
            String text="";
            String temptext="";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myRef.child("istyping").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).setValue("typing");
                if(s.toString().isEmpty()){
                    sendmassage.setVisibility(View.GONE);
                    recorderbtn.setVisibility(View.VISIBLE);
                }
                else{
                    sendmassage.setVisibility(View.VISIBLE);
                    recorderbtn.setVisibility(View.GONE);

                }
                text=s.toString();

                timerHandler = new Handler();
               if (!timerrun){
                timerRunnable = new Runnable() {
                    @Override
                    public void run() {

                        if(temptext.equals(text)){
                            myRef.child("istyping").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).setValue("stop");

                        }
                        else{
                            temptext=text;
                        }

                        timerHandler.postDelayed(this, 1000);


                    }
                };
                timerRunnable.run();
               timerrun=true;
               }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        sendmassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(massage.getText().toString().isEmpty()){

                }
                else{
                    SimpleDateFormat formatter = new SimpleDateFormat("MMMMdd, yyyy/HH:mm", Locale.getDefault());
                    Date date = new Date();
                    String currenttime=formatter.format(date);
                    myRef.child("messages").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).push().child(user_data.get("phone")).setValue(massage.getText().toString()+"%%%12345timeStamp54321%%%"+currenttime);
                    myRef.child("messages").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).push().child(user_data.get("phone")).setValue(massage.getText().toString()+"%%%12345timeStamp54321%%%"+currenttime);
                    myRef.child("messagesUnseen").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).push().setValue("unseen");
                    String massagestr=massage.getText().toString();
                    massage.setText("");
                    MediaPlayer mediaPlayer=MediaPlayer.create(chatactivity.this,R.raw.sendmassage);
                    mediaPlayer.start();
                    FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            String token=s;

                            send(token,massagestr,username.getText().toString());
                        }
                    });

                }
            }
        });
setupchatinfo();
    }
    public  String send(String to,  String body,String title) {

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    final String apiKey = "AAAArp0o8OM:APA91bEXjMDYxSwMjJ2on1wo4Ip_s2U702Ak00I5MKUzTGEjNBpTulVsRxzi5CPutvGgU9Ky0YLi7aQa-0QkAZriZmzqSPdiq5MoEHWcjrJoUbTkb_tDXQ-RMgIoIeuXO6llZsAVt98p";
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "key=" + apiKey);
                    conn.setDoOutput(true);
                    JSONObject message = new JSONObject();
                    message.put("to", to);
                    message.put("priority", "high");

                    JSONObject notification = new JSONObject();
                     notification.put("title", title);
                    notification.put("body", body);
                    notification.put("img",user_data.get("profile_picture"));
                    message.put("data", notification);
                    OutputStream os = conn.getOutputStream();
                    os.write(message.toString().getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println("\nSending 'POST' request to URL : " + url);
                    System.out.println("Post parameters : " + message.toString());
                    System.out.println("Response Code : " + responseCode);
                    System.out.println("Response Code : " + conn.getResponseMessage());

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // print result
                    System.out.println(response.toString());
                    return response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "error";


            }
        };
Object s=asyncTask.execute();

return s.toString();
    }
    private void getstate() {
        myRef.child("blocklist").child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean retval=false;
                    for (DataSnapshot snap : snapshot.getChildren()) {
            if (snap.getValue().equals(getIntent().getStringExtra("phonenumber"))){

                        retval=true;
            }
                    }
                    if (retval){
                        block.setVisibility(View.VISIBLE);
                        nonblocked.setVisibility(View.GONE);
                    }
                    else {
                        block.setVisibility(View.GONE);
                        nonblocked.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getphoto() {
        photolibarary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 105);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==105) {
            Uri imageuri= CropImage.getPickImageResultUri(chatactivity.this,data);
            if(CropImage.isReadExternalStoragePermissionsRequired(chatactivity.this,imageuri)){

                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},0);

            }else{
                StartCrop(imageuri);
            }


        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                store.child("photos").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).child(UUID.randomUUID().toString()).putFile(result.getUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                SimpleDateFormat formatter = new SimpleDateFormat("MMMMdd, yyyy/HH:mm", Locale.getDefault());
                                Date date = new Date();
                                String currenttime=formatter.format(date);
                                myRef.child("messages").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).push().child(user_data.get("phone")).setValue("%%%12345photo54321%%%"+uri.toString()+"%%%12345timeStamp54321%%%"+currenttime);
                                myRef.child("messages").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).push().child(user_data.get("phone")).setValue("%%%12345photo54321%%%"+uri.toString()+"%%%12345timeStamp54321%%%"+currenttime);
                                myRef.child("messagesUnseen").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).push().setValue("unseen");
                                MediaPlayer mediaPlayer=MediaPlayer.create(chatactivity.this,R.raw.sendmassage);
                                mediaPlayer.start();
                            }
                        });

                    }


                });
            }catch (Exception e){}


        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            Toast.makeText(this, "error in pick", Toast.LENGTH_SHORT).show();
        }
    }

        private void StartCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(false).setCropShape(CropImageView.CropShape.RECTANGLE).setInitialCropWindowPaddingRatio(0).setInitialCropWindowRectangle(new Rect()).start(this);


        }

    private void getshadowprofile() {

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setEnabled(false);
                Intent intent=new Intent(chatactivity.this,shadow_profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(chatactivity.this,userphoto, ViewCompat.getTransitionName(userphoto));
                intent.putExtra("phonenumber",getIntent().getStringExtra("phonenumber"));
                intent.putExtra("pic",getIntent().getStringExtra("pic"));
                startActivity(intent,optionsCompat.toBundle());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        username.setEnabled(true);
    }

    private void seenmassages() {
         seen_ref = myRef.child("messagesUnseen").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    myRef.child("messagesUnseen").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void setupofflinemassages() {
        databaseoffline.getmassages().getchats().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<massagemodel>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull List<massagemodel> massagemodels) {
                chatactivity.this.massagemodels=new ArrayList<>();
                for (int i=0; i<massagemodels.size();i++){
                    if (massagemodels.get(i).getPhone().equals(getIntent().getStringExtra("phonenumber"))){
                        chatactivity.this.massagemodels.add(massagemodels.get(i));
                    }
                }

                arrayAdapter=new recycleviewadpt(chatactivity.this,chatactivity.this.massagemodels,getIntent().getStringExtra("phonenumber"),getIntent().getBooleanExtra("type",false),getIntent().getStringExtra("pic"));
                massagesrcv.setAdapter(arrayAdapter);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(chatactivity.this,RecyclerView.VERTICAL,false);
                linearLayoutManager.findFirstVisibleItemPosition();
                massagesrcv.setLayoutManager(linearLayoutManager);
                massagesrcv.scrollToPosition(massagesrcv.getAdapter().getItemCount() - 1);
                massagesrcv.setItemViewCacheSize(150000);
                 arrayAdapter.notifyDataSetChanged();

                setuprecyclerview();

            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void recorder() {

        recorderbtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                checkTouch(event);
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    x1=event.getX();
                    if (recorder==null) {
                        if (ContextCompat.checkSelfPermission(chatactivity.this,Manifest.permission.RECORD_AUDIO) !=PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_RECORD_AUDIO_PERMISSION);
                        }
                        else {
                            MediaPlayer mediaPlayer=MediaPlayer.create(chatactivity.this,R.raw.record);
                            mediaPlayer.start();
                        startRecording();}
                    }
                }





                return true;
            }
        });


    }

    private void getistyping() {
        myRef.child("istyping").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 try {
                     if (snapshot.getValue().equals("typing")){
                         typing.setVisibility(View.VISIBLE);

                     }
                     else{
                         typing.setVisibility(View.INVISIBLE);
                     }
                 }catch (Exception e){

                 }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timerHandler!=null){
        timerHandler.removeCallbacks(timerRunnable);
    }}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (seen_ref!=null){
            myRef.child("messagesUnseen").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).removeEventListener(seen_ref);
        }
        if (timerHandler!=null){
        timerHandler.removeCallbacks(timerRunnable);

    }
        if (timerHandler!=null){
            timerHandler.removeCallbacks(timerRunnable);

        }
        myRef.child("istyping").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).setValue("stop");
        if( arrayAdapter !=null ){
            arrayAdapter.release();
        }
    }

    private void setupchatinfo() {
        if (getIntent().getBooleanExtra("type",false)) {
            try {
                Glide.with(chatactivity.this).load(Uri.parse(getIntent().getStringExtra("pic"))).circleCrop().into(userphoto);

            } catch (Exception e) {

            }
            username.setText(getIntent().getStringExtra("name"));
        }
    }
    private void setuprecyclerview() {

                myRef.child("messages").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        Boolean retval=false;
                    for (int i=0; i<massagemodels.size();i++){
                        if (shot.getKey().equals(massagemodels.get(i).getKey())){
                            retval=true;

                        }

                    }
                    if (!retval){
                        myRef.child("messages").child((user_data.get("phone"))).child(getIntent().getStringExtra("phonenumber")).child(shot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot shot1 : snapshot.getChildren()){



                                if(shot1.getKey().equals(user_data.get("phone"))){
                                //massages.add(new massagemodel(1,shot1.getValue().toString()));

                                    databaseoffline.getmassages().insert(new massagemodel(shot.getKey(),getIntent().getStringExtra("phonenumber"),1,shot1.getValue().toString())).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
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


                                }
                                else{
                                    databaseoffline.getmassages().insert(new massagemodel(shot.getKey(),getIntent().getStringExtra("phonenumber"),2,shot1.getValue().toString())).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }

                                        @Override
                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                        }
                                    });;
                                  //  massages.add(new massagemodel(2,shot1.getValue().toString()));
                                }}
                                arrayAdapter.notifyDataSetChanged();



                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }}
                }


            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
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

    private void showme() {
        myRef.child("shadow_state").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).setValue("true");

    }



    private void startRecording() {
        recoderlayout.animate().setDuration(100).scaleX(1.1f).scaleY(1.1f);
        fileName= getExternalCacheDir().getAbsolutePath();
        fileName+="/recorded_audio.3gp";
        recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(fileName);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {

        }
        massage.setVisibility(View.GONE);
        recordinglayout.setVisibility(View.VISIBLE);
        timerHandler1 = new Handler();
        timerRunnable1 = new Runnable() {

            @Override
            public void run() {

                if (seconds >= 60) {
                    minutes = minutes + 1;
                    seconds = 0;
                }
                if (seconds < 10 && minutes < 10) {
                    txttime.setText("0" + minutes + ":0" + seconds);
                } else if (seconds < 10) {
                    txttime.setText(minutes + ":0" + seconds);
                } else if (minutes < 10) {
                    txttime.setText("0" + minutes + ":" + seconds);
                }
                seconds++;
                timerHandler1.postDelayed(this, 1000);

            }
        };
        timerRunnable1.run();



    }

    private void stopRecording() {
        recoderlayout.animate().setDuration(100).scaleX(1f).scaleY(1f);
        if (recorder!=null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }

        massage.setVisibility(View.VISIBLE);
        recordinglayout.setVisibility(View.GONE);
        if (timerHandler1!=null){
        timerHandler1.removeCallbacks(timerRunnable1);}
        txttime.setText("00:00");
        seconds=0;
        minutes=0;

    }


    private void uploadfile() {
        store.child("Audio").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).child(UUID.randomUUID().toString()).putFile(Uri.fromFile(new File(fileName))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     SimpleDateFormat formatter = new SimpleDateFormat("MMMMdd, yyyy/HH:mm", Locale.getDefault());
                     Date date = new Date();
                     String currenttime=formatter.format(date);
                     myRef.child("messages").child(user_data.get("phone")).child(getIntent().getStringExtra("phonenumber")).push().child(user_data.get("phone")).setValue("%%%12345audio54321%%%"+uri.toString()+"%%%12345timeStamp54321%%%"+currenttime);
                     myRef.child("messages").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).push().child(user_data.get("phone")).setValue("%%%12345audio54321%%%"+uri.toString()+"%%%12345timeStamp54321%%%"+currenttime);
                     myRef.child("messagesUnseen").child(getIntent().getStringExtra("phonenumber")).child(user_data.get("phone")).push().setValue("unseen");
                     MediaPlayer mediaPlayer=MediaPlayer.create(chatactivity.this,R.raw.sendmassage);
                     mediaPlayer.start();
                 }
             });
            }
        });

    }

    @SuppressLint("Range")
    public void checkTouch(MotionEvent event) {

        float x = event.getX();
       // Toast.makeText(this, x+"//////"+x2, Toast.LENGTH_SHORT).show();
        if (Math.abs(x1-x)>400){
            removerecord.setImageResource(R.drawable.ic_baseline_delete_24_red);
            if (event.getAction()== MotionEvent.ACTION_UP){
                MediaPlayer mediaPlayer=MediaPlayer.create(chatactivity.this,R.raw.deleterecord);
                mediaPlayer.start();
                removerecord.setImageResource(R.drawable.ic_baseline_delete_24);
                stopRecording();
            }
        }
        else {
            removerecord.setImageResource(R.drawable.ic_baseline_delete_24);
               if(event.getAction() == MotionEvent.ACTION_UP){
                if (seconds>1){
                    Toast.makeText(chatactivity.this, "recorded", Toast.LENGTH_SHORT).show();
                    uploadfile();
                }
                   stopRecording();
            }


        }



    }

}