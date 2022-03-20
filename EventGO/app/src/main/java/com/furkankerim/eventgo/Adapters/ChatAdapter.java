package com.furkankerim.eventgo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furkankerim.eventgo.Models.Message;
import com.furkankerim.eventgo.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    private static final int MSG_LEFT = 1;
    private static final int MSG_RIGHT = -1;
    private ArrayList<Message> messages;
    private Context context;
    private String mUID, img;
    private Message message;
    private View v;

    public ChatAdapter(ArrayList<Message> messages, Context context, String mUID, String img) {
        this.messages = messages;
        this.context = context;
        this.mUID = mUID;
        this.img = img;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            v = LayoutInflater.from(context).inflate(R.layout.msg_item_right, parent, false);
        } else
            v = LayoutInflater.from(context).inflate(R.layout.msg_item_left, parent, false);


        return new ChatHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        message = messages.get(position);
        

        holder.img.setVisibility(View.GONE);
        holder.txtView.setText(message.getContent());

        if (!message.getSender().equals(mUID)) {
            if (img.equals("default")) {
                holder.circleImg.setImageResource(R.mipmap.ic_launcher);
            } else {
                Picasso.get().load(img).resize(60, 60).into(holder.circleImg);
            }
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImg;
        TextView txtView;
        ImageView img;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            circleImg = itemView.findViewById(R.id.chat_item_imgProfile);
            txtView = itemView.findViewById(R.id.chat_item_txtMessage);
            img = itemView.findViewById(R.id.chat_item_img);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(mUID)) {
            return MSG_RIGHT;
        } else
            return MSG_LEFT;

    }
}
