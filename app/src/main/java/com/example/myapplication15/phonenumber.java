package com.example.myapplication15;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class phonenumber extends Fragment {

Button next;
EditText phonenumber;

ProgressBar loading;
FirebaseFirestore db;
    private FirebaseAuth mAuth;
    static String code_var="";
CountryCodePicker countryCodePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_phonenumber, container, false);
       phonenumber=view.findViewById(R.id.phonenumber);
        countryCodePicker=view.findViewById(R.id.countrycode);
        mAuth = FirebaseAuth.getInstance();
       loading=view.findViewById(R.id.loading);
       db = FirebaseFirestore.getInstance();
       loading.setVisibility(View.GONE);

         next=view.findViewById(R.id.next1);
         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!countryCodePicker.getFullNumber().toString().isEmpty() &&!phonenumber.getText().toString().isEmpty()){
                     next.setVisibility(View.GONE);
                     loading.setVisibility(View.VISIBLE);
                     sendSmS("+"+countryCodePicker.getFullNumber() ,phonenumber.getText().toString());
                 }
                 else{

                     Toast.makeText(getContext(), "please enter phone number and country code", Toast.LENGTH_SHORT).show();
                 }
             }

         });

        return view;
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            checkuser(s,forceResendingToken);


        }


    };

    private void checkuser(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Boolean found=false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("phone_number").equals("+"+countryCodePicker.getFullNumber()+phonenumber.getText().toString())){
                                    if(document.getString("name")!=null && !document.getString("name").isEmpty()){
                                        Bundle bundle=new Bundle();
                                        bundle.putString("user_state","found_complete");
                                        bundle.putString("code",s);
                                        bundle.putString("forceResendingToken",forceResendingToken.toString());
                                        bundle.putString("phonenumber","+"+countryCodePicker.getFullNumber()+phonenumber.getText().toString());
                                        bundle.putString("name",document.getString("name"));
                                        bundle.putString("gender",document.getString("gender"));
                                        bundle.putString("birthdate",document.getString("birth_date"));
                                        bundle.putString("pic_url",document.getString("profile_picture"));
                                        bundle.putString("bio",document.getString("bio"));
                                        code_entering code_entering=new code_entering();
                                        code_entering.setArguments(bundle);
                                        startactivity.getInstance().addfragment(code_entering,"code",false);
                                        loading.setVisibility(View.GONE);
                                        found=true;
                                        break;
                                    }
                                    else{

                                        Bundle bundle=new Bundle();
                                        bundle.putString("user_state","found_incomplete");
                                        bundle.putString("phonenumber","+"+countryCodePicker.getFullNumber()+phonenumber.getText().toString());
                                        bundle.putString("code",s);
                                        code_entering code_entering=new code_entering();
                                        code_entering.setArguments(bundle);
                                        startactivity.getInstance().addfragment(code_entering,"code",false);
                                        loading.setVisibility(View.GONE);
                                        found=true;
                                        break;

                                    } } }

                            if(!found){
                                Bundle bundle=new Bundle();
                                bundle.putString("user_state","not_found");
                                bundle.putString("phonenumber","+"+countryCodePicker.getFullNumber()+phonenumber.getText().toString());
                                bundle.putString("code",s);
                                code_entering code_entering=new code_entering();
                                code_entering.setArguments(bundle);
                                startactivity.getInstance().addfragment(code_entering,"code",false);
                                loading.setVisibility(View.GONE);

                            }
                        }
                    }});
    }

    private void sendSmS(String Code,String phonenumber) {
        if (phonenumber.startsWith("0")){

            phonenumber=phonenumber.substring(1);
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                Code+ phonenumber,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                mCallbacks);
    }

    private void addphonenumber() {
        Map<String, String> user = new HashMap<>();
        user.put("country_code", countryCodePicker.getFullNumber());
        user.put("phone_number", phonenumber.getText().toString());

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "code has been sent", Toast.LENGTH_SHORT).show();

                      //  startactivity.getInstance().addfragment(new code_entering(),"code",true);
                        loading.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "please check your Internet connection", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}