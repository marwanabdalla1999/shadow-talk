package com.example.myapplication15;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BlankFragment extends Fragment implements LocationHelper.OnLocationReceived {
    CardView matching;
    TextView txtmatching;
    TextView txttime;
    TextView cancel;
    int seconds=0,minutes=0;
    Runnable timerRunnable;
    Handler timerHandler;
    FirebaseDatabase database;
    DatabaseReference myRef;
    HashMap<String, String> user_data;
    data_save data_save;
    LocationManager locationManager;
    final int LOCATION_SETTINGS_REQUEST=101;
    Location location;
    boolean getlocation=false;
    private LocationHelper locHelper;
    ValueEventListener listener;
    double min_distance;
    String phone="";
    Boolean found,found1;
    ArrayList<String> matches;
    ArrayList<String> blocklist;
    RelativeLayout loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_blank, container, false);
        // Inflate the layout for this fragment
        loading=view.findViewById(R.id.loadingblockandmatches);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        data_save.getinstance(getContext());
        user_data=data_save.getuserdata(getContext());
        matches=new ArrayList<>();
        blocklist=new ArrayList<>();
        matching=view.findViewById(R.id.matching);
        txtmatching=view.findViewById(R.id.txtmatch);
        txttime=view.findViewById(R.id.time);
        cancel=view.findViewById(R.id.cancel);
        SpannableString content = new SpannableString("Cancel");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
cancel.setText(content);

        getmatched();
        getblocklist();
        // getlocation();
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        matching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locHelper = new LocationHelper(getContext());
                    locHelper.setLocationReceivedLister(BlankFragment.this);

                    getlocation = false;
                    txttime.setVisibility(View.VISIBLE);

                    txtmatching.setText("Matching.");
                    matching.setEnabled(false);
                    timerHandler = new Handler();
                    timerRunnable = new Runnable() {

                        @Override
                        public void run() {
                            if (txtmatching.getText().equals("Matching.")){
                                txtmatching.setText("Matching..");
                            }
                           else if (txtmatching.getText().equals("Matching..")){
                                txtmatching.setText("Matching...");
                            }
                            else if (txtmatching.getText().equals("Matching...")){
                                txtmatching.setText("Matching.");
                            }
                            seconds = seconds + 1;
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

                            timerHandler.postDelayed(this, 1000);

                        }
                    };
                    timerRunnable.run();

                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    //  Toast.makeText(getContext(), location.getLatitude()+"//"+ location.getLongitude(), Toast.LENGTH_SHORT).show();
                    if (location != null) {

                    } else {
                        getlcoationservice();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtmatching.setText("Find Match");
                txttime.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                matching.setEnabled(true);
                timerHandler.removeCallbacks(timerRunnable);
                seconds=0;
                minutes=0;
                myRef.child("matching").child(user_data.get("phone")).setValue("false");
                if(listener!=null){
                myRef.child("matching").removeEventListener(listener);
                }
                locHelper.setLocationReceivedLister(null);

            }
        });

        return view;
    }

    private void getlocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        } else {
try {
    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

}catch (Exception e){

}
            //  Toast.makeText(getContext(), location.getLatitude()+"//"+ location.getLongitude(), Toast.LENGTH_SHORT).show();
            if(location!=null){
            }
            else{
                getlcoationservice();
            }
        }
    }

    private void matching(Location location) {
        myRef.child("matching").child(user_data.get("phone")).setValue(location.getLatitude()+"//"+location.getLongitude());
      listener=  myRef.child("matching").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                min_distance=Double.POSITIVE_INFINITY;
                phone="";
                for (DataSnapshot snap :snapshot.getChildren()){
                    if(!snap.getKey().equals(user_data.get("phone"))){
                        if(!snap.getValue().equals("false")){
                            Boolean ismatched=false;
                            Boolean isblocked=false;
                            for(int i=0; i<matches.size();i++){
                                if (snap.getKey().equals(matches.get(i))){
                                        ismatched=true;

                            }}
                            for(int j=0; j<blocklist.size();j++){
                                if (snap.getKey().equals(blocklist.get(j))){
                                    isblocked=true;

                                }}
                            if(!isblocked && !ismatched){
                                    String[] latlong=snap.getValue().toString().split("//");
                                    double min_distance1= distance(location.getLatitude(),location.getLongitude(),Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1]));
                                    if(min_distance>min_distance1){
                                        min_distance=min_distance1;
                                        phone=snap.getKey();
                                    }

                                }}}}
                        if(!phone.isEmpty()&&!phone.equals("") && min_distance>=0) {
                            Intent intent=new Intent(getContext(), chatactivity.class);
                            intent.putExtra("phonenumber", phone);
                            // intent.putExtra("distance", min_distance);
                            startActivity(intent);
                            txtmatching.setText("Find Match");
                            txttime.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            matching.setEnabled(true);
                            timerHandler.removeCallbacks(timerRunnable);
                            seconds = 0;
                            minutes = 0;
                            myRef.child("matching").child(user_data.get("phone")).setValue("false");
                            myRef.child("matching").removeEventListener(listener);



                    }




                        }





            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getmatched() {

        matching.setEnabled(false);
                myRef.child("messages").child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loading.setVisibility(View.VISIBLE);

                        matches.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                                matches.add(data.getKey());


                        }
                        loading.setVisibility(View.GONE);
                        matching.setEnabled(true);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getblocklist() {

        matching.setEnabled(false);
        myRef.child("blocklist").child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loading.setVisibility(View.VISIBLE);


                blocklist.clear();
                for (DataSnapshot data:snapshot.getChildren()){
                            blocklist.add(data.getValue().toString());
                }
                loading.setVisibility(View.GONE);
                matching.setEnabled(true);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        myRef.child("matching").child(user_data.get("phone")).setValue("false");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                 location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                //locHelper.setLocationReceivedLister(BlankFragment.this);

                if(location!=null) {

                }
                else {
                    getlcoationservice();

                }

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
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext())
                .checkLocationSettings(settingsBuilder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);


                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(getActivity(),
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
        if(!getlocation) {
            cancel.setVisibility(View.VISIBLE);
            matching(location);
        getlocation=true;
        }
    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515*100;
        int d=(int)dist;
        double distance=d/100.00;
        return (distance);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}