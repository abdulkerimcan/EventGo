package com.furkankerim.eventgo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.sax.TextElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Activities.ChatActivity;
import com.furkankerim.eventgo.Activities.DetailsActivity;
import com.furkankerim.eventgo.Models.Chat;
import com.furkankerim.eventgo.Models.User;
import com.furkankerim.eventgo.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder> {
    private ArrayList<Chat> userArrayList;
    private Context context;
    private View view;
    private Chat chat;
    private FirebaseFirestore mFirestore;


    public UsersAdapter(ArrayList<Chat> userArrayList, Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
        mFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.users_item,parent,false);
        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        chat = userArrayList.get(position);
        holder.username.setText(chat.getUsername());
        Picasso.get().load(chat.getUserImg()).resize(66,66).into(holder.circleImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChat = new Intent(context,ChatActivity.class);
                Chat newChat = userArrayList.get(position);
                toChat.putExtra("hedefID",newChat.getUserID());
                toChat.putExtra("channelID",newChat.getChannelID());
                toChat.putExtra("hedefUrl",newChat.getUserImg());
                toChat.putExtra("hedefUsername",newChat.getUsername());
                context.startActivity(toChat);
            }
        });

            mFirestore.collection("ChatChannels").document(chat.getChannelID()).collection("Messages")
                    .whereEqualTo("sender",chat.getUserID())
                    .orderBy("time", Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                //Burda hata var
                            }else{
                                for (DocumentSnapshot ds : value.getDocuments()) {
                                    String msg = (String) ds.get("content");
                                    holder.lastMessage.setText(msg);
                                }
                            }
                        }
                    });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class UsersHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView username,lastMessage;

        public UsersHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.messages_item_circle_user_img);
            username = itemView.findViewById(R.id.messages_item_user_name);
            lastMessage = itemView.findViewById(R.id.messages_item_last_message);
        }
    }
}
