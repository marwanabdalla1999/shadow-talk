package com.example.myapplication15;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class recycleviewadpt extends RecyclerView.Adapter<recycleviewadpt.holder> {

Context mcontext;
List<massagemodel> massage;
    data_save data_save;
    HashMap<String, String> user_data;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseFirestore db;
    String userphone="";
    int secondsOfRecord , minutesOfRecords;
    MediaPlayer mediaPlayerholder;
   recycleviewadpt.holder holder1;
    recycleviewadpt.holder holdertemp;


Handler handler1;

Runnable runnable1;
    Boolean Shadowtype;
    holder dateholder;
    String shadow_pic;
    holder temp1;

    public recycleviewadpt(Context mcontext, List<massagemodel> massage, String userphone,Boolean Shadowtype,String shadow_pic) {
        data_save.getinstance(mcontext);
        user_data=data_save.getuserdata(mcontext);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        db = FirebaseFirestore.getInstance();
        this.mcontext = mcontext;
        this.massage = massage;
        this.userphone=userphone;
        this.Shadowtype=Shadowtype;
        this.shadow_pic=shadow_pic;

handler1=new Handler();
    }

    @Override
    public int getItemViewType(int position) {
        if (massage.get(position).type==1) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public recycleviewadpt.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=null;

        switch (viewType) {
            case 1:
                 view = LayoutInflater.from(mcontext).inflate(R.layout.sendmassage,parent,false);

                break;
            case 2:
                 view = LayoutInflater.from(mcontext).inflate(R.layout.recievemassage,parent,false);
                break;
        }
        return new holder(view);
    }
        void getseen(holder holder,int postion){
             myRef.child("messagesUnseen").child(userphone).child(user_data.get("phone")).addValueEventListener(new ValueEventListener() {
                  @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                       int x=0;
                      if (snapshot.exists()) {
                          for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                              x++;
                          }
                      }
                      if (postion<massage.size()-x && massage.get(postion).getType()==1){

                       holder.seen.setVisibility(View.VISIBLE);
                          holder.seen2.setVisibility(View.VISIBLE);
                          holder.seen3.setVisibility(View.VISIBLE);

                    }
                    else {
                        holder.seen.setVisibility(View.INVISIBLE);
                          holder.seen2.setVisibility(View.INVISIBLE);
                          holder.seen3.setVisibility(View.INVISIBLE);
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
}
    @Override
    public void onBindViewHolder(@NonNull recycleviewadpt.holder holder, int position) {
        getseen(holder,position);
        String[] massagetxt=massage.get(position).getMassage().split("%%%12345timeStamp54321%%%");
        String[] timetxt=new String[2];
        if (massagetxt.length>1){
           timetxt=massagetxt[1].split("/");}
        holder.time_rec.setText(timetxt[1]);
       holder.time_mass.setText(timetxt[1]);
        holder.time_photo.setText(timetxt[1]);
        SimpleDateFormat formatter = new SimpleDateFormat("MMMMdd, yyyy", Locale.getDefault());
        Date date = new Date();
        String time = formatter.format(date);
     //   String[] day=time.split(",");
       // String[] day1=timetxt[0].split("/");
        if (time.equals(timetxt[0])){
            holder.date.setText("Today"+" "+timetxt[0]);
        }

        else {
        holder.date.setText(timetxt[0]);}
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.date.getVisibility() == View.VISIBLE) {
                    holder.date.setVisibility(View.GONE);

                } else {
                    holder.date.setVisibility(View.VISIBLE);
                    if (dateholder != null) {
                        if (dateholder.itemView != holder.itemView) {
                            dateholder.date.setVisibility(View.GONE);
                        }
                    }
                    dateholder = holder;
                }
            return false;
            }
        } );


            if (massage.get(position).getMassage().contains("audio")){


                String[] url=massagetxt[0].split("%%%12345audio54321%%%");

                holder.massagelayout.setVisibility(View.GONE);
                holder.photolayout.setVisibility(View.GONE);
                holder.audiolayout.setVisibility(View.VISIBLE);
                holder.loading.setVisibility(View.VISIBLE);
                holder.play.setVisibility(View.GONE);
                holder.pause.setVisibility(View.GONE);
                MediaPlayer mediaPlayer=new MediaPlayer();

                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        try {

                            mediaPlayer.setDataSource(mcontext, Uri.parse(url[1]));
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                };
                asyncTask.execute();

                if (mediaPlayer!=null){
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            holder.loading.setVisibility(View.GONE);
                            holder.play.setVisibility(View.VISIBLE);
                            holder.pause.setVisibility(View.GONE);
                            int duration = mp.getDuration() / (1000);
                            String durationstr = getduration(duration,mp);
                            holder.duration.setText(durationstr);
                            holder.seekBar.setMax(mp.getDuration());
                            holder.play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                            if (fromUser) {
                                                mp.seekTo(progress);
                                                holder.seekBar.setProgress(progress);
                                            }
                                        }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                        }
                                    });
                                    mp.start();
                                    if (holder1!=null){
                                        if (holder.getAdapterPosition() != holder1.getAdapterPosition()){

                                            handler1.removeCallbacks(runnable1);
                                            holder1.seekBar.setProgress(0);

                                            holder1.play.setVisibility(View.VISIBLE);
                                            holder1.pause.setVisibility(View.GONE);
                                            holder1.loading.setVisibility(View.GONE);
                                            holder1.recordtime.setText("0 : 00");
                                            secondsOfRecord=0;
                                            minutesOfRecords=0;
                                            if (mediaPlayerholder!=null){
                                                mediaPlayerholder.pause();
                                                mediaPlayerholder.seekTo(0);
                                            }

                                        }
                                    }
                                    mediaPlayerholder=mp;
                                    holder1=holder;
                                    holder.play.setVisibility(View.GONE);
                                    holder.loading.setVisibility(View.GONE);
                                    holder.pause.setVisibility(View.VISIBLE);


                                    runnable1 = new Runnable() {

                                        @Override
                                        public void run() {
                                            holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());

                                            String durationstr = getduration(mp.getCurrentPosition()/1000,mp);
                                            holder.recordtime.setText(durationstr);



                                            handler1.postDelayed(runnable1, 50);
                                        }
                                    };
                                    runnable1.run();

                                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            if (mp!=null) {

                                                handler1.removeCallbacks(runnable1);
                                                holder.seekBar.setProgress(0);

                                                holder.play.setVisibility(View.VISIBLE);
                                                holder.pause.setVisibility(View.GONE);
                                                holder.loading.setVisibility(View.GONE);
                                                holder.recordtime.setText("0 : 00");
                                                  //  mp.stop();
                                               // mp.release();


                                            }
                                        }
                                    });
                                    holder.pause.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (mediaPlayer!=null){
                                                mediaPlayer.pause();
                                                handler1.removeCallbacks(runnable1);
                                                holder.play.setVisibility(View.VISIBLE);
                                                holder.pause.setVisibility(View.GONE);
                                                holder.loading.setVisibility(View.GONE);

                                            }


                                        }
                                    });
                                }


                            });




                        }
                    });
                }

                /*    holder.massagelayout.setVisibility(View.GONE);
                holder.photolayout.setVisibility(View.GONE);
                holder.audiolayout.setVisibility(View.VISIBLE);
                    holder.play.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                           // temp1=holder;
                            /*if (holdertemp!=null && mediaPlayer!=null && holdertemp.getAdapterPosition()==holder.getAdapterPosition()){
                                if (!mediaPlayer.isPlaying()){
                                    mediaPlayer.start();
                                    holder.play.setVisibility(View.GONE);
                                    holder.loading.setVisibility(View.GONE);
                                    holder.pause.setVisibility(View.VISIBLE);
                                    if (handler!=null){
                                        handler.postDelayed(runnable,1000);
                                        handler1.postDelayed(runnable1,100);
                                    }
                                }

                            }
                          //  else {
                         /*   if (holdertemp != null) {
                                holdertemp.play.setVisibility(View.VISIBLE);
                                holdertemp.pause.setVisibility(View.GONE);
                                holdertemp.loading.setVisibility(View.GONE);
                                holdertemp.seekBar.setProgress(0);
                                holdertemp.recordtime.setText("0 : 00");
                                holdertemp.recordtime.setVisibility(View.GONE);
                            }

                          //  holdertemp = holder;
                            holder.play.setVisibility(View.GONE);
                            holder.loading.setVisibility(View.VISIBLE);
                            holder.pause.setVisibility(View.GONE);
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    mediaPlayer = null;

                                }
                            }

                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                           /* if (runnable != null) {
                                handler.removeCallbacks(runnable);
                            }
                            try {


                                AsyncTask asyncTask = new AsyncTask() {
                                    @Override
                                    protected Object doInBackground(Object[] objects) {
                                        try {
                                            mediaPlayer.setDataSource(mcontext, Uri.parse(url[1]));
                                            mediaPlayer.prepare();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        return null;
                                    }
                                };
                                asyncTask.execute();

                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaPlayer.start();
                                        minutesOfRecords = 0;
                                        secondsOfRecord = 0;
                                        //getrecordstate(holder);
                                        holder.play.setVisibility(View.GONE);
                                        holder.loading.setVisibility(View.GONE);
                                        holder.pause.setVisibility(View.VISIBLE);
                                        holder.recordtime.setVisibility(View.VISIBLE);
                                                    int second=0;
                                        int duration = mediaPlayer.getDuration() / (1000);
                                        String durationstr = getduration(duration);
                                        holder.duration.setText(durationstr);
                                        holder.seekBar.setMax(mediaPlayer.getDuration());
                                     /*   runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                                if (secondsOfRecord < 10) {
                                                    holder.recordtime.setText(minutesOfRecords + " : " + "0" + secondsOfRecord);
                                                }
                                                else {
                                                    holder.recordtime.setText(minutesOfRecords + " : " + secondsOfRecord);
                                                }

                                                secondsOfRecord++;
                                                if (secondsOfRecord >= 60) {
                                                    secondsOfRecord = 0;
                                                    minutesOfRecords++;
                                                }

                                                handler.postDelayed(runnable, 1000);
                                            }
                                        };
                                        runnable.run();
                                    }
                                });


                            } catch (Exception e) {
                            }


                      //  }

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {

                                  /*  handler.removeCallbacks(runnable);
                                    handler1.removeCallbacks(runnable1);
                                    holder.seekBar.setProgress(0);

                                    holder.play.setVisibility(View.VISIBLE);
                                    holder.pause.setVisibility(View.GONE);
                                    holder.loading.setVisibility(View.GONE);
                                    holder.recordtime.setText("0 : 00");
                                    holdertemp.recordtime.setVisibility(View.GONE);
                                    mediaPlayer=null;


                                }
                     //       });

                        }

                    });
/*if (mediaPlayer!=null){
    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (handler!=null){
            handler.removeCallbacks(runnable);}
            if (handler1!=null){
                handler1.removeCallbacks(runnable1);
            }

            holder.seekBar.setProgress(0);
            holder.play.setVisibility(View.VISIBLE);
            holder.pause.setVisibility(View.GONE);
            holder.loading.setVisibility(View.GONE);
            holder.recordtime.setText("0 : 00");
            holder.recordtime.setVisibility(View.GONE);

            mediaPlayer=null;


        }
    });
}
                    holder.pause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mediaPlayer!=null){
                                mediaPlayer.pause();
                               // mediaPlayer.release();
                                handler.removeCallbacks(runnable);
                                handler1.removeCallbacks(runnable1);
                               // holder.seekBar.setProgress(0);
                              //  mediaPlayer=null;
                                holder.play.setVisibility(View.VISIBLE);
                                holder.pause.setVisibility(View.GONE);
                                holder.loading.setVisibility(View.GONE);
                                //holder.recordtime.setText("0 : 00");
                               // holder.recordtime.setVisibility(View.GONE);
                            }

                        }
                    });*/


            }

             else if(massage.get(position).getMassage().contains("photo")){
                holder.massagelayout.setVisibility(View.GONE);
                holder.audiolayout.setVisibility(View.GONE);
                holder.photolayout.setVisibility(View.VISIBLE);

                String[] url=massagetxt[0].split("%%%12345photo54321%%%");
                Glide.with(mcontext).asBitmap().load(Uri.parse(url[1])).fitCenter().into(holder.photo);


holder.photo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(mcontext,image_viewer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mcontext,holder.photo, ViewCompat.getTransitionName(holder.photo));

        intent.putExtra("pic",url[1]);
        mcontext.startActivity(intent,optionsCompat.toBundle());
    }
});

            }
            else{

                holder.massagelayout.setVisibility(View.VISIBLE);
                holder.audiolayout.setVisibility(View.GONE);
                holder.photolayout.setVisibility(View.GONE);
        holder.massage.setText(massagetxt[0]);
            }

        if(massage.get(position).getType()==1){

           // Glide.with(mcontext).load(Uri.parse(user_data.get("Uri_pic"))).circleCrop().into(holder.img);

        }
        else if(massage.get(position).getType()==2){
            try {
                if (massage!=null && massage.size()>0) {
                    if (Shadowtype){

                   // Glide.with(mcontext).asBitmap().load(Uri.parse(shadow_pic)).circleCrop().into(holder.img);

                    }}
            }
            catch (Exception e){}
        }


    }

   /* private void getrecordstate(holder holder) {
      //  holder.play.setVisibility(temp1.play.getVisibility());
       // holder.loading.setVisibility(temp1.loading.getVisibility());
       // holder.pause.setVisibility(temp1.pause.getVisibility());
      //  holder.recordtime.setVisibility(temp1.recordtime.getVisibility());



/*if (mediaPlayer.isPlaying()) {
    int duration = mediaPlayer.getDuration() / (1000);
    String durationstr = getduration(duration);
    holder.duration.setText(durationstr);
    if (handler1 != null) {
        handler1.removeCallbacks(runnable1);
    }
    holder.seekBar.setMax(mediaPlayer.getDuration());
    runnable1 = new Runnable() {
        @Override
        public void run() {
            holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());

            if (secondsOfRecord < 10) {
                holder.recordtime.setText(minutesOfRecords + " : " + "0" + secondsOfRecord);
            } else {
                holder.recordtime.setText(minutesOfRecords + " : " + secondsOfRecord);
            }


            handler1.postDelayed(runnable1, 100);
        }
    };
    runnable1.run();

}
    }*/

    private String getduration(int duration,MediaPlayer mediaPlayer) {
        if (duration % 1000 > 500) {
            duration++;
        }
        int minute = 0;
        int seconds = duration;
        String durationstr = "";
        if (duration >= 60) {
            minute = duration / 60;
            seconds = (duration % 60) * 60;
        }

        if (seconds < 10) {
            durationstr = minute + " : " + "0" + seconds;
        } else {
            durationstr = minute + " : " + seconds;
        }
        return  durationstr;
    }

    @Override
    public int getItemCount() {
        return massage.size();
    }


    public class holder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView massage;
        ConstraintLayout massagelayout;
        ImageView play;
        SeekBar seekBar;
        LinearLayout audiolayout;
        ImageView pause;
        ProgressBar loading;
        TextView duration;
        TextView recordtime;
        ImageView seen;
        ImageView seen2;
        ImageView seen3;
        TextView time_rec;
        TextView time_mass;
        TextView time_photo;
        TextView date;
        RelativeLayout photolayout;
        ImageView photo;

        public holder(@NonNull View itemView) {
            super(itemView);
            massage=itemView.findViewById(R.id.mymassage);
            massagelayout=itemView.findViewById(R.id.mymassagelayout);
            img=itemView.findViewById(R.id.myphoto);
            play=itemView.findViewById(R.id.play);
            seekBar=itemView.findViewById(R.id.seekBar);
            audiolayout=itemView.findViewById(R.id.audiolayout);
            loading=itemView.findViewById(R.id.loading);
            pause=itemView.findViewById(R.id.pause);
            duration=itemView.findViewById(R.id.duration);
            recordtime=itemView.findViewById(R.id.recordtime);
            seen=itemView.findViewById(R.id.seen);
            time_rec=itemView.findViewById(R.id.time2);
            time_mass=itemView.findViewById(R.id.time3);
            date=itemView.findViewById(R.id.date);
            seen2=itemView.findViewById(R.id.seen2);
            photolayout=itemView.findViewById(R.id.photolayout);
            photo=itemView.findViewById(R.id.photo);
            time_photo=itemView.findViewById(R.id.time4);
            seen3=itemView.findViewById(R.id.seen3);

        }

    }
    public void release(){
       /* if (mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;*/
}
    }

   /* @Override
    public void onViewRecycled(@NonNull holder holder) {
        super.onViewRecycled(holder);
        if (temp1!=null){
        if (temp1.getAdapterPosition()==holder.getAdapterPosition()){
        temp1=holder;}}
    }*/





