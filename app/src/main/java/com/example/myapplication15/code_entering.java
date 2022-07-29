package com.example.myapplication15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class code_entering extends Fragment {
PinEntryEditText code;
String Credentialid;
String user_state="";
    private FirebaseAuth mAuth;
    ProgressBar loading;
    TextView pincodetxt;
    TextView resend;
    TextView timer;
    Runnable runnable;
    Handler handler;
    int second;
    int minute;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_code_entering, container, false);
        //Toast.makeText(getContext(), getArguments().getString("code"), Toast.LENGTH_SHORT).show();
        loading=view.findViewById(R.id.loading);
        pincodetxt=view.findViewById(R.id.pincodetxt);
        loading.setVisibility(View.GONE);
        resend=view.findViewById(R.id.resend);
        timer=view.findViewById(R.id.timer);
        handler=new Handler();

        settimer();
        mAuth = FirebaseAuth.getInstance();
        Credentialid=getArguments().getString("code");
       user_state=getArguments().getString("user_state");
        pincodetxt.setText("otp has been send to "+getArguments().getString("phonenumber")+"\nplease Verify it");
code=view.findViewById(R.id.txt_pin_entry);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend(getArguments().getString("phonenumber"));

            }
        });
code.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
    @Override
    public void onPinEntered(CharSequence str) {
        loading.setVisibility(View.VISIBLE);
        hideKeyboard(getActivity());
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Credentialid, code.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }
});

        return view;
    }

    private void settimer() {
        timer.setVisibility(View.VISIBLE);
        resend.setVisibility(View.GONE);
         second=80;
        CountDownTimer countDownTimer=new CountDownTimer(80000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(Integer.toString(second));
                second--;
            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.GONE);
                resend.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);


            Credentialid=s;

        }


    };
    private void resend(String phonenumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),           //a reference to an activity if this method is in a custom service
                mCallbacks);
        settimer();

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loading.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            if(user_state.equals("found_complete")){
                               Intent userdate=new Intent(getContext(),MainActivity.class);
                              register_userdata();
                                startActivity(userdate);
                                getActivity().finish();
                                loading.setVisibility(View.GONE);


                            }
                            else{
                                Bundle bundle=new Bundle();
                                bundle.putString("phonenumber",getArguments().getString("phonenumber"));
                                user_info user_info=new user_info();
                                user_info.setArguments(bundle);
                                startactivity.getInstance().addfragment(user_info,"code",false);
                                loading.setVisibility(View.GONE);

                            }


                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                code.setError("invalide code");
                                code.requestFocus();
                            }
                        }
                    }
                });

    }

    private void register_userdata() {

       data_save.getinstance(getContext());
       data_save.registeruser(getContext(),getArguments().getString("name"),getArguments().getString("phonenumber")
        ,getArguments().getString("gender"),getArguments().getString("pic_url")
        ,getArguments().getString("birthdate"),getArguments().getString("bio"),"new");
       data_save.signin();

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}