package com.example.myapplication15;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class user_info extends Fragment  {
    int myear;
    int mMonth;
    int mday;
TextView date;
ImageView profileimg;
EditText name;
Uri uri=null;
Button next;
    FirebaseFirestore db;
    ProgressBar loading;
    RadioGroup genderbtn;
    String gender="";
StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
                View view=inflater.inflate(R.layout.fragment_user_info, container, false);
                name=view.findViewById(R.id.name);
                date=view.findViewById(R.id.date);
                profileimg=view.findViewById(R.id.profileimg);
                next=view.findViewById(R.id.next2);
        // Create a Cloud Storage reference from the app
         storageRef = FirebaseStorage.getInstance().getReference();
        loading=view.findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        genderbtn=view.findViewById(R.id.gender);
        db = FirebaseFirestore.getInstance();
                getgender();
                setphoto();
        final Calendar c = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener Date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mday = c.get(Calendar.DAY_OF_MONTH);
                updateDisplay();}};

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), Date, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        myear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mday = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (uri!=null){
                uploadimg();}
            else {
                Toast.makeText(getContext(), "please select image", Toast.LENGTH_SHORT).show();
            }
            }
        });


        return view;

    }

    private void getgender() {
        genderbtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.male:
                        gender="male";
                        break;
                    case R.id.female:
                      gender="female";
                        break;

                }
            }
        });
    }

    private void register_user(Uri uri) {

        Map<String, String> user = new HashMap<>();
        user.put("phone_number", getArguments().getString("phonenumber"));
        user.put("name", name.getText().toString());
        user.put("birth_date", date.getText().toString());
        user.put("gender", gender);
        user.put("profile_picture", uri.toString());
        user.put("bio", "Available");
        user.put("nickname", "new");
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        startActivity(new Intent(getContext(),MainActivity.class));
                            register_user(uri.toString());
                        //  startactivity.getInstance().addfragment(new code_entering(),"code",true);
                        loading.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "please check your Internet connection", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                    }
                });



    }

    private void register_user(String uri) {
        data_save.getinstance(getContext());
        data_save.registeruser(getContext(),name.getText().toString(),getArguments().getString("phonenumber")
                ,gender,uri
                ,date.getText().toString(),"Available","new");
        data_save.signin();

    }

    private void uploadimg() {
        loading.setVisibility(View.VISIBLE);
        next.setVisibility(View.GONE);
        storageRef.child("images").child(getArguments().getString("phonenumber")).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        register_user(uri);
                    }


                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "please check your Internet connection", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                    }
                });
            }

        });
    }

    private void setphoto() {
        profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 100);

            }
        });
    }

    private void updateDisplay() {
        this.date.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mday).append("/")
                        .append(mMonth + 1).append("/")
                        .append(myear).append(" "));
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK && reqCode==100) {
            Uri imageuri=CropImage.getPickImageResultUri(getContext(),data);
            if(CropImage.isReadExternalStoragePermissionsRequired(getContext(),imageuri)){
                
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},0);
                uri=imageuri;
            }else{
                StartCrop(imageuri);
            }

    }
        if(reqCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

          try {
              Glide.with(getContext()).load(result.getUri()).circleCrop().into(profileimg);
              uri=result.getUri();
          }catch (Exception e){

          }

        }
         if(reqCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
             Toast.makeText(getContext(), "error in pick", Toast.LENGTH_SHORT).show();
         }

    }

    private void StartCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true).setMinCropWindowSize(1000,1000).start(getContext(),this);
    }
}