package com.example.myapplication15;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class rcvchats extends RecyclerView.Adapter<rcvchats.holder> {
List<chatmodel> chatmodels;
Context context;
    List<chatmodel> chatmodelsfilter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    HashMap<String, String> user_data;
    public rcvchats(List<chatmodel> chatmodels, Context context) {
        user_data=data_save.getuserdata(context);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        this.chatmodels = chatmodels;
        this.context = context;
        chatmodelsfilter=chatmodels;
    }

    @NonNull
    @Override
    public rcvchats.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.chatmodel,parent,false);
        return new holder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull rcvchats.holder holder, int position) {
        if (chatmodels.get(position).getNumber_of_unseen_massage()<=0){

            holder.number_of_unseen_massages.setVisibility(View.GONE);

        }
        else {

holder.number_of_unseen_massages.setText(Integer.toString(chatmodels.get(position).getNumber_of_unseen_massage()));
        holder.number_of_unseen_massages.setVisibility(View.VISIBLE);

        }


        String[] massage = chatmodels.get(position).getBio().split("%%%12345timeStamp54321%%%");
        String[] Time=new String[2];
        if(massage.length>1){
         Time = massage[1].split("/");}
        SimpleDateFormat formatter = new SimpleDateFormat("MMMMdd, yyyy", Locale.getDefault());
        Date date = new Date();
        String time = formatter.format(date);
        if (time.equals(Time[0])){
        holder.time.setText(Time[1]);}
        else {
            holder.time.setText(Time[0]);
        }
       if (holder.number_of_unseen_massages.getVisibility()==View.VISIBLE){
           holder.time.setTextColor(Color.parseColor("#1E659E"));
       }
       else {
           holder.time.setTextColor(Color.parseColor("#ABB3B3B3"));
       }
                if (chatmodels.get(position).getBio().contains("%%%12345audio54321%%%")){
                    holder.bio.setText("Sent Audio...");

            }
       else if (chatmodels.get(position).getBio().contains("%%%12345photo54321%%%")){
            holder.bio.setText("sent photo...");

        }
            else {

              holder.bio.setText(massage[0]);
                    }
                 if(chatmodels.get(position).getType()){
                  holder.name.setText(chatmodels.get(position).getName());
                     Glide.with(context).load(Uri.parse(chatmodels.get(position).img)).circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.img);

                       }
                 else{

                    }

holder.img.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(chatmodels.get(position).getType()){
        Intent intent=new Intent(context,shadow_profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,holder.img, ViewCompat.getTransitionName(holder.img));
           intent.putExtra("phonenumber",chatmodels.get(position).getPhonenumber());
        intent.putExtra("pic",chatmodels.get(position).getImg());
        context.startActivity(intent,optionsCompat.toBundle());

    }}
});
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(context,chatactivity.class);
                    intent.putExtra("phonenumber",chatmodels.get(position).getPhonenumber());
                    intent.putExtra("name",chatmodels.get(position).getName());
                    intent.putExtra("pic",chatmodels.get(position).getImg());
                    intent.putExtra("type",chatmodels.get(position).getType());
                    context.startActivity(intent);
                    holder.number_of_unseen_massages.setVisibility(View.GONE);
                    holder.time.setTextColor(Color.parseColor("#ABB3B3B3"));

                }
            });

    }



    @Override
    public int getItemCount() {
        return chatmodelsfilter.size();
    }

class holder extends RecyclerView.ViewHolder {
    ImageView img;
    TextView name;
    TextView bio;
    TextView number_of_unseen_massages;
    TextView time;
    public holder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.personname);
        img=itemView.findViewById(R.id.personphoto);
        bio=itemView.findViewById(R.id.personbio);
        time=itemView.findViewById(R.id.time);
        number_of_unseen_massages=itemView.findViewById(R.id.number_of_unseen_massages);
    }
}
    public Filter getfilter(){

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String key=constraint.toString();
                if(key.isEmpty()){
                    chatmodelsfilter=chatmodels;
                }else {
                    ArrayList<chatmodel> isfiltered=new ArrayList<>();
                    for (chatmodel chatmodel:chatmodels){
                        if (chatmodel.getType()){
                        if(chatmodel.getName().toLowerCase().contains(key.toLowerCase())){
                        isfiltered.add(chatmodel);

                        }}}
                    chatmodelsfilter=isfiltered;

                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=chatmodelsfilter;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                chatmodelsfilter= (ArrayList<chatmodel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
