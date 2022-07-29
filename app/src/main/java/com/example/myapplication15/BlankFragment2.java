package com.example.myapplication15;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class BlankFragment2 extends Fragment {
    FirebaseDatabase database;
    DatabaseReference myRef;
    data_save data_save;
    HashMap<String, String> user_data;
    rcvchats chatsadpt;
    Runnable timerRunnable;
    Handler timerHandler;
    RecyclerView chats_recyclerview;
    SwipeRefreshLayout swipeContainer;
    ValueEventListener ref;
    RecyclerViewSkeletonScreen skeletonScreen;
    EditText search;
    CharSequence searchtxt="";
    ArrayList<chatmodel>  chats;
    database chatofflinedata;
    List<chatmodel> chatmodels;
    database1 databaseoffline;
    ValueEventListener shadowstatelistner;
    ValueEventListener unseenmassagelistener;
    Task<QuerySnapshot> infolistener;
    Boolean massagesent=false;
    Boolean retval ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_blank2, container, false);
        swipeContainer=view.findViewById(R.id.swipeContainer);
        data_save.getinstance(getContext());
        user_data=data_save.getuserdata(getContext());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        search=view.findViewById(R.id.search);
        chatofflinedata= com.example.myapplication15.database.getinsance(getContext());
        chats_recyclerview=view.findViewById(R.id.chats);
        databaseoffline=com.example.myapplication15.database1.getinsance(getContext());
        chats=new ArrayList<>();


            getchatsoffline();


search.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        chatsadpt.getfilter().filter(s);
        searchtxt=s;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
});

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
               getchats();

            }
        });

        return view;
    }



    private void getchatsoffline() {
     /*   skeletonScreen = Skeleton.bind(chats_recyclerview)
                .adapter(chatsadpt)
                .load(R.layout.loading_layout)
                .show();*/

        chatofflinedata.chatsduo().getchats().subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<chatmodel>>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {


            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull List<chatmodel> chatmodels) {
                BlankFragment2.this.chatmodels=chatmodels;

                chatsadpt=new rcvchats(BlankFragment2.this.chatmodels,getContext());
                chats_recyclerview.setAdapter(chatsadpt);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                chats_recyclerview.setLayoutManager(linearLayoutManager);
                chats_recyclerview.setHasFixedSize(true);

                chatsadpt.notifyDataSetChanged();
               // skeletonScreen.hide();
                getchats();



            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void getchats() {
        swipeContainer.setEnabled(false);
        if (ref!=null){

            myRef.child("messages").child(user_data.get("phone")).removeEventListener(ref);

        }

         ref = myRef.child("messages").child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                 for (DataSnapshot snap : snapshot.getChildren()) {
                     number_of_unseen_massage(snap);


                 }}

                    if (!snapshot.hasChildren()){
                        chatsadpt.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                        swipeContainer.setEnabled(true);
                     //   skeletonScreen.hide();

                    }

             }


             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }

         }

        );
    }

    private void number_of_unseen_massage(DataSnapshot snap) {
     myRef.child("messagesUnseen").child(user_data.get("phone")).child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        x++;
                    }

                }
                lastoflinemassage(snap, x);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getchat(DataSnapshot snap, String lastmassage, int number_of_unseen_massages) {

        if (shadowstatelistner!=null){
            myRef.child("shadow_state").child(user_data.get("phone")).child(snap.getKey()).removeEventListener(shadowstatelistner);

        }
     shadowstatelistner=myRef.child("shadow_state").child(user_data.get("phone")).child(snap.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                if (snapshot.getValue().equals("true")){
                    getchatdata(snap,lastmassage,number_of_unseen_massages,true);



                }
                else{
                    getchatdata(snap,lastmassage,number_of_unseen_massages,false);


                }}
                else
                {
                    getchatdata(snap, lastmassage,number_of_unseen_massages, false);
                    chatsadpt.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    swipeContainer.setEnabled(true);
                    //skeletonScreen.hide();
                }
                myRef.child("shadow_state").child(user_data.get("phone")).child(snap.getKey()).removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void lastoflinemassage(DataSnapshot snap, int number_of_unseen_massages) {


        myRef.child("messages").child(user_data.get("phone")).child(snap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lastmassage="";
                    if (snapshot.exists()){
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    lastmassage="";
                    String[] massage=snapshot1.getValue().toString().split("=");
                    if (massage!=null && massage.length>1){

                        for (int i=1 ; i<massage.length;i++){
                           lastmassage+=massage[i];
                        }
                      lastmassage=lastmassage.substring(0,lastmassage.length()-1);
                }
                }

                getchat(snap, lastmassage,number_of_unseen_massages);
            }
            else{
                        getchat(snap, "New Shadow%%%12345timeStamp54321%%% ", number_of_unseen_massages);
            }

                myRef.child("messages").child(user_data.get("phone")).child(snap.getKey()).removeEventListener(this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        }

        );

    }

    private void getchatdata(DataSnapshot snap, String lastmassage, int number_of_unseen_massages, Boolean shadow_state) {


        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

         infolistener = db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("phone_number").equals(snap.getKey())) {
                                    //   chats.add(new chatmodel(snap.getKey(),shadow_state,document.getString("profile_picture"),document.getString("name"),document.getString("bio")));
                                     retval = false;
                                    for (int i = 0; i < chatmodels.size(); i++) {
                                        if (chatmodels.get(i).getPhonenumber().equals(snap.getKey())) {

                                            if (document.getString("profile_picture").equals(chatmodels.get(i).getImg()) && document.getString("name").equals(chatmodels.get(i).getName())
                                                    && shadow_state.equals(chatmodels.get(i).getType()) && number_of_unseen_massages==chatmodels.get(i).getNumber_of_unseen_massage() && lastmassage.equals(chatmodels.get(i).getBio())) {

                                                retval = true;


                                            } else {
                                                if (i==chatmodels.size()-1){
                                                        retval = true;
                                                    chatofflinedata.chatsduo().delete_using_phone(snap.getKey())
                                                            .subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
                                                        @Override
                                                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {


                                                        }

                                                        @Override
                                                        public void onComplete() {
                    chatofflinedata.chatsduo().insert(new chatmodel(snap.getKey(), shadow_state, document.getString("profile_picture"), document.getString("name"), lastmassage, number_of_unseen_massages)).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
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

                                                        @Override
                                                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                                        }
                                                    });


                                                        }
                                                        else{
                                                chatofflinedata.chatsduo().delete(chatmodels.get(i)).subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
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
                                            }}
                                        }
                                    }
                                    if (!retval) {
                                        chatofflinedata.chatsduo().insert(new chatmodel(snap.getKey(), shadow_state, document.getString("profile_picture"), document.getString("name"), lastmassage, number_of_unseen_massages))
                                                .subscribeOn(Schedulers.computation()).subscribe(new CompletableObserver() {
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
                                }
                            }
                        } else {

                        }

                        swipeContainer.setRefreshing(false);
                        swipeContainer.setEnabled(true);
                    }
                });

    }






}