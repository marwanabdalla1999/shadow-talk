package com.example.myapplication15;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.example.myapplication15.Location.LocationHelper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class splash_screen extends AppCompatActivity implements LocationHelper.OnLocationReceived {
    FirebaseDatabase database;
    DatabaseReference myRef;
    data_save data_save;
    ConstraintLayout background;
    HashMap<String, String> user_data;
    final int LOCATION_SETTINGS_REQUEST=101;
    private LocationHelper locHelper;
    Boolean success=false;
    static ArrayList<chatmodel> chats;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        locHelper = new LocationHelper(this);
        locHelper.setLocationReceivedLister(splash_screen.this);
        chats=new ArrayList<>();
        database = FirebaseDatabase.getInstance();
            init_notifcation();
        myRef = database.getReference();
        data_save.getinstance(this);
        user_data=data_save.getuserdata(this);
        background=findViewById(R.id.background);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

            }
        }, 20000);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            getlcoationservice();
        }
        else{
            getlcoationservice();

        }


    }

    private void init_notifcation() {
       /* FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);

                        Toast.makeText(splash_screen.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getlcoationservice();
                locHelper = new LocationHelper(this);
                locHelper.setLocationReceivedLister(splash_screen.this);


            }

        }
    }
    private void getlcoationservice() {
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);

                                if(!task.isSuccessful()){
                                    getlcoationservice();
                                }
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(splash_screen.this,
                                                LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {
        if (data_save.getsignstate()) {
            if(!success){

            Intent i = new Intent(splash_screen.this, MainActivity.class);
            startActivity(i);
            finish();
            success=true;
            }
        } else {
            if(!success) {

                Intent i = new Intent(splash_screen.this, startactivity.class);
                startActivity(i);
                // close this activity
                finish();
           success=true;
            }
        }
    }

    private void getchat(DataSnapshot snap) {
        myRef.child("shadow_state").child(user_data.get("phone")).child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue().equals("true")){
                    getchatdata(snap,true);


                }
                else{
                    getchatdata(snap,false);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getchatdata(DataSnapshot snap,Boolean shadow_state) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals(snap.getKey())){
                                  // chats.add(new chatmodel(snap.getKey(),shadow_state,document.getString("profile_picture"),document.getString("name"),document.getString("bio")));

                                }
                            }
                        } else {

                        }
                    }
                });

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

    }
    private void getchats() {

        myRef.child("messages").child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot snap: snapshot.getChildren()){
                    getchat(snap);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}